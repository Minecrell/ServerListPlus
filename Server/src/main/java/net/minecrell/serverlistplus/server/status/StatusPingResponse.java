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

package net.minecrell.serverlistplus.server.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kyori.adventure.text.Component;

@Data
public final class StatusPingResponse {

    @Data
    @AllArgsConstructor
    public static final class Version {

        private String name;
        private int protocol;

    }

    @Data
    @AllArgsConstructor
    public static final class Players {

        private int online;
        private int max;

        private UserProfile[] sample;

    }

    private Component description = Component.empty();
    private final Version version = new Version("ServerListPlus", -1);
    private Players players = null;
    private String favicon;

}
