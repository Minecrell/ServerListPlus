/*
 * ServerListPlus - Customize your server's ping information!
 * Copyright (C) 2013, Minecrell
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

package net.minecrell.serverlistplus.api.metrics.configuration;

import java.util.UUID;

public class DefaultMetricsConfiguration implements MetricsConfiguration {
    private static final String uuid = UUID.randomUUID().toString();

    private static final DefaultMetricsConfiguration defaultConfig = new DefaultMetricsConfiguration();

    public static DefaultMetricsConfiguration getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public boolean isOptOut() {
        return false;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public boolean debugEnabled() {
        return false;
    }
}
