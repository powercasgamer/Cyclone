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
package net.deltapvp.cyclone.command.api;

import org.bukkit.command.CommandSender;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.meta.CommandMeta;
import net.deltapvp.cyclone.Cyclone;

/**
 * A command, registered with Cloud's {@link CommandManager} and operates on a {@link CommandSender}
 * sender.
 */
public abstract class BaseCommand {

    /**
     * Register this command with the {@link CommandManager}.
     *
     * @param commandManager The manager to register commands with. This assumes the manager
     *        supports {@link CommandSender}s as a sender.
     */
    public abstract void register(final CommandManager<CommandSender> commandManager);

    public Command.Builder<CommandSender> rootBuilder() {
        return getPlugin().getCommandManager().commandBuilder("cyclone")
                .meta(CommandMeta.DESCRIPTION,
                        String.format("cyclone command. '/%s help'", "cyclone"))
                .handler(ctx -> {
                    ctx.getSender().sendMessage("lol");
                });
    }

    public Cyclone getPlugin() {
        return Cyclone.getInstance();
    }

}
