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

package net.minecrell.serverlistplus.core.config.help;

import net.minecrell.serverlistplus.core.config.ServerStatusConf;
import net.minecrell.serverlistplus.core.util.Helper;

import java.util.Arrays;

public final class ConfExamples {
    private ConfExamples() {}

    public static ServerStatusConf forServerStatus() {
        ServerStatusConf conf = new ServerStatusConf();
        conf.Default = new ServerStatusConf.StatusConf();
        conf.Default.Description = Arrays.asList(
                Helper.lines(
                        "A Minecraft Server.",
                        "&c(Actually a very boring one...)"
                ), "A Minecrell Server."
        );
        conf.Default.Players = new ServerStatusConf.StatusConf.PlayersConf();
        conf.Default.Players.Hover = Arrays.asList(
                Helper.lines(
                        "Notch",
                        "Dinnerbone",
                        "EvilSeph"
                ), Helper.lines(
                        "Minecrell",
                        "someone else"
                )
        );

        conf.Personalized = new ServerStatusConf.StatusConf();
        conf.Personalized.Description = Arrays.asList(
                Helper.lines(
                        "Ohai, %player%!",
                        "How are you?"
                ), "A %player% Server."
        );
        conf.Personalized.Players = new ServerStatusConf.StatusConf.PlayersConf();
        conf.Personalized.Players.Hover = Arrays.asList(
                Helper.lines(
                        "%player%",
                        "%player%",
                        "%player%"
                )
        );

        return conf;
    }
}