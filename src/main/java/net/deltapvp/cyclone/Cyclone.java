/*
 * This file is part of Cyclone, licensed under the MIT License.
 *
 *  Copyright (c) 2022-122 powercas_gamer
 *  Copyright (c) 2022-122 contributors
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
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
import io.github.slimjar.app.builder.ApplicationBuilder;
import io.leangen.geantyref.TypeToken;
import net.deltapvp.cyclone.command.api.BaseCommand;
import net.deltapvp.cyclone.command.arguments.ModuleArgument;
import net.deltapvp.cyclone.command.impl.HelpCommand;
import net.deltapvp.cyclone.command.impl.ListCommand;
import net.deltapvp.cyclone.module.api.Module;
import net.deltapvp.cyclone.module.impl.CommandModule;
import net.deltapvp.cyclone.module.impl.MessageModule;
import net.deltapvp.cyclone.util.TextUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.JsonObject;
import io.github.slimjar.app.builder.ApplicationBuilder;
import io.github.slimjar.logging.ProcessLogger;
import io.github.slimjar.resolver.data.Repository;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public final class Cyclone extends JavaPlugin {
    private static Cyclone INSTANCE;
    private Map<String, Module> modules = new HashMap<>(2);
    private Collection<BaseCommand> commands = new ArrayList<>(2);
    // cloud
    private BukkitCommandManager<CommandSender> commandManager;
    private BukkitAudiences bukkitAudiences;
    private MinecraftHelp<CommandSender> minecraftHelp;
    private static final String[] BLACKLIST =
            {"Checksum matched", "Resolved", "Verifying checksum", "Downloaded checksum"};

    @Override
    public void onLoad() {
        INSTANCE = this;
        downloadDepends();
        saveDefaultConfig();
        setupModules();
        modules.values().stream().filter(Module::isEnabled).forEach(Module::onLoad);
    }

    @Override
    public void onEnable() {
        modules.values().stream().filter(Module::isEnabled).forEach(Module::onEnable);
        setupCloud();
        setupCommands();
        commands.forEach(cmd -> {
            cmd.register(commandManager);
        });
    }

    @Override
    public void onDisable() {
        modules.values().stream().filter(Module::isEnabled).forEach(Module::onDisable);
        INSTANCE = null;
    }

    public static Cyclone getInstance() {
        return INSTANCE;
    }

    void downloadDepends() {
        try {
            ApplicationBuilder.appending("Cyclone").logger(new ProcessLogger() {
                @Override
                public void log(String arg0, Object... arg1) {
                    String finalMessage = arg0;
                    for (int i = 0; i < arg1.length; i++) {
                        finalMessage = finalMessage.replace("{" + i + "}", arg1[i].toString());
                    }

                    boolean blacklisted = false;
                    for (String bl : BLACKLIST) {
                        if (finalMessage.startsWith(bl)) {
                            blacklisted = true;
                            break;
                        }
                    }
                    if (!blacklisted)
                        getLogger().info(finalMessage);
                }
            }).internalRepositories(
                    Collections.singleton(new Repository(new URL("https://repo.deltapvp.net/"))))
                .downloadDirectoryPath(getDataFolder().toPath().resolve("libs"))
                .build();
        } catch (Throwable thr) {
            getLogger().log(Level.SEVERE, "error whilst loading dependencies", thr);
            this.setEnabled(false);
        }

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
        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(Module.class), parser -> new ModuleArgument.ModuleParser<>());
    }

    public BukkitAudiences getAdventure() {
        return this.bukkitAudiences;
    }

    public Map<String, Module> getModules() {
        return modules;
    }

    public BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public MinecraftHelp<CommandSender> getMinecraftHelp() {
        return minecraftHelp;
    }

}
