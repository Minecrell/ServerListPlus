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

package net.minecrell.serverlistplus.core.config.help;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.ServerStatusConf;
import net.minecrell.serverlistplus.core.replacement.rgb.RGBFormat;
import net.minecrell.serverlistplus.core.util.BooleanOrList;
import net.minecrell.serverlistplus.core.util.Helper;

import java.util.ArrayList;
import java.util.Arrays;

public final class Examples {
    private Examples() {}

    public static ServerStatusConf forServerStatus(ServerListPlusCore core) {
        // Feel free to improve the examples
        ServerStatusConf conf = new ServerStatusConf();

        // Description
        conf.Default = new ServerStatusConf.StatusConf();
        conf.Default.Description = new ArrayList<>(Arrays.asList(
                Helper.joinLines(
                        "&aA Minecraft Server.",
                        "&7Now with [&a&lPvP&7], [&a&lMinigames&7], and much more!"
                ), Helper.joinLines(
                        "&aA Minecraft Server.",
                        "&eNow running the latest &lMinecraft &eversion!"
                )
        ));

        if (core.getPlugin().getRGBFormat() != RGBFormat.UNSUPPORTED) {
            conf.Default.Description.add(Helper.joinLines(
                    "&a&#45bf55A Minecraft &#0288d1Server.",
                    "&e%gradient#ffbe00#f4511e%Now with RGB colors. Nice!%gradient%"
            ));
        }

        // Player hover
        conf.Default.Players = new ServerStatusConf.StatusConf.PlayersConf();
        conf.Default.Players.Hover = BooleanOrList.of(
                Helper.joinLines(
                        "&aWelcome to &lA Minecraft Server&a!",
                        "&aCurrently &e&l%online%/%max% &aplayers are playing on our server!"
                )
        );

        // Personalized
        // Description
        conf.Personalized = new ServerStatusConf.StatusConf();
        conf.Personalized.Description = new ArrayList<>(Arrays.asList(
                Helper.joinLines(
                        "&aA Minecraft Server. &7|  &eHello, &l%player%!",
                        "&7Now with [&a&lPvP&7], [&a&lMinigames&7], and much more!"
                ), Helper.joinLines(
                        "&aA Minecraft Server. &7|  &eHello, &l%player%!",
                        "&eNow running the latest &lMinecraft &eversion!"
                )
        ));

        if (core.getPlugin().getRGBFormat() != RGBFormat.UNSUPPORTED) {
            conf.Personalized.Description.add(Helper.joinLines(
                    "&a&#45bf55A Minecraft &#0288d1Server.",
                    "&e%gradient#ffbe00#f4511e%Now with RGB colors. Hello %player%!%gradient%"
            ));
        }

        // Player hover
        conf.Personalized.Players = new ServerStatusConf.StatusConf.PlayersConf();
        conf.Personalized.Players.Hover = BooleanOrList.of(
                Helper.joinLines(
                        "&aWelcome back, &l%player%&a!",
                        "&aCurrently &e&l%online%/%max% &aplayers are playing on our server!"
                )
        );

        return conf;
    }

    public static PluginConf forPlugin() {
        return new PluginConf();
    }
}