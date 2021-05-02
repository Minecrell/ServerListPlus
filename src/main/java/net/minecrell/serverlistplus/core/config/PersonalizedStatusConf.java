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

package net.minecrell.serverlistplus.core.config;

import net.minecrell.serverlistplus.core.util.BooleanOrList;
import net.minecrell.serverlistplus.core.util.IntegerRange;

import java.util.List;

public class PersonalizedStatusConf {
    public StatusConf Default;
    public StatusConf Personalized;
    public StatusConf Banned;

    public static class StatusConf {
        public List<String> Description;
        public PlayersConf Players;
        public VersionConf Version;
        public FaviconConf Favicon;

        public static class PlayersConf {
            public List<IntegerRange> Online, Max;
            public Boolean Hidden;
            public BooleanOrList<String> Hover;
            public List<String> Slots;

            public PlayersConf() {
            }

            // Make it possible to use "Players: false" to hide the player count
            public PlayersConf(boolean b) {
                this.Hidden = !b;
            }
        }

        public static class VersionConf {
            public List<String> Name;
            public Integer Protocol;
        }

        public static class FaviconConf {
            public Boolean Disabled;
            public List<String> Files, Folders;
            public List<String> URLs;
            public List<String> Heads, Helms;
            public List<String> Encoded;

            public FaviconConf() {
            }

            // Make it possible to use "Favicon: false" to disable the favicon
            public FaviconConf(boolean b) {
                this.Disabled = !b;
            }
        }
    }
}
