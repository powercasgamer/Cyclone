/*
 * This file is part of Cyclone, licensed under the MIT License.
 *
 *  Copyright (c) 2022 powercas_gamer
 *  Copyright (c) 2022 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.deltapvp.cyclone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.command.CommandSender;
import cloud.commandframework.CommandTree;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.minecraft.extras.MinecraftHelp.HelpColors;
import cloud.commandframework.paper.PaperCommandManager;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.deltapvp.cyclone.command.api.BaseCommand;
import net.deltapvp.cyclone.command.impl.HelpCommand;
import net.deltapvp.cyclone.command.impl.ListCommand;
import net.deltapvp.cyclone.module.api.Module;
import net.deltapvp.cyclone.module.impl.CommandModule;
import net.deltapvp.cyclone.module.impl.MessageModule;
import net.deltapvp.cyclone.util.TextUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public final class Cyclone extends ExtendedJavaPlugin {
    private static Cyclone INSTANCE;
    private Map<String, Module> modules = new HashMap<>(2);
    private Collection<BaseCommand> commands = new ArrayList<>(2);
    // cloud
    private BukkitCommandManager<CommandSender> commandManager;
    private BukkitAudiences bukkitAudiences;
    private MinecraftHelp<CommandSender> minecraftHelp;

    @Override
    public void load() {
        INSTANCE = this;
        saveDefaultConfig();
        PacketEvents.create(this);
        PacketEventsSettings settings = PacketEvents.get().getSettings();
        settings
                .fallbackServerVersion(ServerVersion.v_1_8_8)
                .compatInjector(false)
                .checkForUpdates(false)
                .bStats(true);
        PacketEvents.get().loadAsyncNewThread();
        setupModules();
        modules.values().stream().filter(Module::isEnabled).forEach(Module::onLoad);
    }

    @Override
    public void enable() {
        PacketEvents.get().init();
        modules.values().stream().filter(Module::isEnabled).forEach(Module::onEnable);
        setupCloud();
        setupCommands();
        commands.forEach(cmd -> {
            cmd.register(commandManager);
        });
    }

    @Override
    public void disable() {
        modules.values().stream().filter(Module::isEnabled).forEach(Module::onDisable);
        PacketEvents.get().terminate();
        INSTANCE = null;
    }

    public static Cyclone getInstance() {
        return INSTANCE;
    }

    void setupModules() {
        modules.put("command", new CommandModule(this));
        modules.put("message", new MessageModule(this));
    }

    void setupCommands() {
        commands.add(new HelpCommand());
        commands.add(new ListCommand());
    }

    void setupCloud() {
        Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build();
        Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            this.commandManager = new PaperCommandManager<>(this, executionCoordinatorFunction,
                    mapperFunction, mapperFunction);
        } catch (final Exception e) {
            this.getLogger().severe("Failed to initialize the commandmanager");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.bukkitAudiences = BukkitAudiences.create(this);
        this.minecraftHelp = new MinecraftHelp<>("/cyclone help", this.bukkitAudiences::sender,
                this.commandManager);
        this.minecraftHelp.setHelpColors(HelpColors.of(NamedTextColor.DARK_GRAY, NamedTextColor.WHITE, NamedTextColor.GRAY, NamedTextColor.YELLOW, NamedTextColor.GRAY));
        if (this.commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.commandManager.registerBrigadier();
        }
        if (this.commandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            ((PaperCommandManager<CommandSender>) this.commandManager)
                    .registerAsynchronousCompletions();
        }
        new MinecraftExceptionHandler<CommandSender>().withInvalidSyntaxHandler()
                .withInvalidSenderHandler().withArgumentParsingHandler()
                .withCommandExecutionHandler()
                .withDecorator(component -> TextUtil.parseConfig("messages.prefix")
                        .append(Component.space()).append(component))
                .apply(this.commandManager, this.bukkitAudiences::sender);
        commandManager.registerExceptionHandler(NoPermissionException.class,
                (source, exception) -> {
                    TextUtil.sendMessage(source,
                            TextUtil.parseConfig("messages.no-permission", Arrays.asList(
                                Placeholder.unparsed("permission", exception.getMissingPermission()),
                                Placeholder.unparsed("command", exception.getCurrentChain().get(0).getName()))));
                });
        commandManager.registerExceptionHandler(InvalidSyntaxException.class,
                (source, exception) -> {
                    String[] syntax = exception.getCorrectSyntax().split(" ");

                    TextUtil.sendMessage(source,
                            TextUtil.parseConfig("messages.usage", Arrays.asList(Placeholder
                                    .unparsed("command", syntax[0] + " [" + syntax[1] + "]"))));
                });
     //   commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(Module.class), parser -> new ModuleArgument.ModuleParser<>());
    }

    public BukkitAudiences getAdventure() {
        return this.bukkitAudiences;
    }

    public BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public MinecraftHelp<CommandSender> getMinecraftHelp() {
        return minecraftHelp;
    }

    public Map<String, Module> getModules() {
        return modules;
    }



}
