/*
 * ServerListPlus
 * Copyright (C) 2017 Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.bungee.status;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.minecrell.serverlistplus.status.Favicon;
import net.minecrell.serverlistplus.status.PlayerProfile;
import net.minecrell.serverlistplus.status.StatusClient;
import net.minecrell.serverlistplus.status.StatusPing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

class BungeeStatusPing implements StatusPing {

    private final StatusClient client;
    private final ServerPing response;

    @Nullable private ServerPing.Players hiddenPlayers;
    @Nullable private BungeeFavicon bungeeFavicon;

    BungeeStatusPing(StatusClient client, ServerPing response) {
        this.client = client;
        this.response = response;
    }

    @Override
    public StatusClient getClient() {
        return client;
    }

    @Override
    public String getDescription() {
        return BaseComponent.toLegacyText(response.getDescriptionComponent());
    }

    @Override
    public void setDescription(String description) {
        response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(description)));
    }

    @Override
    public boolean isHidePlayers() {
        return response.getPlayers() == null;
    }

    @Override
    public void setHidePlayers(boolean hidePlayers) {
        // Check if any changes are necessary
        if (isHidePlayers() == hidePlayers) {
            return;
        }

        if (hidePlayers) {
            this.hiddenPlayers = response.getPlayers();
            response.setPlayers(null);
        } else if (this.hiddenPlayers != null) {
            response.setPlayers(this.hiddenPlayers);
        } else {
            throw new UnsupportedOperationException("Cannot restore players hidden by another plugin");
        }
    }

    @Override
    public int getOnlinePlayers() {
        ServerPing.Players players = response.getPlayers();
        return players != null ? players.getOnline() : -1;
    }

    @Override
    public void setOnlinePlayers(int online) {
        ServerPing.Players players = response.getPlayers();
        if (players != null) {
            players.setOnline(online);
        }
    }

    @Override
    public int getMaxPlayers() {
        ServerPing.Players players = response.getPlayers();
        return players != null ? players.getMax() : -1;
    }

    @Override
    public void setMaxPlayers(int max) {
        ServerPing.Players players = response.getPlayers();
        if (players != null) {
            players.setMax(max);
        }
    }

    @Override
    public List<PlayerProfile> getPlayerProfiles() {
        ServerPing.Players players = response.getPlayers();
        if (players == null) {
            return Collections.emptyList();
        }

        ServerPing.PlayerInfo[] sample = players.getSample();
        if (sample == null || sample.length == 0) {
            return Collections.emptyList();
        }

        ArrayList<PlayerProfile> profiles = new ArrayList<>(sample.length);
        for (ServerPing.PlayerInfo profile : sample) {
            profiles.add(new PlayerProfile(profile.getUniqueId(), profile.getName()));
        }

        return profiles;
    }

    @Override
    public void setPlayerProfiles(List<PlayerProfile> profiles) {
        ServerPing.Players players = response.getPlayers();
        if (players == null) {
            return;
        }

        if (profiles.isEmpty()) {
            players.setSample(null);
            return;
        }

        ServerPing.PlayerInfo[] sample = new ServerPing.PlayerInfo[profiles.size()];
        for (int i = 0; i < sample.length; i++) {
            PlayerProfile profile = profiles.get(i);
            sample[i] = new ServerPing.PlayerInfo(profile.getName(), profile.getUniqueId());
        }

        players.setSample(sample);
    }

    @Override
    public String getVersion() {
        return response.getVersion().getName();
    }

    @Override
    public void setVersion(String version) {
        response.getVersion().setName(version);
    }

    @Override
    public int getProtocolVersion() {
        return response.getVersion().getProtocol();
    }

    @Override
    public void setProtocolVersion(int protocolVersion) {
        response.getVersion().setProtocol(protocolVersion);
    }

    @Override
    @Nullable
    public Favicon getFavicon() {
        net.md_5.bungee.api.Favicon favicon = response.getFaviconObject();
        if (favicon == null) {
            this.bungeeFavicon = null;
            return null;
        }

        if (this.bungeeFavicon != null && favicon == this.bungeeFavicon.getFavicon()) {
            return this.bungeeFavicon;
        }

        return this.bungeeFavicon = new BungeeFavicon(favicon);
    }

    @Override
    public void setFavicon(@Nullable Favicon favicon) throws UnsupportedOperationException {
        if (favicon == null) {
            response.setFavicon((net.md_5.bungee.api.Favicon) null);
            this.bungeeFavicon = null;
            return;
        }

        if (!(favicon instanceof BungeeFavicon)) {
            throw new UnsupportedOperationException("Expected favicon of type BungeeFavicon");
        }

        BungeeFavicon bungeeFavicon = (BungeeFavicon) favicon;
        response.setFavicon(bungeeFavicon.getFavicon());
        this.bungeeFavicon = bungeeFavicon;
    }

}
