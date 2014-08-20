/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your complete server status ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.replacement;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.Helper;

public enum DefaultLiteralPlaceholder implements DynamicPlaceholder {
    PLAYER ("%player%") {
        @Override
        public String replace(StatusResponse response, String s) {
            PlayerIdentity identity = response.getRequest().getIdentity();
            return identity != null ? replace(s, identity.getName()) : super.replace(response, s);
        }

        @Override
        public String replace(ServerListPlusCore core, String s) {
            // Use unknown player name
            return replace(s, core.getConf(PluginConf.class).Unknown.PlayerName);
        }
    },
    ONLINE_PLAYERS ("%online%") {
        @Override
        public String replace(StatusResponse response, String s) {
            Integer online = response.fetchOnlinePlayers();
            return online != null ? replace(s, online.toString()) : super.replace(response, s);
        }

        @Override
        public String replace(ServerListPlusCore core, String s) {
            // Use unknown player count
            return replace(s, core.getConf(PluginConf.class).Unknown.PlayerCount);
        }
    },
    MAX_PLAYERS ("%max%") {
        @Override
        public String replace(StatusResponse response, String s) {
            Integer max = response.fetchMaxPlayers();
            return max != null ? replace(s, max.toString()) : super.replace(response, s);
        }

        @Override
        public String replace(ServerListPlusCore core, String s) {
            // Use unknown player count
            return replace(s, core.getConf(PluginConf.class).Unknown.PlayerCount);
        }
    },
    RANDOM_PLAYER ("%random_player%") {
        @Override
        public String replace(ServerListPlusCore core, String s) {
            return replace(s, core.getPlugin().getRandomPlayer());
        }
    };

    protected final String literal;

    private DefaultLiteralPlaceholder(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public boolean find(String s) {
        return s.contains(literal);
    }

    @Override
    public String replace(StatusResponse response, String s) {
        return replace(response.getCore(), s);
    }

    @Override
    public String replace(String s, Object replacement) {
        return Helper.replace(literal, s, replacement);
    }


}
