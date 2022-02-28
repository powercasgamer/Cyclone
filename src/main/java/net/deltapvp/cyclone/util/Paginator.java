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
import com.google.common.base.Preconditions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.feature.pagination.Pagination;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;


public abstract class Paginator<T> implements Pagination.Renderer.RowRenderer<T> {

    @Range(from = 0, to = 25)
    protected int resultsPerPage = 10;
    @Range(from = 0, to = Integer.MAX_VALUE)
    protected int totalPages;
    protected Component title = Component.empty();
    @NotNull
    protected String pageCommand;
    @NotNull
    protected Collection<@NotNull T> collection;
    protected final Pagination.Builder pagination =
            Pagination.builder().renderer(new Pagination.Renderer() {
                @Override
                public @NotNull Component renderEmpty() {
                    return Component.text("There are no entries present.", NamedTextColor.RED);
                }

                @Override
                public @NotNull Component renderUnknownPage(int page, int pages) {
                    return Component.text("Unknown page selected. " + pages + " total pages.",
                            NamedTextColor.RED);
                }
            }).resultsPerPage(resultsPerPage).line((what) -> {
                what.character('-');
                what.style(Style.style(NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH));
            });
    protected final Pagination<T> pagination2 =
            this.pagination.build(this.title, this, value -> "/" + pageCommand + " " + value);

    public Paginator(@Range(from = 0, to = Integer.MAX_VALUE) int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public Paginator<T> totalPages(@Range(from = 0, to = Integer.MAX_VALUE) int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public Paginator<T> title(@NotNull Component title) {
        Preconditions.checkNotNull(title, "title cannot be null");
        this.title = title;
        return this;
    }

    public Paginator<T> pageCommand(@NotNull String command) {
        Preconditions.checkNotNull(command, "command cannot be null");
        this.pageCommand = command;
        return this;
    }

    public void render(@NotNull CommandSender sender, int page) {
        this.pagination2.render(this.collection, page).forEach(x -> {
            TextUtil.sendMessage(sender, x);
        });
    }

}
