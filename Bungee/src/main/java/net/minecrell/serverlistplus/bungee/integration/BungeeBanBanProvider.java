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

package net.minecrell.serverlistplus.bungee.integration;

import de.lucavinci.bungeeban.BungeeBan;
import de.lucavinci.bungeeban.BungeeBanApi;
import de.lucavinci.bungeeban.util.Ban;
import de.lucavinci.bungeeban.util.BungeeBanPlayer;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;

import java.util.Date;

public class BungeeBanBanProvider implements BanProvider {

    private static BungeeBanApi getAPI() {
        return BungeeBan.getApi();
    }

    private static BungeeBanPlayer getPlayer(PlayerIdentity playerIdentity) {
        return getAPI().getPlayer(playerIdentity.getUuid());
    }

    private static Ban getBan(PlayerIdentity playerIdentity) {
        return getPlayer(playerIdentity).getActiveBan();
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return getPlayer(playerIdentity).isBanned();
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getBanReason();
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getBannedBy();
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        final long expires = ban.getBanEnd();

        return (expires < 0L) ? null : new Date(expires);
    }

}
