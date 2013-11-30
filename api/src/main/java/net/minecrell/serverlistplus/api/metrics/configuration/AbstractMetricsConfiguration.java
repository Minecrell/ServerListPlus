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

public abstract class AbstractMetricsConfiguration implements MetricsConfiguration {
    public static enum ConfigurationEntry {
        OPT_OUT ("opt-out", DefaultMetricsConfiguration.getDefaultConfig().isOptOut()),
        UUID ("guid", DefaultMetricsConfiguration.getDefaultConfig().getUUID()),
        DEBUG ("debug", DefaultMetricsConfiguration.getDefaultConfig().debugEnabled());

        private final String key;
        private final Object defaultValue;

        private ConfigurationEntry(String key, Object defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getKey() {
            return key;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    protected AbstractMetricsConfiguration() {}

    protected String readValue(ConfigurationEntry entry) {
        return readValue(entry.getKey(), entry.getDefaultValue().toString());
    }

    protected abstract String readValue(String key, String defaultValue);

    protected boolean parseBoolean(String s) {
        return Boolean.parseBoolean(s);
    }

    @Override
    public boolean isOptOut() {
        return parseBoolean(readValue(ConfigurationEntry.OPT_OUT));
    }

    @Override
    public String getUUID() {
        return readValue(ConfigurationEntry.UUID);
    }

    @Override
    public boolean debugEnabled() {
        return parseBoolean(readValue(ConfigurationEntry.DEBUG));
    }
}
