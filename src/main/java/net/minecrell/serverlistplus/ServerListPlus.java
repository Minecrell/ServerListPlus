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

package net.minecrell.serverlistplus;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkState;

import net.minecrell.serverlistplus.config.ConfigurationManager;
import net.minecrell.serverlistplus.config.loader.ConfigurationLoader;
import net.minecrell.serverlistplus.config.processor.status.StatusProfileConfigurationProcessor;
import net.minecrell.serverlistplus.module.Component;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public final class ServerListPlus {

    public enum State {
        LOADED, INITIALIZED, ENABLED, DISABLED
    }

    @Nullable private static ServerListPlus instance;

    public static ServerListPlus getInstance() {
        checkState(instance != null, "ServerListPlus was not initialized yet");
        return instance;
    }

    private final Platform platform;
    private final Logger logger;
    private final String name;
    private final String version;

    private final List<Component> components = new ArrayList<>();
    private final Map<Class<? extends Component>, Component> componentLookup = new HashMap<>();
    private State state = State.LOADED;

    public ServerListPlus(Platform platform, Logger logger, ConfigurationLoader configurationLoader) {
        checkState(instance == null, "ServerListPlus was already initialized");
        this.platform = platform;
        this.logger = logger;

        Package p = getClass().getPackage();
        this.name = firstNonNull(p.getSpecificationTitle(), "ServerListPlus");
        this.version = firstNonNull(p.getSpecificationVersion(), "Unknown");

        ConfigurationManager configurationManager = new ConfigurationManager(logger, configurationLoader);
        registerComponent(configurationManager);

        configurationManager.registerProcessor(new StatusProfileConfigurationProcessor());

        instance = this;
    }

    public Platform getPlatform() {
        return platform;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDisplayName() {
        return name + " (" + platform + ')';
    }

    public String getDisplayVersion() {
        return getDisplayName() + " v" + version;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> moduleClass) {
        return (T) componentLookup.get(moduleClass);
    }

    @SuppressWarnings("unchecked")
    public void registerComponent(Component component) {
        registerComponent((Class<Component>) component.getClass(), component);
    }

    public <T extends Component> void registerComponent(Class<T> moduleClass, T module) {
        if (componentLookup.containsKey(moduleClass)) {
            throw new IllegalArgumentException(moduleClass + " is already registered");
        }

        if (components.contains(module)) {
            throw new IllegalArgumentException(module + " is already registered");
        }

        componentLookup.put(moduleClass, module);
        components.add(module);
    }

    public State getState() {
        return state;
    }

    public void initialize() {
        checkState(state == State.LOADED, "Already initialized");

        logger.info("Initializing {}", getDisplayVersion());

        for (Component component : components) {
            component.initialize();
        }

        this.state = State.INITIALIZED;
    }

    public void enable() {
        checkState(state == State.INITIALIZED, "Not initialized");

        for (Component component : components) {
            component.enable();
        }

        this.state = State.ENABLED;
    }

    public void disable() {
        for (Component component : components) {
            component.disable();
        }

        this.state = State.DISABLED;
    }

}
