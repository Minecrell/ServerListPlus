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

package net.minecrell.serverlistplus.core.status.hosts;

import lombok.NonNull;
import lombok.Value;
import net.minecrell.serverlistplus.core.status.StatusRequest;

@Value
public class VirtualNamedHost implements VirtualHost {
    protected static final String NAME_PREFIX = "Name/";

    private final @NonNull String name;

    @Override
    public boolean matches(StatusRequest.Target target) {
        return name.equalsIgnoreCase(target.getName());
    }

    public static VirtualNamedHost parse(String host) {
        return new VirtualNamedHost(host);
    }
}
