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

package net.minecrell.serverlistplus.core.plugin;

/**
 * Represents the server implementation running the ServerListPlus plugin container.
 */
public enum ServerType {
    BUKKIT ("Bukkit"), SPIGOT ("Spigot"), PAPER("Paper"),
    BUNGEE ("BungeeCord"),
    VELOCITY("Velocity"),
    CANARY ("Canary"),
    SPONGE ("Sponge"),
    SERVER ("Server"),
    CUSTOM;

    private final String displayName;

    ServerType() { this(null); }

    ServerType(String displayName) {
        this.displayName = displayName;
    }

    public boolean hasWeirdRGB() {
        switch (this) {
            case BUKKIT:
            case SPIGOT:
            case PAPER:
            case BUNGEE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return (displayName != null) ? displayName : super.toString();
    }
}
