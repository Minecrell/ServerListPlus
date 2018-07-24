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

package net.minecrell.serverlistplus.canary;

import net.canarymod.Canary;
import net.canarymod.bansystem.Ban;
import net.canarymod.bansystem.BanManager;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import net.minecrell.serverlistplus.core.util.UUIDs;

import java.util.Date;

public class CanaryBanProvider implements BanProvider {

    private static String getUUID(PlayerIdentity playerIdentity) {
        return UUIDs.STANDARD.toString(playerIdentity.getUuid());
    }

    private static BanManager getBans() {
        return Canary.bans();
    }

    private static Ban getBan(PlayerIdentity playerIdentity) {
        return getBans().getBan(getUUID(playerIdentity));
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return getBans().isBanned(getUUID(playerIdentity));
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getReason();
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getBanningPlayer();
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        final long expiration = ban.getExpiration();

        return (expiration < 0L) ? null : new Date(expiration);
    }

}
