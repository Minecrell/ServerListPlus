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

package net.minecrell.serverlistplus.api;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.minecrell.serverlistplus.api.plugin.ServerCommandSender;
import net.minecrell.serverlistplus.api.plugin.ServerListPlugin;
import net.minecrell.serverlistplus.api.plugin.ServerListServer;
import org.yaml.snakeyaml.Yaml;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ServerListPlusAPI {
    private final ServerListPlugin plugin;

    private final Yaml yamlLoader;

    private ServerListConfiguration config;

    private final Map<String, String> playerIPs = new HashMap<>();

    private ServerListMetrics metrics;

    public ServerListPlusAPI(ServerListPlugin plugin) throws Exception {
        this.plugin = plugin;

        this.getLogger().info("Initializing ServerListPlusAPI...");
        this.yamlLoader = ServerListConfiguration.createYAMLLoader(this.getPlugin().getClass().getClassLoader());
        this.reload();
        this.getLogger().info("ServerListPlusAPI initialized!");
    }

    public ServerListPlugin getPlugin() {
        return plugin;
    }

    public ServerListServer getServer() {
        return this.getPlugin().getServerListServer();
    }

    public ServerListConfiguration getConfiguration() {
        return config;
    }

    public Logger getLogger()     {
        return plugin.getLogger();
    }

    public void reload() throws Exception {
        try {
            this.config = ServerListConfiguration.loadConfiguration(this, yamlLoader);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "An internal error occurred while loading the configuration!", e);
            throw e;
        }

        plugin.reload();

        if (config.isEnableMetrics()) {
            if (metrics == null) {
                try {
                    (this.metrics = new ServerListMetrics(this)).start();
                } catch (Throwable ignored) {}
            }
        } else if (metrics != null) {
            // Disable metrics
            try {
                this.metrics.disable();
            } catch (Throwable ignored) {}
            this.metrics = null;
        }

        if (metrics != null) {
            try {
                metrics.reloadConfiguration(config);
            } catch (Throwable ignored) {}
        }
    }

    public ServerPing processRequest(InetSocketAddress address, ServerPing ping) {
        return this.processRequest(address, ping, null);
    }

    public ServerPing processRequest(InetAddress address, ServerPing ping) {
        return this.processRequest(address, ping, null);
    }

    public ServerPing processRequest(InetSocketAddress address, ServerPing ping, String forcedHost) {
        return this.processRequest(address.getAddress(), ping, forcedHost);
    }

    public ServerPing processRequest(InetAddress address, ServerPing ping, String forcedHost) {
        if (config.getLines().size() <= 0) {
            ping.getPlayers().setSample(null); return ping;
        }

        List<String> lines = ((forcedHost != null) && config.getForcedHosts().containsKey(forcedHost)) ?
                config.getForcedHosts().get(forcedHost) : config.getLines();

        String playerName = null;
        boolean identified = false;
        if (config.getPlayerTracking().isEnabled()) {
            playerName = address.getHostAddress();
            if (playerIPs.containsKey(playerName)) {
                playerName = playerIPs.get(playerName);
                identified = true;
            } else {
                playerName = config.getPlayerTracking().getUnknownPlayer().getName();
                if (config.getPlayerTracking().getUnknownPlayer().getCustomLines().isEnabled()) {
                    lines = config.getPlayerTracking().getUnknownPlayer().getCustomLines().getLines();
                }
            }
        }

        PlayerInfo[] players = new PlayerInfo[lines.size()];
        for (int i = 0; i < players.length; i++) {
            String line = lines.get(i);
            if (line.trim().length() <= 0) line = "&r";
            if (config.getPlayerTracking().isEnabled()) line = line.replace("%player%", playerName);

            players[i] = new PlayerInfo(this.getServer().colorizeString(line), ""); // Create a player with an empty ID
        }

        ping.getPlayers().setSample(players);

        if (metrics != null) {
            try {
                metrics.processRequest(identified);
            } catch (Throwable ignored) {}
        }

        return ping;
    }

    public void processPlayerLogin(String playerName, InetSocketAddress address) {
        this.processPlayerLogin(playerName, address.getAddress());
    }

    public void processPlayerLogin(String playerName, InetAddress address) {
        if (config.getPlayerTracking().isEnabled()) playerIPs.put(address.getHostAddress(), playerName);

        if (metrics != null) {
            try {
                metrics.processPlayerLogin();
            } catch (Throwable ignored) {}
        }
    }

    public void processCommand(ServerCommandSender sender, String label, String[] args) {
        switch (((args.length > 0) ? args[0] : "info").toUpperCase(Locale.ENGLISH)) {
            case "RELOAD":
                try {
                    this.reload();
                    this.sendColoredMessage(sender, "&aConfiguration reloaded!");
                } catch (Exception e) {
                    this.getLogger().warning("Cancelling configuration reload!");
                    this.sendColoredMessage(sender, "&cAn internal error occurred while reloading the plugin configuration!");
                } break;
            default:
                this.sendColoredMessage(
                        sender,
                        "&6ServerListPlusAPI v" + plugin.getVersion(),
                        "&cCopyright (C) 2013, Minecrell",
                        "&9http://www.spigotmc.org/resources/serverlistplus.241/",
                        "-----------------------------------------------",
                        "&2Type &6/" + label + " reload &2to reload the plugin configuration."
                ); break;
        }
    }

    private void sendColoredMessage(ServerCommandSender sender, String... messages) {
        for (String message : messages)
            sender.sendMessage(this.getServer().colorizeString(message));
    }
}
