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

public class ConfigurationExamples {
    private ConfigurationExamples() {}

    public static ServerListConfiguration getServerListExample() {
        ServerListConfiguration config = new ServerListConfiguration();

        config.Description = Arrays.asList(
                Helper.joinLines(
                        "&6This server is running &cServerListPlus&6.",
                        "&eIf you add multiple entries here one will be randomly chosen."),
                Helper.joinLines(
                        "&aThis is another random message.",
                        "&4Up to 2 lines are possible (&aper limitation of Minecraft&4)"
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
}
