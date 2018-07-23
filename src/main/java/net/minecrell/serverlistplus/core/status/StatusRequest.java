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
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.util.Helper;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@Getter
public class StatusRequest {
    private final InetAddress client;
    private final PlayerIdentity identity;
    private @Setter Integer protocolVersion;
    private Target target;

    public StatusRequest(InetAddress client, PlayerIdentity identity) {
        this.client = Preconditions.checkNotNull(client, "client");
        this.identity = identity;
    }

    public boolean isIdentified() {
        return identity != null;
    }

    public void setTarget(String host, int port) {
        setTarget(InetSocketAddress.createUnresolved(cleanVirtualHost(host), port));
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

    /**
     * Returns the cleaned hostname for the input sent by the client.
     *
     * @param host The host sent by the client
     * @return The cleaned hostname
     */
    public static String cleanVirtualHost(String host) {
        // FML appends a marker to the host to recognize FML clients (\0FML\0)
        host = Helper.substringBefore(host, '\0');

        // When clients connect with a SRV record, there host contains a trailing '.'
        if (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }

        return host;
    }

}
