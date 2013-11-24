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

package net.minecrell.serverlistplus.bukkit;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.reflect.StructureModifier;
import net.md_5.bungee.api.ServerPing;
import net.minecrell.serverlistplus.api.plugin.ServerListPlugin;
import net.minecrell.serverlistplus.api.ServerListPlusAPI;
import net.minecrell.serverlistplus.api.plugin.ServerListServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class BukkitServerListPlus extends JavaPlugin implements ServerListPlugin {
    private final Gson gson = new Gson();

    private final ServerListServer server = new BukkitServer(this);

    private ServerListPlusAPI serverList;
    private LoginListener loginListener;

    private RequestListener requestListener;
    private Map<String, String> hostMap;

    @Override
    public void onEnable() {
        try {
            this.serverList = new ServerListPlusAPI(this);
        } catch (Exception e) {
            this.getLogger().warning("Disabling the plugin, please fix the error before restarting the server!");
            this.getServer().getPluginManager().disablePlugin(this); return;
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(new PingListener());
        this.getCommand("serverlistplus").setExecutor(new ServerListCommand());
        this.reload();

        this.getLogger().info(this.getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info(this.getName() + " disabled.");
    }

    public class ServerListCommand implements CommandExecutor {
        private ServerListCommand() {}

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            serverList.processCommand(new BukkitCommandSender(sender), label, args); return true;
        }
    }

    public class RequestListener extends PacketAdapter {
        private RequestListener() {
            super(BukkitServerListPlus.this, ConnectionSide.CLIENT_SIDE, GamePhase.LOGIN, Packets.Client.HANDSHAKE);
        }

        @Override // Handshake
        public void onPacketReceiving(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            Integer state = packet.getIntegers().readSafely(2);

            // Check if handshake is for server ping
            if (state == null) return;
            if (state != 1) return;

            String hostname = packet.getStrings().readSafely(0);
            if (hostname == null) return; // Check if hostname was correctly read

            // Cache the hostname
            hostMap.put(event.getPlayer().getAddress().getAddress().getHostAddress(), hostname);
        }
    }

    public class PingListener extends PacketAdapter {
        private PingListener() {
            super(BukkitServerListPlus.this, ConnectionSide.SERVER_SIDE, GamePhase.LOGIN, Packets.Server.KICK_DISCONNECT);
        }

        @Override // Kick disconnect
        public void onPacketSending(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            StructureModifier<String> packetStrings = packet.getStrings();

            InetAddress address = event.getPlayer().getAddress().getAddress();
            String hostname = address.getHostAddress();
            hostname = (hostMap.containsKey(hostname)) ? hostMap.get(hostname) : null;

            try {
                ServerPing ping = gson.fromJson(packetStrings.read(0), ServerPing.class);
                if ((ping == null) || (ping.getVersion() == null) || (ping.getPlayers() == null)) return;
                packetStrings.write(0, gson.toJson(serverList.processRequest(address, ping, hostname)));
            } catch (JsonParseException ignored) {}
        }
    }

    public class LoginListener implements Listener {
        private LoginListener() {}

        @EventHandler
        public void onPlayerLogin(PlayerLoginEvent event) {
            serverList.processPlayerLogin(event.getPlayer().getName(), event.getAddress());
        }
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

        // Clear the host cache
        hostMap = new HashMap<>();

        if (serverList.getConfiguration().getForcedHosts().size() > 0) {
            // Enable packet listener if it is disbled
            if (requestListener == null)
                ProtocolLibrary.getProtocolManager().addPacketListener((this.requestListener = new RequestListener()));
        } else if (requestListener != null) {
            // Disable packet listener if it is enabled
            ProtocolLibrary.getProtocolManager().removePacketListener(requestListener);
            this.requestListener = null;
        }

        // Register / unregister the listeners
        if (serverList.getConfiguration().getPlayerTracking().isEnabled()) {
            // Enable login listener if it is disabled
            if (loginListener == null)
                this.getServer().getPluginManager().registerEvents((this.loginListener = new LoginListener()), this);
        } else if (loginListener != null) {
            // Disable login listener if it is enabled
            HandlerList.unregisterAll(loginListener);
            this.loginListener = null;
        }
    }
}
