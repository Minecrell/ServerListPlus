/*
 * ServerListPlus
 * Copyright (C) 2017 Minecrell <https://github.com/Minecrell>
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

import net.minecrell.serverlistplus.config.loader.ConfigurationLoader;
import net.minecrell.serverlistplus.config.processor.ConfigurationProcessor;
import net.minecrell.serverlistplus.module.Component;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public final class ConfigurationManager implements Component {

    private final Logger logger;

    private final ConfigurationLoader loader;
    private final Map<String, ConfigurationProcessor> processors = new HashMap<>();

    public ConfigurationManager(Logger logger, ConfigurationLoader loader) {
        this.logger = logger;
        this.loader = loader;
    }

    @Nullable
    public ConfigurationProcessor getProcessor(String id) {
        return this.processors.get(id);
    }

    public Collection<ConfigurationProcessor> getProcessors() {
        return Collections.unmodifiableCollection(processors.values());
    }

    public void registerProcessor(ConfigurationProcessor processor) {
        this.processors.put(processor.getId(), processor);
    }

    public boolean reload() {
        boolean success = true;

        for (ConfigurationProcessor processor : this.processors.values()) {
            try {
                reload(processor);
            } catch (ConfigurationException e) {
                success = false;
                this.logger.error(e.getMessage(), e.getCause());
            }
        }

        return success;
    }

    public boolean reload(String id) {
        ConfigurationProcessor processor = getProcessor(id);
        if (processor == null) {
            return false;
        }

        try {
            reload(processor);
            return true;
        } catch (ConfigurationException e) {
            this.logger.error(e.getMessage(), e.getCause());
            return false;
        }
    }

    private void reload(ConfigurationProcessor processor) throws ConfigurationException {
        this.logger.debug("Loading configuration '{}'...", processor.getId());
        processor.reload(this.loader);
        this.logger.info("Successfully loaded '{}' configuration", processor.getId());
    }

    @Override
    public void initialize() {
        reload(); // Reload configuration
    }

}
