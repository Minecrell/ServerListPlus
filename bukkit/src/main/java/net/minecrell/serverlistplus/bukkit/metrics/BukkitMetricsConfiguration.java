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

package net.minecrell.serverlistplus.bukkit.metrics;

import net.minecrell.serverlistplus.api.metrics.configuration.AbstractMetricsConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.file.Path;

public class BukkitMetricsConfiguration extends AbstractMetricsConfiguration {
    private final YamlConfiguration yamlConfig;

    protected BukkitMetricsConfiguration() {
        this.yamlConfig = new YamlConfiguration();

        for (ConfigurationEntry entry : ConfigurationEntry.values()) {
            yamlConfig.addDefault(entry.getKey(), entry.getDefaultValue());
        }
    }

    protected BukkitMetricsConfiguration load(Path path) throws Exception {
        this.yamlConfig.load(path.toFile()); return this;
    }

    public YamlConfiguration getHandle() {
        return yamlConfig;
    }

    @Override
    protected String readValue(String key, String defaultValue) {
        return yamlConfig.getString(key, defaultValue);
    }
}
