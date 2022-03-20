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

import java.net.URL;
import java.util.Collections;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.slimjar.app.builder.ApplicationBuilder;
import io.github.slimjar.logging.ProcessLogger;
import io.github.slimjar.resolver.data.Repository;

public class DependencyDownloader {
    private static final String[] BLACKLIST =
    {"Checksum matched", "Resolved", "Verifying checksum", "Downloaded checksum"};

    private final JavaPlugin plugin;

    public DependencyDownloader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void downloadDependencies() {
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
                        plugin.getLogger().info(finalMessage);
                }
            }).internalRepositories(
                    Collections.singleton(new Repository(new URL("https://repo.deltapvp.net/"))))
                .downloadDirectoryPath(plugin.getDataFolder().toPath().resolve("libs"))
                .build();
        } catch (Throwable thr) {
            plugin.getLogger().log(Level.SEVERE, "error whilst loading dependencies", thr);
        }

    }
    
}
