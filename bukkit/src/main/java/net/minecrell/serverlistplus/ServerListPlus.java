package net.minecrell.serverlistplus;

import com.comphenix.protocol.Packets.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import net.md_5.bungee.api.ServerPing;
import net.minecrell.serverlistplus.api.ServerListPlusAPI;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class ServerListPlus extends JavaPlugin {
    private ServerListPlusAPI serverList;

    @Override
    public void onEnable() {
        File configFile = new File(this.getDataFolder(), "serverlist.txt");

        try {
            this.serverList = new ServerListPlusAPI(configFile);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Unable to access server list configuration file: " + configFile.getAbsolutePath(), e);
            this.getLogger().warning("Disabling plugin, please fix the error, before restarting again!");
            this.getServer().getPluginManager().disablePlugin(this); return;
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(new ServerListPacketAdapter());
        this.getLogger().info(this.getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info(this.getName() + " disabled.");
    }

    private final class ServerListPacketAdapter extends PacketAdapter {
        private ServerListPacketAdapter() {
            super(ServerListPlus.this, ConnectionSide.SERVER_SIDE, GamePhase.LOGIN, Server.KICK_DISCONNECT);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            PacketContainer packet = event.getPacket(); Gson gson = new Gson();
            packet.getStrings().write(0,gson.toJson(serverList.handleServerPing(gson.fromJson(packet.getStrings().read(0), ServerPing.class))));
        }
    }
}
