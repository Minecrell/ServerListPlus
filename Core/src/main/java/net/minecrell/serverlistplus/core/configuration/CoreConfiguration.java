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

import lombok.EqualsAndHashCode;

import net.minecrell.serverlistplus.api.configuration.Configuration;
import net.minecrell.serverlistplus.api.configuration.ConfigurationPart;
import net.minecrell.serverlistplus.api.configuration.util.Description;
import net.minecrell.serverlistplus.api.configuration.util.UniqueName;

@UniqueName ("Core")
@Description({
        "WARNING: Changing some of the values in this section can possibly",
        "break the plugin or crash your server. Change them at your own risk",
        "and only if you know what you're doing.",
        "But if you're interested: by changing the cache the of the player",
        "tracker, you can keep the players name in the server list for a",
        "longer time. Just change the time below."
})
@EqualsAndHashCode(callSuper = false)
public class CoreConfiguration extends Configuration {
    public CacheConfiguration Caches;

    @EqualsAndHashCode (callSuper = false)
    public static class CacheConfiguration extends ConfigurationPart {
        public String PlayerTracking;

        @Override
        public void setDefault() {
            this.PlayerTracking = "expireAfterWrite=2h";
        }
    }

    @Override
    public void setDefault() {
        this.Caches = new CacheConfiguration();
    }
}
