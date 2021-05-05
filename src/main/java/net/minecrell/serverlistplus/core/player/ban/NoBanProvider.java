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

package net.minecrell.serverlistplus.core.player.ban;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;

import java.util.Date;

public final class NoBanProvider implements BanProvider {

    public static final BanProvider INSTANCE = new NoBanProvider();

    private NoBanProvider() {
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return false;
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        return null;
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        return null;
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        return null;
    }
}
