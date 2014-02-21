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

package net.minecrell.serverlistplus.bungee;

import java.util.logging.Level;

import net.minecrell.serverlistplus.api.AbstractServerPingResponse;
import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.api.configuration.PluginConfiguration;
import net.minecrell.serverlistplus.api.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.api.plugin.ServerType;
import net.minecrell.serverlistplus.bungee.util.AbstractBungeePlugin;
import net.minecrell.serverlistplus.core.DefaultServerListPlusCore;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class BungeePlugin extends AbstractBungeePlugin implements ServerListPlusPlugin {
    private ServerListPlusCore core;

    private LoginListener loginListener;
    private PingListener pingListener;

    @Override
    public void onEnable() {
        try {
            this.core = new DefaultServerListPlusCore(this);
        } catch (ServerListPlusException e) {
            this.getLogger().info("Please fix the error before restarting the server!"); return;
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "An internal error occurred while initializing the ServerListPlus " +
                    "core!", e); return;
        }

        this.getProxy().getPluginManager().registerCommand(this, new ServerListPlusCommand());
        this.configurationReloaded();
    }

    public final class ServerListPlusCommand extends Command {
        private ServerListPlusCommand() {
            super("ServerListPlus", "ServerListPlus.Admin", "ServerList+", "ServerList", "slp", "sl");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            // TODO: Unable to get the entered command in BungeeCord
            core.processCommand(new BungeeCommandSender(sender), this.getName(), this.getName(), args);
        }
    }

    public final class LoginListener implements Listener {
        private LoginListener() {}

        @EventHandler
        public void onPlayerLogin(LoginEvent event) {
            core.processLogin(event.getConnection().getName(), event.getConnection().getAddress().getAddress());
        }
    }

    public final class PingListener implements Listener {
        private PingListener() {}

        @EventHandler
        public void onProxyPing(final ProxyPingEvent event) {
            final ServerPing response = event.getResponse();
            core.processRequest(event.getConnection().getAddress().getAddress(), new AbstractServerPingResponse() {
                @Override
                public void setDescription(String description) {
                    response.setDescription(description);
                }

                @Override
                public void setPlayerHover(String[] playerHover) {
                    ServerPing.PlayerInfo[] sample = new ServerPing.PlayerInfo[playerHover.length];
                    for (int i = 0; i < playerHover.length; i++) {
                        sample[i] = new ServerPing.PlayerInfo(playerHover[i], ""); // Create a player with an empty ID
                    }
                    response.getPlayers().setSample(sample);
                }
            });
        }
    }

    @Override
    public void configurationReloaded() {
        if (core == null) return;

        if (core.getDataProvider().hasDescription() || core.getDataProvider().hasPlayerHover()) {
            if (pingListener == null) {
                this.getProxy().getPluginManager().registerListener(this, (this.pingListener = new PingListener()));
                this.getLogger().info("Enabled ping listener.");
            }
        } else if (pingListener != null) {
            this.getProxy().getPluginManager().unregisterListener(pingListener);
            this.pingListener = null;
            this.getLogger().info("Disabled ping listener.");
        }

        if (core.getConfigManager().get(PluginConfiguration.class).PlayerTracking) {
            if (loginListener == null) {
                this.getProxy().getPluginManager().registerListener(this, (this.loginListener = new LoginListener()));
                this.getLogger().info("Enabled login listener.");
            }
        } else if (loginListener != null) {
            this.getProxy().getPluginManager().unregisterListener(loginListener);
            this.loginListener = null;
            this.getLogger().info("Disabled login listener.");
        }
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUNGEE;
    }

    @Override
    public String colorizeString(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
