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

package net.minecrell.serverlistplus.core;

import java.util.List;
import java.util.Random;

import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.configuration.ServerListConfiguration;
import net.minecrell.serverlistplus.core.util.CoreServerListPlusManager;
import net.minecrell.serverlistplus.core.util.Helper;

public class ServerListManager extends CoreServerListPlusManager {
    private final Random random = new Random();
    private String[] descriptions;
    private String[][] playerHover;

    public ServerListManager(ServerListPlusCore core) {
        super(core);
    }

    public void reload() {
        ServerListConfiguration config = this.getCore().getConfigManager().get(ServerListConfiguration.class);

        if (!Helper.nullOrEmpty(config.Description)) {
            List<String> configDescriptions = config.Description;
            String[] descriptions = new String[configDescriptions.size()];
            for (int i = 0; i < descriptions.length; i++)
                descriptions[i] = this.getCore().getPlugin().colorizeString(configDescriptions.get(i));
            this.descriptions = descriptions;
        } else this.descriptions = null;

        if (config.Players != null && !Helper.nullOrEmpty(config.Players.Hover)) {
            List<String> configPlayerHover = config.Players.Hover;
            String[][] playerHover = new String[configPlayerHover.size()][];
            for (int i = 0; i < playerHover.length; i++)
                playerHover[i] = Helper.splitLines(this.getCore().getPlugin().colorizeString(
                        configPlayerHover.get(i)));
            this.playerHover = playerHover;
        } else this.playerHover = null;
    }

    public String getDescription() {
        if (descriptions == null) return null;
        return Helper.nextEntry(random, descriptions);
    }

    public String[] getPlayerHover() {
        if (playerHover == null) return null;
        return Helper.nextEntry(random, playerHover);
    }

}
