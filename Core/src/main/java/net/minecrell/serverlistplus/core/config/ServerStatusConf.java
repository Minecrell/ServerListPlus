/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your server list ping!
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

package net.minecrell.serverlistplus.core.config;

import net.minecrell.serverlistplus.core.config.help.Description;

import java.util.List;

@Description({
        "Customize your server list status here.",
        "Currently, you can change the status description (MotD) and add custom messages",
        "when a player hovers the player amount in the server list.",
        " - Add multiple entries for random messages.",
        " - Save the file in valid UTF-8 format for special characters.",
        " - Color codes are possible using the usual color codes.",
        " - The default status is used when the player name is unknown, in the personalized",
        "   status you can use '%player' to replace it with the player's name."
})
public class ServerStatusConf {
    public StatusConf Default;
    public StatusConf Personalized;

    public static class StatusConf {
        public List<String> Description;
        public PlayersConf Players;

        public static class PlayersConf {
            public List<String> Hover;
        }
    }

}
