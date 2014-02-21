/*
 *       __                           __ _     _     ___ _
 *      / _\ ___ _ ____   _____ _ __ / /(_)___| |_  / _ \ |_   _ ___
 *      \ \ / _ \ '__\ \ / / _ \ '__/ / | / __| __|/ /_)/ | | | / __|
 *      _\ \  __/ |   \ V /  __/ | / /__| \__ \ |_/ ___/| | |_| \__ \
 *      \__/\___|_|    \_/ \___|_| \____/_|___/\__\/    |_|\__,_|___/
 *                       Customize your server ping!
 *
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.configuration;

import java.util.Arrays;

import net.minecrell.serverlistplus.api.configuration.ServerListConfiguration;
import net.minecrell.serverlistplus.core.util.Helper;

public class ConfigurationDocs {
    private ConfigurationDocs() {}

    public static final String[] SERVER_LIST_DESCRIPTION = new String[] {
            "Customize your server ping here.",
            "Up to now, you can only change the server description (MotD) and",
            "add custom messages when a player hovers the player amount in the",
            "server list. Randomizing is possible by adding multiple entries",
            "in the correct section. You can personalize your server ping",
            "by adding %player% to replace it with the player's name.",
            "",
            "Below is an example configuration. Remove the comment at the",
            "beginning of the line and the space to test it."
    };

    public static ServerListConfiguration getServerListExample() {
        ServerListConfiguration config = new ServerListConfiguration();

        config.Description = Arrays.asList(
                Helper.joinLines(
                        "&6This server is running &cServerListPlus&6.",
                        "&eMinecraft 1.7"),
                Helper.joinLines(
                        "&aThis is another random message.",
                        "&4Up to 2 lines are possible."
                ));
        config.Players = new ServerListConfiguration.PlayersConfiguration();
        config.Players.Hover = Arrays.asList(
                Helper.joinLines(
                        "&6This messages will be displayed if a player",
                        "&6hovers the player amount in the server list.",
                        "&aThere is no real line limitation, but make sure",
                        "&athat it's still visible completely in the client",
                        "&aalso on smaller displays."),
                Helper.joinLines(
                        "&cAs for the description, randomizing is possible by",
                        "&cadding multiple entries here."),
                Helper.joinLines(
                        "&eColor codes are possible with the usual color codes",
                        "&eusing & and the color code behind it.",
                        "&4If you use special characters make sure saving",
                        "&4the file in valid UTF-8 format."));

        return config;
    }

    public static final String[] PLUGIN_DESCRIPTION = new String[] {
            "General options about the plugin.",
            "You can disable the player tracking to save some performance if you",
            "don't use it. By setting 'stats' to 'false' or 'off' you can disable",
            "sending anonymous data to a plugin statistic service. The unknown",
            "player name is used instead of the real player name if the player",
            "has not yet logged in to you server yet, therefore his name is unknown."
    };

    public static final String[] CORE_DESCRIPTION = new String[] {
            "WARNING: Changing some of the values in this section can possibly",
            "break the plugin or crash your server. Change them at your own risk",
            "and only if you know what you're doing.",
            "But if you're interested: by changing the cache the of the player",
            "tracker, you can keep the players name in the server list for a",
            "longer time. Just change the time below."
    };
}
