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
import net.minecrell.serverlistplus.core.status.StatusResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DefaultPatternPlaceholder implements DynamicPlaceholder {
    ONLINE_AT (Pattern.compile("%online@(\\w+)%")) {
        @Override
        public String replace(ServerListPlusCore core, String s) {
            Matcher matcher = matcher(s);
            if (!matcher.find()) return s;

            final String unknown = Matcher.quoteReplacement(core.getConf(PluginConf.class).Unknown.PlayerCount);
            StringBuffer result = new StringBuffer();

            do {
                Integer players = core.getPlugin().getOnlinePlayersAt(matcher.group(1));
                matcher.appendReplacement(result, players != null ? players.toString() : unknown);
            } while (matcher.find());

            matcher.appendTail(result);
            return result.toString();
        }
    };

    private final Pattern pattern;

    private DefaultPatternPlaceholder(Pattern pattern) {
        this.pattern = pattern;
    }

    public Matcher matcher(String s) {
        return pattern.matcher(s);
    }

    @Override
    public boolean find(String s) {
        return matcher(s).find();
    }

    @Override
    public String replace(String s, Object replacement) {
        return matcher(s).replaceAll(Matcher.quoteReplacement(replacement.toString()));
    }

    @Override
    public String replace(StatusResponse response, String s) {
        return replace(response.getCore(), s);
    }
}
