/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.core.config;

import net.minecrell.serverlistplus.core.util.IntegerRange;

import java.util.List;

public class PersonalizedStatusConf {
    public StatusConf Default;
    public StatusConf Personalized;

    public static class StatusConf {
        public List<String> Description;
        public PlayersConf Players;
        public VersionConf Version;
        public FaviconConf Favicon;

        public static class PlayersConf {
            public List<IntegerRange> Online, Max;
            public Boolean Hidden;
            public List<String> Hover;
            public List<String> Slots;
        }

        public static class VersionConf {
            public List<String> Name;
            public Integer Protocol;
        }

        public static class FaviconConf {
            public List<String> Files, Folders;
            public List<String> URLs;
            public List<String> Heads, Helms;
            public List<String> Encoded;
        }
    }
}
