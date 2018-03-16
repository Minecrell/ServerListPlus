/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
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

package net.minecrell.serverlistplus.core.config;

import static net.minecrell.serverlistplus.core.favicon.ResizeStrategy.SCALE;

import net.minecrell.serverlistplus.core.config.help.Description;
import net.minecrell.serverlistplus.core.favicon.ResizeStrategy;
import net.minecrell.serverlistplus.core.player.JSONIdentificationStorage;
import net.minecrell.serverlistplus.core.util.TimeUnitValue;

import java.util.concurrent.TimeUnit;

@Description({
        "Stats: Enable/disable sending plugin statistics.",
        "PlayerTracking: Enable/disable tracking of player names to their IP-Addresses.",
        "  - Persistence can be disabled with the toggle in the Storage section.",
        "Samples: Toggle using multiple player infos for the player hover message.",
        "    May prevent the annoying ... and x more ... message for bigger servers",
        "    DynamicPlayers is special and aligns the number with the number of",
        "    random players used in placeholders.",
        "Unknown: Placeholder replacement if the real value is unknown.",
        "Favicon: Options for the creation / downloading of favicons:",
        " - RecursiveFolderSearch: Also search for favicons in sub directories.",
        " - SkinSource: The URL to get the Minecraft Skins from. (%s -> player name)",
        " - ResizeStrategy: The strategy used to resize too small or too big favicons.",
        "     NONE (keep them as is, will probably fail), SCALE (scale them to the correct size)"
})
public class PluginConf {
    public boolean Stats = true;
    public PlayerTrackingConf PlayerTracking = new PlayerTrackingConf();
    public SamplesConf Samples = new SamplesConf();
    public UnknownConf Unknown = new UnknownConf();
    public FaviconConf Favicon = new FaviconConf();

    public static class PlayerTrackingConf {
        public boolean Enabled = true;
        public StorageConf Storage = new JSONIdentificationStorage.Conf();

        public PlayerTrackingConf() {}

        public PlayerTrackingConf(boolean enabled)  {
            this.Enabled = enabled;
        }

        public static abstract class StorageConf {
            public boolean Enabled = true;
        }
    }

    public static class SamplesConf {
        public boolean Multiple = false;
        public boolean DynamicPlayers = false;
    }

    public static class UnknownConf {
        public String PlayerName = "player";
        public String PlayerCount = "???";
        public String Date = "???";
        public String BanReason = "some reason";
        public String BanOperator = "somebody";
        public String BanExpirationDate = "never";
    }

    public static class FaviconConf {
        public TimeUnitValue Timeout = new TimeUnitValue(TimeUnit.SECONDS, 10);
        public boolean RecursiveFolderSearch = false;
        public ResizeStrategy ResizeStrategy = SCALE;
    }
}
