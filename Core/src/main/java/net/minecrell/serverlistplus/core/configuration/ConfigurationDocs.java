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
            "server list.",
            " - Add multiple entries to let the plugin choose a random one.",
            " - Save the file in valid UTF-8 format if you want to use special",
            "   characters.",
            " - Colors are possible using the usual color codes.",
            "   http://minecraft.gamepedia.com/Formatting_codes",
            " - The Minecraft client will only display the first 2 lines of the",
            "   description. The player hover messages are not limited, but make",
            "   sure it does also fit on smaller screens.",
            " - You can personalize your server list by using '%player%' instead",
            "   of the player name. It will be replaced by the player name or by",
            "   the unknown name in the plugin configuration below if the user",
            "   has not logged in to the server yet."
    };

    public static ServerListConfiguration getServerListExample() {
        ServerListConfiguration config = new ServerListConfiguration();

        config.Description = Arrays.asList(
                Helper.joinLines(
                        "&6Welcome back, %player%!",
                        "&cServer Network &f| &eMinecraft 1.7"),
                Helper.joinLines(
                        "&aWelcome to the &cServer Network, &a%player%!",
                        "&6Today are 2 more kits playable for &2free&6!"));
        config.Players = new ServerListConfiguration.PlayersConfiguration();
        config.Players.Hover = Arrays.asList(
                Helper.joinLines(
                        "&aHey, %player%!",
                        "",
                        "&eJoin our server today and play one of",
                        "&eour amazing mini games!",
                        " &a- Survival Games",
                        " &a- Factions",
                        " &a- PvP",
                        " &a... and many more!"),
                Helper.joinLines(
                        "&6Have you tested the our new PvP kits, %player%?",
                        "&aTodays free kits:",
                        " - &eKangaroo",
                        " - &eArcher",
                        "&cOur Premium kits are &b25% off &ctoday!"
                )
        );

        return config;
    }

    public static final String[] PLUGIN_DESCRIPTION = new String[] {
            "General options about the plugin.",
            " - If you don't want the plugin to send anonymous plugin statistics",
            "   to a service (PluginMetrics) you can disable them here.",
            " - Set the player tracking to 'off' if you don't use it and want",
            "   to save some performance.",
            " - The unknown player name is used instead of the actual player name",
            "   if the player has not logged in to the server yet."
    };

    public static final String[] CORE_DESCRIPTION = new String[] {
            "WARNING: Changing some of the values in this section can possibly",
            "break the plugin or crash your server. Change them at your own risk",
            "and only if you know what you're doing.",
            " - You can change the cache options for the player tracker if you",
            "   want to keep the cached player names by their IP a little bit",
            "   longer than by default."
    };
}
