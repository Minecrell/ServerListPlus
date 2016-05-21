/*
 * ServerListPlus
 * Copyright (C) 2016, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.impl;

public final class ImplementationType {

    public static final ImplementationType BUKKIT = new ImplementationType("Bukkit", true);
    public static final ImplementationType BUNGEE = new ImplementationType("BungeeCord", true);
    public static final ImplementationType SPONGE = new ImplementationType("Sponge", true);
    public static final ImplementationType SERVER = new ImplementationType("Server", true);

    private final String name;
    private final boolean official;

    public ImplementationType(String name) {
        this(name, false);
    }

    private ImplementationType(String name, boolean official) {
        this.name = name;
        this.official = official;
    }

    public String getName() {
        return name;
    }

    public boolean isOfficial() {
        return official;
    }

    @Override
    public String toString() {
        return name + " (" + (official ? "Official" : "Unofficial") + ')';
    }

}
