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

package net.minecrell.serverlistplus.bukkit.integration;

import me.confuser.banmanager.common.api.BmAPI;
import me.confuser.banmanager.common.data.PlayerBanData;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;

import java.util.Date;

public class BanManagerBanProvider implements BanProvider {

    private static PlayerBanData getBan(PlayerIdentity playerIdentity) {
        return BmAPI.getCurrentBan(playerIdentity.getUuid());
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return BmAPI.isBanned(playerIdentity.getUuid());
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final PlayerBanData ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getReason();
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final PlayerBanData ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getActor().getName();
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final PlayerBanData ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        final long expires = ban.getExpires();

        return (expires < 0L) ? null : new Date(expires);
    }

}
