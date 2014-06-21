/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your complete server status ping!
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
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.plugin.ServerType;
import net.minecrell.serverlistplus.core.util.InstanceStorage;

import java.util.Collections;
import java.util.logging.Level;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.mcstats.MetricsLite;

public class BukkitPlugin extends BukkitPluginBase implements ServerListPlusPlugin {
    private ServerListPlusCore core;
    private LoadingCache<FaviconSource, WrappedServerPing.CompressedImage> faviconCache;

    private LoginListener loginListener;
    private StatusPacketListener packetListener;

    private MetricsLite metrics;

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

        this.getCommand("serverlistplus").setExecutor(new ServerListPlusCommand());
        this.getLogger().info(this.getDisplayName() + " enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info(this.getDisplayName() + " disabled.");
    }

    public final class ServerListPlusCommand implements CommandExecutor {
        private ServerListPlusCommand() {}

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            core.executeCommand(new BukkitCommandSender(sender), cmd.getName(), args); return true;
        }
    }

    public final class LoginListener implements Listener {
        private LoginListener() {}

        @EventHandler
        public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
            core.addClient(event.getName(), event.getAddress());
        }
    }

    public final class StatusPacketListener extends PacketAdapter {
        public StatusPacketListener() {
            super(PacketAdapter.params(BukkitPlugin.this, PacketType.Status.Server.OUT_SERVER_INFO).optionAsync());
        }

        @Override // Server status packet
        public void onPacketSending(final PacketEvent event) {
            final WrappedServerPing ping = event.getPacket().getServerPings().read(0);
            boolean playersHidden = !ping.isPlayersVisible();

            ServerStatusManager.Response response = core.getStatus().createResponse(event.getPlayer().getAddress()
                    .getAddress(), playersHidden ? new ServerStatusManager.ResponseFetcher() :
                    new ServerStatusManager.ResponseFetcher() {

                @Override
                public Integer fetchPlayersOnline() {
                    return ping.getPlayersOnline();
                }

                @Override
                public Integer fetchMaxPlayers() {
                    return ping.getPlayersMaximum();
                }
            });

            String message = response.getDescription();
            if (message != null) ping.setMotD(message);

            if (!playersHidden) {
                Integer count = response.getPlayersOnline();
                if (count != null) ping.setPlayersOnline(count);
                count = response.getMaxPlayers();
                if (count != null) ping.setPlayersMaximum(count);

                message = response.getPlayerHover();
                if (message != null) ping.setPlayers(Collections.singleton(
                        new WrappedGameProfile(ServerStatusManager.EMPTY_UUID, message)));
            }

            message = response.getVersion();
            if (message != null) ping.setVersionName(message);
            Integer protocol = response.getProtocol();
            if (protocol != null) ping.setVersionProtocol(protocol);
        }
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUKKIT;
    }

    @Override
    public LoadingCache<FaviconSource, WrappedServerPing.CompressedImage> getFaviconCache() {
        return faviconCache;
    }

    @Override
    public String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public void initialize(ServerListPlusCore core) {

    }

    @Override
    public void reloadFaviconCache(CacheBuilder<Object, Object> builder) {
        if (builder != null) {
            this.faviconCache = builder.build(new CacheLoader<FaviconSource, WrappedServerPing.CompressedImage>() {
                @Override
                public WrappedServerPing.CompressedImage load(FaviconSource key) throws Exception {
                    return WrappedServerPing.CompressedImage.fromPng(key.getLoader().load(core, key.getSource()));
                }
            });
        } else {
            faviconCache.invalidateAll();
            faviconCache.cleanUp();
            this.faviconCache = null;
        }
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

        if (confs.get(PluginConf.class).Stats) {
            if (metrics == null)
                try {
                    this.metrics = new MetricsLite(this);
                    metrics.enable();
                    metrics.start();
                } catch (Throwable e) {
                    this.getLogger().warning("Failed to enable plugin statistics: " + e.getMessage());
                }
        } else if (metrics != null)
            try {
                metrics.disable();
                this.metrics = null;
            } catch (Throwable e) {
                this.getLogger().warning("Failed to disable plugin statistics: " + e.getMessage());
            }
    }

    @Override
    public void statusChanged(ServerStatusManager status) {
        if (status.hasChanges()) {
            if (packetListener == null) {
                ProtocolLibrary.getProtocolManager().addPacketListener(this.packetListener =
                        new StatusPacketListener());
                this.getLogger().info("Registered status packet listener.");
            }
        } else if (packetListener != null) {
            ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
            this.packetListener = null;
            this.getLogger().info("Unregistered status packet listener.");
        }
    }
}
