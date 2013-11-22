/*
 * ServerListPlus - Customize your server's ping information!
 * Copyright (C) 2013, Minecrell
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.bungee;

import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.minecrell.serverlistplus.api.plugin.ServerListPlugin;
import net.minecrell.serverlistplus.api.ServerListPlusAPI;
import net.minecrell.serverlistplus.api.plugin.ServerListServer;

public class BungeeServerListPlus extends Plugin implements ServerListPlugin {
    private final ServerListServer server = new BungeeServer(this);
    private ServerListPlusAPI serverList;
    private LoginListener loginListener;

    @Override
    public void onEnable() {
        try {
            this.serverList = new ServerListPlusAPI(this);
        } catch (Exception e) {
            this.getLogger().warning("Disabling the plugin, please fix the error before restarting the server!");
            e.printStackTrace(); return;
        }

        this.getProxy().getPluginManager().registerListener(this, new PingListener());
        this.getProxy().getPluginManager().registerCommand(this, new ServerListCommand());
        this.reload();
    }

    public class ServerListCommand extends Command {
        private ServerListCommand() {
            super("serverlistplus", "ServerListPlus.Admin", "serverlist", "slp", "sl");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            serverList.processCommand(new BungeeCommandSender(sender), "serverlistplus", args);
        }
    }

    public class PingListener implements Listener {
        private PingListener() {}

        @EventHandler
        public void onProxyPing(ProxyPingEvent event) {
            String forcedHost = null;

            if (serverList.getConfiguration().getForcedHosts().size() > 0) {
                ServerInfo forcedHostInfo = AbstractReconnectHandler.getForcedHost(event.getConnection());
                if (forcedHostInfo != null) {
                    forcedHost = forcedHostInfo.getName();
                }
            }

            serverList.processRequest(event.getConnection().getAddress(), event.getResponse(), forcedHost);
        }
    }

    public class LoginListener implements Listener {
        private LoginListener() {}

        @EventHandler
        public void onPlayerLogin(LoginEvent event) {
            serverList.processPlayerLogin(event.getConnection().getName(), event.getConnection().getAddress());
        }
    }


    @Override
    public String getName() {
        return this.getDescription().getName();
    }

    @Override
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public ServerListServer getServerListServer() {
        return server;
    }

    @Override
    public void reload() {
        if (serverList == null) return;
        if (serverList.getConfiguration().getPlayerTracking().isEnabled()) {
            if (loginListener == null)
                this.getProxy().getPluginManager().registerListener(this, (this.loginListener = new LoginListener()));
        } else if (loginListener != null) {
            this.getProxy().getPluginManager().unregisterListener(loginListener);
            this.loginListener = null;
        }
    }
}
