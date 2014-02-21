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

package net.minecrell.serverlistplus.api.configuration;

import lombok.EqualsAndHashCode;

import net.minecrell.serverlistplus.api.configuration.util.Description;
import net.minecrell.serverlistplus.api.configuration.util.UniqueName;

@UniqueName ("Plugin")
@Description ({
        "General options about the plugin.",
        "You can disable the player tracking to save some performance if you",
        "don't use it. By setting 'stats' to 'false' or 'off' you can disable",
        "sending anonymous data to a plugin statistic service. The unknown",
        "player name is used instead of the real player name if the player",
        "has not yet logged in to you server yet, therefore his name is unknown."
})
@EqualsAndHashCode (callSuper = false)
public class PluginConfiguration extends Configuration {
    public boolean Stats;
    public boolean PlayerTracking;
    @Deprecated public String UnknownPlayerName;

    @Override
    public void setDefault() {
        this.Stats = true;
        this.PlayerTracking = true;
        this.UnknownPlayerName = "player";
    }
}
