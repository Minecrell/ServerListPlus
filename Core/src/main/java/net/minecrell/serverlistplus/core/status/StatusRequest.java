/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
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

import lombok.NonNull;
import lombok.Value;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.google.common.base.Preconditions;

public class StatusRequest {
    private final InetAddress client;
    private final PlayerIdentity identity;
    private int protocolVersion;
    private Target target;

    public StatusRequest(InetAddress client, PlayerIdentity identity) {
        this.client = Preconditions.checkNotNull(client, "client");
        this.identity = identity;
    }

    public InetAddress getClient() {
        return client;
    }

    public PlayerIdentity getIdentity() {
        return identity;
    }

    public boolean isIdentified() {
        return identity != null;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(InetSocketAddress host) {
        setTarget(host, null);
    }

    public void setTarget(InetSocketAddress host, String name) {
        setTarget(new Target(host, name));
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public StatusResponse createResponse(StatusManager status) {
        return createResponse(status, null);
    }

    public StatusResponse createResponse(StatusManager status, ResponseFetcher fetcher) {
        return new StatusResponse(this, status, fetcher);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatusRequest)) return false;
        StatusRequest that = (StatusRequest) o;
        return client.equals(that.client);
    }

    @Override
    public int hashCode() {
        return client.hashCode();
    }

    @Value
    public static class Target {
        private final @NonNull InetSocketAddress host;
        private final String name;
    }
}
