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

import me.clip.placeholderapi.PlaceholderAPI;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.replacement.DynamicReplacer;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public class PlaceholderAPIDynamicReplacer implements DynamicReplacer {

    private final Server server;

    public PlaceholderAPIDynamicReplacer(Server server) {
        this.server = server;
    }

    @Override
    public boolean find(String s) {
        return PlaceholderAPI.containsBracketPlaceholders(s);
    }

    @Override
    public String replace(StatusResponse response, String s) {
        PlayerIdentity identity = response.getRequest().getIdentity();
        if (identity != null) {
            // Note: I'm not sure if getOfflinePlayer is really safe here.
            // Server status pings are handled asynchronously (not on main thread)
            return PlaceholderAPI.setBracketPlaceholders(this.server.getOfflinePlayer(identity.getUuid()), s);
        } else {
            return replace(response.getCore(), s);
        }
    }

    @Override
    public String replace(ServerListPlusCore core, String s) {
        return PlaceholderAPI.setBracketPlaceholders((OfflinePlayer) null, s);
    }

}
