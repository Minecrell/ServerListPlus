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

import lombok.Value;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

@Value
public class VirtualHostAddress implements VirtualHost {
    private static final Pattern SEPARATOR = Pattern.compile(":", Pattern.LITERAL);

    private final String host;
    private final Integer port;
    private final InetSocketAddress address;

    public VirtualHostAddress(String host, Integer port) {
        this.host = Strings.emptyToNull(host);
        this.port = port;
        this.address = host != null && port != null ? new InetSocketAddress(host, port) : null;
    }

    @Override
    public boolean matches(StatusRequest.Target target) {
        InetSocketAddress targetHost = target.getHost();
        if (address != null) return address.equals(targetHost);
        return !(host != null && !host.equalsIgnoreCase(targetHost.getHostString())) &&
                !(port != null && port != targetHost.getPort());
    }

    public static VirtualHostAddress parse(String host) {
        String[] parts = SEPARATOR.split(host);
        host = replaceWildcard(parts[0]);
        if (parts.length == 1) return new VirtualHostAddress(host, null);
        String portString = replaceWildcard(parts[1]);
        Integer port = null;
        if (portString != null) port = Integer.valueOf(portString);
        return new VirtualHostAddress(host, port);
    }

    private static String replaceWildcard(String s) {
        return s.equals("*") ? null : s;
    }
}
