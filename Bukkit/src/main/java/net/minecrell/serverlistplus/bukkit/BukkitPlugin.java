/*
 *       __                           __ _     _     ___ _
 *      / _\ ___ _ ____   _____ _ __ / /(_)___| |_  / _ \ |_   _ ___
 *      \ \ / _ \ '__\ \ / / _ \ '__/ / | / __| __|/ /_)/ | | | / __|
 *      _\ \  __/ |   \ V /  __/ | / /__| \__ \ |_/ ___/| | |_| \__ \
 *      \__/\___|_|    \_/ \___|_| \____/_|___/\__\/    |_|\__,_|___/
 *                       Customize your server ping!
 *
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.bukkit;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.logging.Level;

import net.minecrell.serverlistplus.api.AbstractServerPingResponse;
import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.api.ServerPingResponse;
import net.minecrell.serverlistplus.api.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.api.plugin.ServerType;
import net.minecrell.serverlistplus.bukkit.util.AbstractBukkitPlugin;
import net.minecrell.serverlistplus.core.DefaultServerListPlusCore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.google.common.collect.UnmodifiableIterator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;

public final class BukkitPlugin extends AbstractBukkitPlugin implements ServerListPlusPlugin {
    private ServerListPlusCore core;

    private PingEventListener eventListener;
    private PingPacketListener packetListener;

    @Override
    public void onEnable() {
        try {
            this.core = new DefaultServerListPlusCore(this);
        } catch (ServerListPlusException e) {
            this.getLogger().info("Please fix the error before restarting the server!");
            this.disablePlugin(); return;
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "An internal error occurred while initializing the ServerListPlus " +
                    "core!", e);
            this.disablePlugin(); return;
        }

        this.getCommand("ServerListPlus").setExecutor(new ServerListPlusCommand());
        this.configurationReloaded();

        this.getLogger().info(this.getDisplayVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info(this.getDisplayVersion() + " disabled.");
    }

    public final class ServerListPlusCommand implements CommandExecutor {
        private ServerListPlusCommand() {}

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            core.processCommand(new BukkitCommandSender(sender), cmd.getName(), label, args); return true;
        }
    }

    public final class PingEventListener implements Listener {
        private PingEventListener() {}

        @EventHandler
        public void onServerListPing(final ServerListPingEvent event) {
            core.processRequest(event.getAddress(), new AbstractServerPingResponse() {
                @Override
                public void setDescription(String description) {
                    event.setMotd(description);
                }
            }, ServerPingResponse.Modify.DESCRIPTION);
        }
    }

    public final class PingPacketListener extends PacketAdapter {
        private PingPacketListener() {
            super(PacketAdapter.params(BukkitPlugin.this, PacketType.Status.Server.OUT_SERVER_INFO).optionAsync());
        }

        @Override // Server ping packet
        public void onPacketSending(PacketEvent event) {
            final WrappedServerPing ping = event.getPacket().getServerPings().read(0);
            core.processRequest(event.getPlayer().getAddress().getAddress(), new AbstractServerPingResponse() {
                @Override
                public void setPlayerHover(String[] playerHover) {
                    ping.setPlayers(new ProfileConversionIterable(playerHover));
                }
            }, ServerPingResponse.Modify.PLAYERS);
        }
    }

    @Override
    public void configurationReloaded() {
        if (core == null) return;

        if (core.getDataProvider().hasDescription()) {
            if (eventListener == null) {
                this.getServer().getPluginManager().registerEvents((this.eventListener = new PingEventListener()),
                        this);
                this.getLogger().info("Enabled ping event listener.");
            }
        } else if (eventListener != null) {
            HandlerList.unregisterAll(eventListener);
            this.eventListener = null;
            this.getLogger().info("Disabled ping event listener.");
        }

        if (core.getDataProvider().hasPlayerHover()) {
            if (packetListener == null) {
                ProtocolLibrary.getProtocolManager().addPacketListener((this.packetListener = new PingPacketListener()));
                this.getLogger().info("Enabled packet listener.");
            }
        } else if (packetListener != null) {
            ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
            this.packetListener = null;
            this.getLogger().info("Disabled packet listener.");
        }
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUKKIT;
    }

    @Override
    public String colorizeString(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class ProfileConversionIterable implements Iterable<WrappedGameProfile> {
        private final String[] lines;

        @Override
        public Iterator<WrappedGameProfile> iterator() {
            return new UnmodifiableIterator<WrappedGameProfile>() {
                private int pos = 0;

                @Override
                public boolean hasNext() {
                    return pos < lines.length;
                }

                @Override
                public WrappedGameProfile next() {
                    return new WrappedGameProfile("", lines[pos++]); // Create a player with an empty ID
                }
            };
        }
    }
}
