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

package net.minecrell.serverlistplus.bungee.metrics;

import net.md_5.bungee.api.ProxyServer;
import net.minecrell.serverlistplus.api.metrics.configuration.AbstractMetricsConfiguration;
import net.minecrell.serverlistplus.api.metrics.configuration.DefaultMetricsConfiguration;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class BungeeMetricsConfiguration extends AbstractMetricsConfiguration {
    private final Properties properties;

    protected BungeeMetricsConfiguration() {
        this.properties = new Properties();

        for (ConfigurationEntry entry : ConfigurationEntry.values()) {
            properties.put(entry.getKey(), entry.getDefaultValue());
        }
    }

    public Properties getHandle() {
        return properties;
    }

    protected BungeeMetricsConfiguration load(Path path) throws Exception {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            this.properties.load(reader); return this;
        }
    }

    @Override
    protected String readValue(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    @Override
    public String getUUID() {
        return ProxyServer.getInstance().getConfigurationAdapter().getString("stats", DefaultMetricsConfiguration.getDefaultConfig().getUUID());
    }
}
