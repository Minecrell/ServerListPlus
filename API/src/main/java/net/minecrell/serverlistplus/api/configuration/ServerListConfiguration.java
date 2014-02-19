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

import java.util.List;

import net.minecrell.serverlistplus.api.configuration.util.Description;
import net.minecrell.serverlistplus.api.configuration.util.UniqueName;

@UniqueName ("ServerList")
@Description ("This is the server list configuration.")
@EqualsAndHashCode (callSuper = false)
public class ServerListConfiguration extends Configuration {
    public List<String> Motd;
    public PlayersConfiguration Players;

    @Override
    public void setDefault() {
        this.Motd = null;
        this.Players = null;
    }

    @EqualsAndHashCode(callSuper = false)
    public static class PlayersConfiguration extends ConfigurationPart {
        public List<String> Hover;

        @Override
        public void setDefault() {
            this.Hover = null;
        }
    }
}
