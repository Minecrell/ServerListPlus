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

package net.minecrell.serverlistplus;

import net.minecrell.serverlistplus.config.PluginConfig;
import net.minecrell.serverlistplus.config.StatusProfileConfig;
import net.minecrell.serverlistplus.config.manager.ConfigurationManager;
import net.minecrell.serverlistplus.config.manager.YamlConfigurationManager;
import net.minecrell.serverlistplus.impl.ImplementationType;
import net.minecrell.serverlistplus.impl.ServerListPlusImpl;
import net.minecrell.serverlistplus.logger.Logger;

import java.nio.file.Path;
import java.util.Objects;

public final class ServerListPlus {

    private final ImplementationType implType;
    private final ServerListPlusImpl impl;
    private final Logger logger;

    private final ConfigurationManager configManager;

    public ServerListPlus(ImplementationType implType, ServerListPlusImpl impl, Logger logger, Path configDir) {
        this.implType = Objects.requireNonNull(implType, "implType");
        this.impl = Objects.requireNonNull(impl, "impl");
        this.logger = Objects.requireNonNull(logger, "logger");

        this.configManager = new YamlConfigurationManager(this, configDir); // TODO: Allow customization

        this.configManager.registerConfigMap("status", String.class, StatusProfileConfig.class, StatusProfileConfig.getDefaults());
        this.configManager.registerConfig("plugin", PluginConfig.class, new PluginConfig());
    }

    public ImplementationType getImplementationType() {
        return this.implType;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void initialize() {
        this.configManager.reload();
    }

}
