/*
 * ServerListPlus
 * Copyright (C) 2016, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.config;

import net.minecrell.serverlistplus.util.NonnullByDefault;

@NonnullByDefault
public class PluginConfig {

    @Option(name = "Metrics")
    private boolean metrics = true;

    @Option(name = "PlayerTracking")
    private PlayerTracking playerTracking = new PlayerTracking();

    public boolean isMetrics() {
        return metrics;
    }

    public void setMetrics(boolean metrics) {
        this.metrics = metrics;
    }

    public PlayerTracking getPlayerTracking() {
        return playerTracking;
    }

    public void setPlayerTracking(PlayerTracking playerTracking) {
        this.playerTracking = playerTracking;
    }

    public static class PlayerTracking {

        @Option(name = "Enabled")
        private boolean enabled = true;

    }

}
