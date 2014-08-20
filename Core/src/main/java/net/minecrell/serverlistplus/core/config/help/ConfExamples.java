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

package net.minecrell.serverlistplus.core.config.help;

import net.minecrell.serverlistplus.core.config.CoreConf;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.ServerStatusConf;
import net.minecrell.serverlistplus.core.util.Helper;

import java.util.Arrays;

public final class ConfExamples {
    private ConfExamples() {}

    public static ServerStatusConf forServerStatus() {
        // Feel free to improve the examples
        ServerStatusConf conf = new ServerStatusConf();

        // Description
        conf.Default = new ServerStatusConf.StatusConf();
        conf.Default.Description = Arrays.asList(
                Helper.lines(
                        "&cA Minecraft Server.",
                        "&cCurrently running &e1.7&c."
                ), "&aSingle lines are also possible."
        );

        // Player hover
        conf.Default.Players = new ServerStatusConf.StatusConf.PlayersConf();
        conf.Default.Players.Hover = Arrays.asList(
                Helper.lines(
                        "&cOur server is currently still",
                        "&cunder construction. We will",
                        "&crelease it soon!"
                ), Helper.lines(
                        "&aWhat does our server offer?",
                        "&6We have:",
                        "  - &6Survival Games",
                        "  - &6Skyblock",
                        "  - &6and much more!"
                )
        );

        // Personalized
        // Description
        conf.Personalized = new ServerStatusConf.StatusConf();
        conf.Personalized.Description = Arrays.asList(
                Helper.lines(
                        "Hello, %player%!",
                        "How are you?"
                ), Helper.lines(
                        "&cA Minecraft Server.",
                        "&eCome and play on our server, %player%!"
                )
        );

        // Player hover
        conf.Personalized.Players = new ServerStatusConf.StatusConf.PlayersConf();
        conf.Personalized.Players.Hover = Arrays.asList(
                Helper.lines(
                        "&aYou are not %player%?",
                        "&aWell that's bad then something went wrong!",
                        "&aYou're probably using the same IP-Address",
                        "&alike someone else playing on our server.",
                        "&eThere is no way to fix this, sorry!"
                )
        );

        return conf;
    }

    public static PluginConf forPlugin() {
        return new PluginConf();
    }

    public static CoreConf forCore() {
        return new CoreConf();
    }
}