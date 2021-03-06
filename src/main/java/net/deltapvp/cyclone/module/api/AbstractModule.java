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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.deltapvp.cyclone.Cyclone;
import net.deltapvp.cyclone.util.FastUUID;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class AbstractModule extends PacketListenerAbstract implements Module, Listener {

	protected final Map<UUID, Integer> punishments = new ConcurrentHashMap<>();
	@NotNull
	private final String name;
	@NotNull
	protected final Logger logger;
	@NotNull
	protected final Plugin plugin;
	@NotNull
	protected final String bypassPerm;
	@Nullable
	protected final String punishCmd;

	protected AbstractModule(@NotNull Cyclone plugin, @NotNull String name) {
		this.plugin = plugin;
		this.name = name;
		this.bypassPerm = plugin.getConfig().getString("modules." + name.toLowerCase() + ".bypass");
		this.punishCmd = plugin.getConfig().getString("modules." + name.toLowerCase() + ".punish");
		this.logger = Logger.getLogger(plugin.getLogger().getName() + " - " + name);

		if (bypassPerm != null) {
			Permission perm = new Permission(bypassPerm, PermissionDefault.FALSE);
			Bukkit.getPluginManager().addPermission(perm);
		}
	}

	@Override
	public void onEnable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		PacketEvents.get().registerListener(this);
		logger.info(() -> "Registered successfully");
	}

	@Override
	public boolean isEnabled() {
		return plugin.getConfig().getBoolean("modules." + name.toLowerCase() + ".enabled");
	}

	/**
	 * The name of the module
	 * 
	 * @return the name of the module
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canBypass(@NotNull Player player) {
		return player.hasPermission(bypassPerm);
	}

	/**
	 * Punish a player based on the module's punish command. Possible placeholders are: %player%,
	 * %uuid%, %total%
	 * 
	 * @param player the player to punish
	 */
	public void punishPlayer(@NotNull Player player) {
		if (punishCmd == null || punishCmd.isEmpty()) {
			return;
		}
		punishments.compute(player.getUniqueId(), (uuid, numb) -> {
			return numb + 1;
		});
		String cmd = punishCmd;
		cmd = cmd.replace("%player%", player.getName());
		cmd = cmd.replace("%uuid%", FastUUID.toString(player.getUniqueId()));
		cmd = cmd.replace("%total%", punishments.get(player.getUniqueId()) + "");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}
}
