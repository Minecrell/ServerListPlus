/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your server list ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.bukkit;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.ServerStatusManager;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.plugin.ServerType;
import net.minecrell.serverlistplus.core.util.InstanceStorage;

import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class BukkitPlugin extends BukkitPluginBase implements ServerListPlusPlugin {
    private ServerListPlusCore core;
    private LoginListener loginListener;
    private PingEventListener pingListener;
    private StatusPacketListener packetListener;

    @Override
    public void onEnable() {
        try {
            this.core = new ServerListPlusCore(this);
        } catch (ServerListPlusException e) {
            this.getLogger().info("Please fix the error before restarting the server!");
            this.disablePlugin(); return;
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "An internal error occurred while initializing the core!", e);
            this.disablePlugin(); return;
        }

        this.getLogger().info(this.getDisplayName() + " enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info(this.getDisplayName() + " disabled.");
    }

    public final class LoginListener implements Listener {
        private LoginListener() {}

        @EventHandler
        public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
            core.addClient(event.getName(), event.getAddress());
        }
    }

    public final class PingEventListener implements Listener {
        private PingEventListener() {}

        @EventHandler
        public void onServerListPing(ServerListPingEvent event) {
            String player = loginListener != null ? core.resolveClient(event.getAddress()) : null;
            String description = core.getStatus().getDescription(player);
            if (description != null) event.setMotd(description);
        }
    }

    public final class StatusPacketListener extends PacketAdapter {
        public StatusPacketListener() {
            super(PacketAdapter.params(BukkitPlugin.this, PacketType.Status.Server.OUT_SERVER_INFO).optionAsync());
        }

        @Override // Server status packet
        public void onPacketSending(PacketEvent event) {
            String player = loginListener != null ? core.resolveClient(event.getPlayer().getAddress().getAddress()) : null;
            String playerHover = core.getStatus().getPlayerHover(player);
            if (playerHover != null) {
                event.getPacket().getServerPings().read(0).setPlayers(
                        Arrays.asList(new WrappedGameProfile(ServerStatusManager.EMPTY_UUID, playerHover)));
            }
        }
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUKKIT;
    }

    @Override
    public String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public void initialize(ServerListPlusCore core) {

    }

    @Override
    public void configChanged(InstanceStorage<Object> confs) {
        if (confs.get(PluginConf.class).PlayerTracking) {
            if (loginListener == null) {
                this.registerListener(this.loginListener = new LoginListener());
                this.getLogger().info("Registered player tracking listener.");
            }
        } else if (loginListener != null) {
            this.unregisterListener(loginListener);
            this.loginListener = null;
            this.getLogger().info("Unregistered player tracking listener.");
        }
    }

    @Override
    public void statusChanged(ServerStatusManager status) {
        if (status.hasDescription()) {
            if (pingListener == null) {
                this.registerListener(this.pingListener = new PingEventListener());
                this.getLogger().info("Registered server ping listener.");
            }
        } else if (pingListener != null) {
            this.unregisterListener(pingListener);
            this.pingListener = null;
            this.getLogger().info("Unregistered server ping listener.");
        }

        if (status.hasPlayerHover()) {
            if (packetListener == null) {
                ProtocolLibrary.getProtocolManager().addPacketListener(this.packetListener = new StatusPacketListener());
                this.getLogger().info("Registered status packet listener.");
            }
        } else if (packetListener != null) {
            ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
            this.packetListener = null;
            this.getLogger().info("Unregistered status packet listener.");
        }
    }
}
