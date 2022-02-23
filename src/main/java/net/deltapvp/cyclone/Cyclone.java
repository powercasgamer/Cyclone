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
import java.util.Collection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import net.deltapvp.cyclone.module.api.Module;
import net.deltapvp.cyclone.module.impl.CommandModule;

public final class Cyclone extends JavaPlugin {
    private static Cyclone INSTANCE;
    private Collection<Module> modules = new ArrayList<>();

    @Override
    public void onLoad() {
        INSTANCE = this;
        saveDefaultConfig();
        setupModules();
        modules.forEach(Module::onLoad);
    }

    @Override
    public void onEnable() {
        getCommand("cyclone").setExecutor(new CommandExecutor() {

            @Override
            public boolean onCommand(CommandSender sender, Command command, String label,
                    String[] args) {
                if (!sender.hasPermission("cyclone.reload"))
                    return false;

                reloadConfig();
                modules.forEach(Module::reload);
                sender.sendMessage("reloaded");
                return true;
            }

        });

        modules.forEach(Module::onEnable);
    }

    @Override
    public void onDisable() {
        modules.forEach(Module::onDisable);
        INSTANCE = null;
    }

    public static Cyclone getInstance() {
        return INSTANCE;
    }

    void setupModules() {
        modules.add(new CommandModule(this));
    }
}
