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

import net.md_5.bungee.api.plugin.Plugin;
import net.minecrell.serverlistplus.api.metrics.configuration.AbstractMetricsConfigurationProvider;
import net.minecrell.serverlistplus.api.metrics.configuration.MetricsConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class BungeeMetricsConfigurationProvider extends AbstractMetricsConfigurationProvider {
    private final Plugin plugin;

    public BungeeMetricsConfigurationProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public Path getConfigurationPath() {
        return new File(new File(plugin.getDataFolder().getParentFile(), "PluginMetrics"), "config.yml").toPath();
    }

    @Override
    public void createConfiguration() throws IOException {
        BungeeMetricsConfiguration config = new BungeeMetricsConfiguration();
        Path configPath = this.getConfigurationPath();

        Files.createFile(configPath);

        try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
            config.getHandle().store(writer, "http://mcstats.org");
        }
    }

    @Override
    protected MetricsConfiguration load() throws Exception {
        return null;
    }
}
