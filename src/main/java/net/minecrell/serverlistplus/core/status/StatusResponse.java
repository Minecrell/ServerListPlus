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

package net.minecrell.serverlistplus.core.status;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.status.hosts.VirtualHost;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StatusResponse {
    // Used for custom player slots features to make client believe the server is outdated and show custom version text
    public static final int MAGIC_FUTURE_VERSION = 9999;

    private final @Getter StatusRequest request;
    private final @Getter StatusManager status;
    private final ResponseFetcher fetcher;

    private final @Getter Set<VirtualHost> matchingHosts;

    private Integer online, max; // The cached player count values
    private boolean playerSlots;

    private Iterator<String> randomPlayers;
    private Map<String, Iterator<String>> randomPlayersMap;

    protected StatusResponse(StatusRequest request, StatusManager status, ResponseFetcher fetcher) {
        this.request = Preconditions.checkNotNull(request, "request");
        this.status = Preconditions.checkNotNull(status, "status");
        this.fetcher = fetcher;

        Set<VirtualHost> matchingHosts = null;

        if (request.getTarget() != null && !status.getHosts().isEmpty()) {
            ImmutableSet.Builder<VirtualHost> builder = ImmutableSet.builder();
            for (VirtualHost host : status.getHosts().keySet())
                if (host.matches(request.getTarget())) builder.add(host);
            if ((matchingHosts = builder.build()).isEmpty()) matchingHosts = null;
        }

        this.matchingHosts = matchingHosts;
    }

    public ServerListPlusCore getCore() {
        return status.getCore();
    }

    public Iterator<String> getRandomPlayers() {
        if (randomPlayers != null) return randomPlayers;
        return this.randomPlayers = getCore().getPlugin().getRandomPlayers();
    }

    public Iterator<String> getRandomPlayers(String location) {
        if (randomPlayersMap == null) this.randomPlayersMap = new HashMap<>();
        if (randomPlayersMap.containsKey(location)) return randomPlayersMap.get(location);
        Iterator<String> result = getCore().getPlugin().getRandomPlayers(location);
        randomPlayersMap.put(location, result);
        return result;
    }

    public boolean hidePlayers() {
        Boolean result;
        if (matchingHosts != null) {
            for (VirtualHost host : matchingHosts) {
                result = status.getHosts().get(host).hidePlayers(this);
                if (result != null) return result;
            }
        }

        return (result = status.getPatch().hidePlayers(this)) != null ? result : false;
    }

    public Integer getOnlinePlayers() {
        if (online != null) return online;

        if (matchingHosts != null) {
            for (VirtualHost host : matchingHosts) {
                this.online = status.getHosts().get(host).getOnlinePlayers(this);
                if (online != null) return online;
            }
        }

        return this.online = status.getPatch().getOnlinePlayers(this);
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
        if (max != null) return max;

        if (matchingHosts != null) {
            for (VirtualHost host : matchingHosts) {
                this.max = status.getHosts().get(host).getMaxPlayers(this);
                if (max != null) return max;
            }
        }

        return this.max = status.getPatch().getMaxPlayers(this);
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
        if (matchingHosts != null) {
            String result;
            for (VirtualHost host : matchingHosts) {
                result = status.getHosts().get(host).getDescription(this);
                if (result != null) return result;
            }
        }

        return status.getPatch().getDescription(this);
    }

    public String getPlayerHover() {
        if (matchingHosts != null) {
            String result;
            for (VirtualHost host : matchingHosts) {
                result = status.getHosts().get(host).getPlayerHover(this);
                if (result != null) return result;
            }
        }

        return status.getPatch().getPlayerHover(this);
    }

    public String getPlayerSlots() {
        if (matchingHosts != null) {
            String result;
            for (VirtualHost host : matchingHosts) {
                result = status.getHosts().get(host).getPlayerSlots(this);
                if (result != null) return result;
            }
        }

        return status.getPatch().getPlayerSlots(this);
    }

    public String getVersion() {
        String result;
        if (request.getProtocolVersion() != null) {
            // We might have already patched the response, so responseVersion might be == MAGIC_FUTURE_VERSION
            // In that case, assume that displaying player slots is fine, since the client is probably not outdated
            int responseVersion = fetcher.getProtocolVersion();
            if (responseVersion == request.getProtocolVersion() || responseVersion == MAGIC_FUTURE_VERSION) {
                result = getPlayerSlots();
                if (result != null) {
                    playerSlots = true;
                    return result;
                }
            }
        }

        if (matchingHosts != null) {
            for (VirtualHost host : matchingHosts) {
                result = status.getHosts().get(host).getVersion(this);
                if (result != null) return result;
            }
        }

        return status.getPatch().getVersion(this);
    }

    public Integer getProtocolVersion() {
        if (playerSlots) return MAGIC_FUTURE_VERSION;

        if (matchingHosts != null) {
            Integer result;
            for (VirtualHost host : matchingHosts) {
                result = status.getHosts().get(host).getProtocolVersion(this);
                if (result != null) return result;
            }
        }

        return status.getPatch().getProtocolVersion(this);
    }

    public FaviconSource getFavicon() {
        if (matchingHosts != null) {
            FaviconSource result;
            for (VirtualHost host : matchingHosts) {
                result = status.getHosts().get(host).getFavicon(this);
                if (result != null) return result;
            }
        }

        return status.getPatch().getFavicon(this);
    }
}
