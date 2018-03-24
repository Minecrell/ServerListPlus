package net.minecrell.serverlistplus.bukkit.handlers;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.minecrell.serverlistplus.bukkit.BukkitPlugin;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.status.ResponseFetcher;
import net.minecrell.serverlistplus.core.status.StatusRequest;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.Helper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import java.net.InetSocketAddress;
import java.util.List;

public class PaperEventHandler extends BukkitEventHandler {

    public PaperEventHandler(BukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        // Still handle events that don't implement PaperServerListPingEvent
        if (!(event instanceof PaperServerListPingEvent)) {
            super.onServerListPing(event);
        }
    }

    @EventHandler
    public void onPaperServerListPing(final PaperServerListPingEvent event) {
        if (bukkit.getCore() == null) return; // Too early, we haven't finished initializing yet

        StatusRequest request = bukkit.getCore().createRequest(event.getAddress());
        request.setProtocolVersion(event.getClient().getProtocolVersion());
        InetSocketAddress host = event.getClient().getVirtualHost();
        if (host != null) {
            request.setTarget(host);
        }

        StatusResponse response = request.createResponse(bukkit.getCore().getStatus(),
                // Return unknown player counts if it has been hidden
                new ResponseFetcher() {
                    @Override
                    public Integer getOnlinePlayers() {
                        return event.shouldHidePlayers() ? null : event.getNumPlayers();
                    }

                    @Override
                    public Integer getMaxPlayers() {
                        return event.shouldHidePlayers() ? null : event.getMaxPlayers();
                    }

                    @Override
                    public int getProtocolVersion() {
                        return event.getProtocolVersion();
                    }
                }
        );

        // Description
        String message = response.getDescription();
        if (message != null) event.setMotd(message);

        // Version name
        message = response.getVersion();
        if (message != null) event.setVersion(message);
        // Protocol version
        Integer protocol = response.getProtocolVersion();
        if (protocol != null) event.setProtocolVersion(protocol);

        if (response.hidePlayers()) {
            event.setHidePlayers(true);
        } else {
            // Online players
            Integer count = response.getOnlinePlayers();
            if (count != null) event.setNumPlayers(count);
            // Max players
            count = response.getMaxPlayers();
            if (count != null) event.setMaxPlayers(count);

            // Player hover
            message = response.getPlayerHover();
            if (message != null) {
                List<PlayerProfile> profiles = event.getPlayerSample();
                profiles.clear();

                if (response.useMultipleSamples()) {
                    count = response.getDynamicSamples();
                    List<String> lines = count != null ? Helper.splitLinesCached(message, count) :
                            Helper.splitLinesCached(message);

                    for (String line : lines) {
                        profiles.add(bukkit.getServer().createProfile(line));
                    }
                } else {
                    profiles.add(bukkit.getServer().createProfile(message));
                }
            }
        }

        // Favicon
        FaviconSource favicon = response.getFavicon();
        if (favicon != null) {
            CachedServerIcon icon = bukkit.getFavicon(favicon);
            if (icon != null)
                try {
                    event.setServerIcon(icon);
                } catch (UnsupportedOperationException ignored) {}
        }
    }

}
