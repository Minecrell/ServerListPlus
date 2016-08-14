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
import java.util.function.Function;

import javax.annotation.Nullable;

public final class ServerListPlus {

    private final ImplementationType implType;
    private final String version;

    private final ServerListPlusImpl impl;
    private final Logger logger;

    private final ConfigurationManager configManager;

    private ServerListPlus(Builder builder) {
        this.implType = builder.implType;
        this.impl = builder.impl;
        this.logger = builder.logger;

        if (builder.configManager != null) {
            this.configManager = builder.configManager.apply(this);
        } else {
            this.configManager = new YamlConfigurationManager(this, builder.configDir);
        }

        if (builder.version != null) {
            this.version = builder.version;
        } else {
            String version = getClass().getPackage().getImplementationVersion();
            this.version = version != null ? version : "Unknown";
        }

        this.configManager.registerConfigMap("status", String.class, StatusProfileConfig.class, StatusProfileConfig.getDefaults());
        this.configManager.registerConfig("plugin", PluginConfig.class, new PluginConfig());
    }

    public ImplementationType getImplementationType() {
        return this.implType;
    }

    public String getName() {
        return "ServerListPlus (" + getImplementationType().getName() + ')';
    }

    public String getVersion() {
        return version;
    }

    public String getDisplayName() {
        return getName() + ' ' + version + " - https://git.io/slp";
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void initialize() {
        logger.info(getDisplayName());

        this.configManager.reload();
    }

    public static final class Builder {

        private final ImplementationType implType;
        private final ServerListPlusImpl impl;
        private final Logger logger;
        private final Path configDir;

        @Nullable private String version;

        @Nullable private Function<ServerListPlus, ConfigurationManager> configManager;

        public Builder(ImplementationType implType, ServerListPlusImpl impl, Logger logger, Path configDir) {
            this.implType = Objects.requireNonNull(implType, "implType");
            this.impl = Objects.requireNonNull(impl, "impl");
            this.logger = Objects.requireNonNull(logger, "logger");
            this.configDir = Objects.requireNonNull(configDir, "configDir");
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder configManager(Function<ServerListPlus, ConfigurationManager> configManager) {
            this.configManager = configManager;
            return this;
        }

        public ServerListPlus build() {
            return new ServerListPlus(this);
        }

    }

}
