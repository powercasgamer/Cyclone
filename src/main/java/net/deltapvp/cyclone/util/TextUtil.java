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
package net.deltapvp.cyclone.util;

import java.util.Collection;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.deltapvp.cyclone.Cyclone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

public class TextUtil {
    public final static MiniMessage MINIMESSAGE = MiniMessage.builder()
            .tags(TagResolver.builder().resolvers(StandardTags.color()).build()).build();
        
    public static Component parse(@NotNull String content) {
        return parse(content, null);
    }

    public static Component parse(@NotNull String content, @Nullable Collection<TagResolver> tags) {
        if (tags == null || tags.isEmpty()) {
            return MINIMESSAGE.deserialize(content);
        }
        return MINIMESSAGE.deserialize(content, TagResolver.resolver(tags));
    }

    public static Component parseConfig(@NotNull String path) {
        return parse(path, null);
    }

    public static Component parseConfig(@NotNull String path, @Nullable Collection<TagResolver> tags) {
        ConfigurationSection section = Cyclone.getInstance().getConfig().getConfigurationSection(path);
        String content = section.getString("text");
        String hover = section.getString("hover");
        String click = section.getString("click");
        Component component;
        if (tags == null || tags.isEmpty()) {
            component = MINIMESSAGE.deserialize(content);
        }
        component = MINIMESSAGE.deserialize(content, TagResolver.resolver(tags));
        if (!hover.isEmpty()) {
            component = component.hoverEvent(HoverEvent.showText(parse(hover)));
        }
        if (!click.isEmpty()) {
            component = component.clickEvent(ClickEvent.runCommand(click));
        }

        return component;
    }

    public static void sendMessage(CommandSender sender, String content) {
        sendMessage(sender, parse(content));
    }

    public static void sendMessage(CommandSender sender, Component component) {
        Cyclone.getInstance().getAdventure().sender(sender).sendMessage(component);
    }


}
