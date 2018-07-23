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

package net.minecrell.serverlistplus.core.player.ban.integration;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import net.minecrell.serverlistplus.core.util.UUIDs;

import java.util.Date;

public class AdvancedBanBanProvider implements BanProvider {

    private static String getUUID(PlayerIdentity playerIdentity) {
        return UUIDs.NO_DASHES.toString(playerIdentity.getUuid());
    }

    private static Punishment getBan(PlayerIdentity playerIdentity) {
        return PunishmentManager.get().getBan(getUUID(playerIdentity));
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return PunishmentManager.get().isBanned(getUUID(playerIdentity));
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final Punishment punishment = getBan(playerIdentity);

        if (punishment == null)
            return null;

        return punishment.getReason();
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final Punishment punishment = getBan(playerIdentity);

        if (punishment == null)
            return null;

        return punishment.getOperator();
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final Punishment punishment = getBan(playerIdentity);

        if (punishment == null)
            return null;

        final long expiration = punishment.getEnd();

        return (expiration < 0L) ? null : new Date(expiration);
    }

}
