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
package net.deltapvp.cyclone.module.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Module {

	/**
	 * What the module should execute when its loaded, typically during the onLoad stage of the server
	 */
	default void onLoad() {}

	/**
	 * What the module should execute when its enabled, typically during the onEnable stage of the server
	 */
	default void onEnable() {}

	/**
	 * What the module should execute when its disabled, typically during the onDisable stage of the server
	 */
	default void onDisable() {}


	/**
	 * Checks whether or not a player is able to bypass a module
	 * 
	 * @param player the player to check
	 * @return true if the player can bypass it, false if they cannot
	 */
	default boolean canBypass(@NotNull Player player) {
		return false;
	}

	/**
	 * What the module should do when its reloaded
	 */
	void reload();
}
