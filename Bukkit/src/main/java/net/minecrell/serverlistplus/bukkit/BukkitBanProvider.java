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

package net.minecrell.serverlistplus.bukkit;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Server;

import java.util.Date;

public class BukkitBanProvider implements BanProvider {

    private final Server server;

    public BukkitBanProvider(Server server) {
        this.server = server;
    }

    private BanList getBanList() {
        return this.server.getBanList(BanList.Type.NAME);
    }

    private BanEntry getBanEntry(PlayerIdentity playerIdentity) {
        return getBanList().getBanEntry(playerIdentity.getName());
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return getBanList().isBanned(playerIdentity.getName());
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final BanEntry banEntry = getBanEntry(playerIdentity);

        if (banEntry == null)
            return null;

        return banEntry.getReason();
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final BanEntry banEntry = getBanEntry(playerIdentity);

        if (banEntry == null)
            return null;

        return banEntry.getSource();
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final BanEntry banEntry = getBanEntry(playerIdentity);

        if (banEntry == null)
            return null;

        return banEntry.getExpiration();
    }

}
