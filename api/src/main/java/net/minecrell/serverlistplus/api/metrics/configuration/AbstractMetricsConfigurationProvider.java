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

import net.minecrell.serverlistplus.api.metrics.configuration.AbstractMetricsConfiguration.ConfigurationEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractMetricsConfigurationProvider implements MetricsConfigurationProvider {
    protected AbstractMetricsConfigurationProvider() {}

    @Override
    public final MetricsConfiguration loadConfiguration() throws Exception {
        Path configPath = this.getConfigurationPath();
        if (Files.notExists(configPath)) {
            Files.createDirectories(configPath.getParent());
            this.createConfiguration();
        }

        return load();
    }

    public abstract Path getConfigurationPath();
    public abstract void createConfiguration() throws IOException;

    protected abstract MetricsConfiguration load() throws Exception;
}
