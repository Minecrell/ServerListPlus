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

package net.minecrell.serverlistplus.platform;

public final class PlatformType {

    public static final PlatformType BUNGEE = new PlatformType("Bungee", true);

    private final String name;
    private final boolean official;

    public PlatformType(String name) {
        this(name, false);
    }

    private PlatformType(String name, boolean official) {
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
        if (official) {
            return name;
        } else {
            return name + " [Unofficial]";
        }
    }

}
