package net.minecrell.serverlistplus;

import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.minecrell.serverlistplus.api.ServerListPlusAPI;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ServerListPlus extends Plugin implements Listener {
    private ServerListPlusAPI serverList;

    @Override
    public void onEnable() {
        File configFile = new File(this.getDataFolder(), "serverlist.txt");

        try {
            this.serverList = new ServerListPlusAPI(configFile);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Unable to access server list configuration file: " + configFile.getAbsolutePath(), e);
            this.getLogger().warning("Disabling plugin, please fix the error, before restarting again!"); return;
        }

        this.getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onServerPing(ProxyPingEvent event) {
        serverList.handleServerPing(event.getResponse());
    }
}
