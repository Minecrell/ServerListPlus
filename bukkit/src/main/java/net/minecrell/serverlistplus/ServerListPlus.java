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
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class ServerListPlus extends JavaPlugin implements Listener {
    private File configFile;
    private ServerListPlusAPI serverList;
    private Gson gson;

    @Override
    public void onEnable() {
        this.gson = new Gson();
        this.configFile = new File(this.getDataFolder(), "serverlist.txt");

        try {
            this.serverList = new ServerListPlusAPI(configFile);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Unable to access server list configuration file: " + configFile.getAbsolutePath(), e);
            this.getLogger().warning("Disabling plugin, please fix the error, before restarting again!");
            this.getServer().getPluginManager().disablePlugin(this); return;
        }

        this.getServer().getPluginManager().registerEvents(this, this);
        ProtocolLibrary.getProtocolManager().addPacketListener(new ServerListPacketAdapter());
        this.getLogger().info(this.getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info(this.getName() + " disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        this.getLogger().info("Reloading configuration...");

        try {
            serverList.reload();

            this.getLogger().info("Configuration reloaded!");
            sender.sendMessage(ChatColor.GREEN + "Configuration successfully reloaded!");
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Unable to access server list configuration file: " + configFile.getAbsolutePath(), e);
            this.getLogger().warning("Cancelling configuration reload!");
            sender.sendMessage(ChatColor.RED + "An internal error occurred while reloading the configuration!");
        }

        return true;
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        ServerListPlusAPI.handlePlayerLogin(event.getPlayer().getName(), event.getPlayer().getAddress().getAddress());
    }

    private final class ServerListPacketAdapter extends PacketAdapter {
        private ServerListPacketAdapter() {
            super(ServerListPlus.this, ConnectionSide.SERVER_SIDE, GamePhase.LOGIN, Server.KICK_DISCONNECT);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            PacketContainer packet = event.getPacket();

            try {
                ServerPing ping = gson.fromJson(packet.getStrings().read(0), ServerPing.class);

                if ((ping == null) || (ping.getVersion() == null) || (ping.getPlayers() == null)) return;
                packet.getStrings().write(0, gson.toJson(serverList.handleServerPing(event.getPlayer().getAddress().getAddress(), ping)));
            } catch (JsonParseException e) {
                return;
            }
        }
    }
}
