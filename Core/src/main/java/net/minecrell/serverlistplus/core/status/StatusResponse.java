/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your complete server status ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.status;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;

import com.google.common.base.Preconditions;

public class StatusResponse {
    private final StatusRequest request;
    private final StatusManager status;
    private final PlayerFetcher fetcher;

    private Integer online, max; // The cached player count values

    protected StatusResponse(StatusRequest request, StatusManager status, PlayerFetcher fetcher) {
        this.request = Preconditions.checkNotNull(request, "request");
        this.status = Preconditions.checkNotNull(status, "status");
        this.fetcher = fetcher;
    }

    public ServerListPlusCore getCore() {
        return status.getCore();
    }

    public StatusRequest getRequest() {
        return request;
    }

    public StatusManager getStatus() {
        return status;
    }

    public boolean hidePlayers() {
        return status.hidePlayers(this);
    }

    public Integer getOnlinePlayers() {
        return online != null ? online : (this.online = status.getOnlinePlayers(this));
    }

    public Integer fetchOnlinePlayers() {
        if (online == null) {
            // First try to get it from the configuration
            this.online = getOnlinePlayers();
            if (online == null && fetcher != null) {
                // Ok, let's get it from the response instead
                this.online = fetcher.getOnlinePlayers();
            }
        }

        return online;
    }

    public Integer getMaxPlayers() {
        return max != null ? max : (this.max = status.getMaxPlayers(this));
    }

    public Integer fetchMaxPlayers() {
        if (max == null) {
            // First try to get it from the configuration
            this.max = getMaxPlayers();
            if (max == null && fetcher != null) {
                // Ok, let's get it from the response instead
                this.max = fetcher.getMaxPlayers();
            }
        }

        return max;
    }

    public String getDescription() {
        return status.getDescription(this);
    }

    public String getPlayerHover() {
        return status.getPlayerHover(this);
    }

    public String getVersion() {
        return status.getVersion(this);
    }

    public Integer getProtocol() {
        return status.getProtocol(this);
    }

    public FaviconSource getFavicon() {
        return status.getFavicon(this);
    }
}
