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
package net.deltapvp.cyclone.module.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import net.deltapvp.cyclone.Cyclone;
import net.deltapvp.cyclone.module.api.AbstractModule;

public class MessageModule extends AbstractModule {
    private String patternString;
    private Pattern pattern;

    public MessageModule(Cyclone plugin) {
        super(plugin, "Message");
        reload();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String input = event.getMessage().replace(" ", "").trim();

        if (canBypass(player)) {
            return;
        }

        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return;
        }

        event.getRecipients().clear();
        logger.warning(player.getName() + " tried to say: " + input);
        punishPlayer(player);
    }

    @Override
    public void reload() {
        this.patternString = plugin.getConfig().getStringList("modules.message.messages").stream()
                .filter(f -> (!(f == null)) || (f != null && !f.isEmpty()))
                .map(s -> String.format("(?:%s)", s)).collect(Collectors.joining("|"));
        this.pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
    }
}
