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

import net.minecrell.serverlistplus.api.metrics.configuration.AbstractMetricsConfiguration.ConfigurationEntry;
import net.minecrell.serverlistplus.api.metrics.configuration.AbstractMetricsConfigurationProvider;
import net.minecrell.serverlistplus.api.metrics.configuration.MetricsConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class BukkitMetricsConfigurationProvider extends AbstractMetricsConfigurationProvider {
    private final Plugin plugin;

    public BukkitMetricsConfigurationProvider(Plugin plugin) {
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
        YamlConfiguration yamlConfig = new BukkitMetricsConfiguration().getHandle();
        yamlConfig.options().header("http://mcstats.org").copyDefaults(true);
        yamlConfig.save(this.getConfigurationPath().toFile());
    }

    @Override
    protected MetricsConfiguration load() throws Exception {
        return new BukkitMetricsConfiguration().load(this.getConfigurationPath());
    }
}
