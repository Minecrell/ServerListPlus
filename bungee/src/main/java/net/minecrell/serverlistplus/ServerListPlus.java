package net.minecrell.serverlistplus;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.minecrell.serverlistplus.api.ServerListPlusAPI;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class ServerListPlus extends Plugin implements Listener {
    private File configFile;
    private ServerListPlusAPI serverList;

    @Override
    public void onEnable() {
        this.configFile = new File(this.getDataFolder(), "serverlist.txt");

        try {
            this.serverList = new ServerListPlusAPI(configFile);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Unable to access server list configuration file: " + configFile.getAbsolutePath(), e);
            this.getLogger().warning("Disabling plugin, please fix the error, before restarting again!"); return;
        }

        this.getProxy().getPluginManager().registerListener(this, this);
        this.getProxy().getPluginManager().registerCommand(this, new ReloadCommand());
    }

    @EventHandler
    public void onServerPing(ProxyPingEvent event) {
        serverList.handleServerPing(event.getConnection().getAddress().getAddress(), event.getResponse());
    }

    @EventHandler
    public void onPlayerLogin(PostLoginEvent event) {
        ServerListPlusAPI.handlePlayerLogin(event.getPlayer().getName(), event.getPlayer().getAddress().getAddress());
    }

    private class ReloadCommand extends Command {

        public ReloadCommand() {
            super("serverlistplus", "serverlistplus.reload", "serverlist", "slp");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            getLogger().info("Reloading configuration...");

            try {
                serverList.reload();

                getLogger().info("Configuration reloaded!");
                sender.sendMessage(ChatColor.GREEN + "Configuration successfully reloaded!");
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Unable to access server list configuration file: " + configFile.getAbsolutePath(), e);
                getLogger().warning("Cancelling configuration reload!");
                sender.sendMessage(ChatColor.RED + "An internal error occurred while reloading the configuration!");
            }
        }
    }
}
