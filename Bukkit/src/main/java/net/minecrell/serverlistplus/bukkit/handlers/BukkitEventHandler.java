/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
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

package net.minecrell.serverlistplus.bukkit.handlers;

import net.minecrell.serverlistplus.bukkit.BukkitPlugin;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.status.ResponseFetcher;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

public class BukkitEventHandler extends StatusHandler implements Listener {
    private boolean registered;

    public BukkitEventHandler(BukkitPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerListPing(final ServerListPingEvent event) {
        if (bukkit.getCore() == null) return; // Too early, we haven't finished initializing yet
        StatusResponse response = bukkit.getCore().createRequest(event.getAddress()).createResponse(
                bukkit.getCore().getStatus(), new ResponseFetcher() {
                    @Override
                    public Integer getOnlinePlayers() {
                        return event.getNumPlayers();
                    }

                    @Override
                    public Integer getMaxPlayers() {
                        return event.getMaxPlayers();
                    }

                    @Override
                    public int getProtocolVersion() {
                        return -1;
                    }
                });

        // Description
        String message = response.getDescription();
        if (message != null)
            event.setMotd(message);

        // Max players
        Integer max = response.getMaxPlayers();
        if (max != null)
            event.setMaxPlayers(max);

        // Favicon
        FaviconSource favicon = response.getFavicon();
        if (favicon == FaviconSource.NONE) {
            try {
                event.setServerIcon(null);
            } catch (UnsupportedOperationException | IllegalArgumentException ignored) {}
        } else if (favicon != null) {
            CachedServerIcon icon = bukkit.getFavicon(favicon);
            if (icon != null)
                try {
                    event.setServerIcon(icon);
                } catch (UnsupportedOperationException | IllegalArgumentException ignored) {}
        }
    }

    @Override
    public boolean register() {
        if (this.registered) {
            return false;
        }

        this.registered = true;
        bukkit.registerListener(this);
        return true;
    }

    @Override
    public boolean unregister() {
        if (!this.registered) {
            return false;
        }

        this.registered = false;
        bukkit.unregisterListener(this);
        return true;
    }

}
