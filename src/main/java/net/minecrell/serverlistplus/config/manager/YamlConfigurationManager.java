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

package net.minecrell.serverlistplus.config.manager;

import net.minecrell.serverlistplus.ServerListPlus;
import net.minecrell.serverlistplus.config.Description;
import net.minecrell.serverlistplus.config.MappedElement;
import net.minecrell.serverlistplus.config.yaml.ServerListPlusYaml;
import net.minecrell.serverlistplus.config.yaml.YamlConfigReader;
import net.minecrell.serverlistplus.config.yaml.YamlConfigWriter;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class YamlConfigurationManager extends ConfigurationManager {

    private static final String FILE_EXTENSION = ".yml";
    private static final String BACKUP_EXTENSION = ".old";

    protected final ServerListPlusYaml yaml;

    private final Map<String, ConfigRegistration<?>> registry = new LinkedHashMap<>();

    public YamlConfigurationManager(ServerListPlus core, Path configDir) {
        super(core, configDir);
        this.yaml = new ServerListPlusYaml(core.getLogger());
    }

    @Override
    public <T> void registerConfig(String key, Class<T> type, T def) {
        ConfigRegistration<?> registration = this.registry.get(key);
        if (registration != null) {
            registration.updateObject(type);
        } else {
            this.registry.put(key, new ConfigRegistration.Object<>(type, def));
        }
    }

    @Override
    public <T> void registerConfigList(String key, Class<T> type, List<T> def) {
        ConfigRegistration<?> registration = this.registry.get(key);
        if (registration != null) {
            registration.updateList(type);
        } else {
            this.registry.put(key, new ConfigRegistration.List<>(type, def));
        }
    }

    @Override
    public <K, V> void registerConfigMap(String key, Class<K> keyType, Class<V> valueType, Map<K, V> def) {
        ConfigRegistration<?> registration = this.registry.get(key);
        if (registration != null) {
            registration.updateMap(keyType, valueType);
        } else {
            this.registry.put(key, new ConfigRegistration.Map<>(keyType, valueType, def));
        }
    }

    @Override
    public void registerMappedElement(Class<? extends MappedElement> type, String key, Class<? extends MappedElement> element) {
        this.yaml.registerMappedElement(type, key, element);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getConfig(String key) {
        ConfigRegistration<?> registration = this.registry.get(key);
        return registration != null ? (T) registration.get() : null;
    }

    protected Path getConfigPath(String key) {
        return getConfigDir().resolve(key + FILE_EXTENSION);
    }

    @Override
    public synchronized void reload() {
        this.registry.forEach(this::reloadConfig);
    }

    @Override
    public synchronized boolean reload(String key) {
        return reloadConfig(key, this.registry.get(key));
    }

    protected boolean reloadConfig(String key, @Nullable ConfigRegistration<?> registration) {
        if (registration == null) {
            return false;
        }

        Path path = getConfigPath(key);

        getLogger().info("Reloading configuration '{}' from {}", key, path);

        if (Files.notExists(path)) {
            return saveConfig(key, registration);
        }

        try (YamlConfigReader reader = this.yaml.createReader(Files.newBufferedReader(path))) {
            registration.load(reader);
            return true;
        } catch (YAMLException e) {
            getLogger().error("Failed to parse configuration '{}' from {}. Make sure the YAML syntax is valid!", key, path, e);
        } catch (CharacterCodingException e) {
            getLogger().error("Your configuration '{}' from {} contains invalid special characters. Please save your configuration using UTF-8 "
                    + "instead.", key, path, e);
        } catch (IOException e) {
            getLogger().error("Failed to load configuration '{}' from {}", key, path, e);
        } catch (Exception e) {
            getLogger().error("An internal error occurred while reloading configuration '{}' from {}", key, path, e);
        }

        return false;
    }

    @Override
    public synchronized void save() {
        this.registry.forEach(this::saveConfig);
    }

    @Override
    public synchronized boolean save(String key) {
        return saveConfig(key, this.registry.get(key));
    }

    protected boolean saveConfig(String key, @Nullable ConfigRegistration<?> registration) {
        if (registration == null) {
            return false;
        }

        Path path = getConfigPath(key);

        getLogger().info("Saving configuration '{}' to {}", key, path);

        try {
            if (Files.exists(path)) {
                // Create a backup if the configuration already exists!
                Path backupPath = getConfigDir().resolve(key + BACKUP_EXTENSION + FILE_EXTENSION);
                getLogger().debug("Saving configuration backup for '{}' to {}", key, backupPath);

                Files.copy(path, backupPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // Create plugin folder if it doesn't exist already
                Files.createDirectories(path.toAbsolutePath().getParent());
            }

            try (YamlConfigWriter writer = this.yaml.createWriter(Files.newBufferedWriter(path))) {
                writer.writeComment(key.substring(0, 1).toUpperCase(Locale.ENGLISH) + key.substring(1) + " configuration file");
                writer.writeComment(core.getDisplayName());
                writer.writeNewLine();

                if (registration.description != null) {
                    for (String line : registration.description) {
                        writer.writeComment(line);
                    }

                    writer.writeNewLine();
                }

                writer.writeComment("Please refer to the wiki for instructions how to use this configuration.");
                writer.writeComment("https://git.io/slp-wiki, https://git.io/slp-wiki-" + key);

                writer.writeNewLine();

                writer.writeConfig(registration.get());
                return true;
            }
        } catch (YAMLException e) {
            getLogger().error("Failed to generate YAML syntax for configuration '{}'", key, e);
        } catch (IOException e) {
            getLogger().error("Failed to write configuration '{}' to {}", key, path, e);
        } catch (Exception e) {
            getLogger().error("An internal error occurred while saving configuration '{}' to {}", key, path, e);
        }

        return false;
    }

    protected static abstract class ConfigRegistration<T> {

        protected T def;
        @Nullable protected T loaded;

        @Nullable private final String[] description;

        protected ConfigRegistration(@Nullable Class<?> valueType, T def) {
            this.def = Objects.requireNonNull(def, "def");

            if (valueType != null) {
                Description description = valueType.getAnnotation(Description.class);
                this.description = description != null ? description.value() : null;
            } else {
                this.description = null;
            }
        }

        protected T get() {
            return this.loaded != null ? this.loaded : this.def;
        }

        protected final void load(YamlConfigReader reader) throws IOException {
            this.loaded = loadConfig(reader);
        }

        protected abstract T loadConfig(YamlConfigReader reader);

        protected void updateObject(Class<?> type) {
            throw new IllegalArgumentException(getClass() + " does not accept " + type + " values");
        }

        protected void updateList(Class<?> type) {
            throw new IllegalArgumentException(getClass() + " does not accept " + type + " values");
        }

        protected void updateMap(Class<?> keyType, Class<?> valueType) {
            throw new IllegalArgumentException(getClass() + " does not accept " + keyType + "-> " + valueType + " values");
        }

        @SuppressWarnings("unchecked")
        protected static <T> Class<T> checkCompatible(Class<? extends T> currentType, Class<?> type) {
            Objects.requireNonNull(type, "type");
            if (!currentType.isAssignableFrom(type)) {
                throw new IllegalArgumentException(type + " is incompatible with " + currentType);
            }
            return (Class<T>) type;
        }

        protected static class Object<T> extends ConfigRegistration<T> {

            private Class<? extends T> type;

            protected Object(Class<T> type, T def) {
                super(type, def);
                this.type = Objects.requireNonNull(type, "type");
            }

            @Override
            protected T loadConfig(YamlConfigReader reader) {
                return reader.load(this.type);
            }

            @Override
            protected void updateObject(Class<?> type) {
                this.type = checkCompatible(this.type, type);
            }

        }

        protected static class List<T> extends ConfigRegistration<java.util.List<? extends T>> {

            private Class<? extends T> type;

            public List(Class<T> type, java.util.List<T> def) {
                super(type, def);
                this.type = Objects.requireNonNull(type, "type");
            }

            @Override
            protected java.util.List<? extends T> loadConfig(YamlConfigReader reader) {
                return reader.loadList(this.type);
            }

            @Override
            protected void updateList(Class<?> type) {
                this.type = checkCompatible(this.type, type);
            }

        }

        protected static class Map<K, V> extends ConfigRegistration<java.util.Map<? extends K, ? extends V>> {

            private Class<? extends K> keyType;
            private Class<? extends V> valueType;

            public Map(Class<K> keyType, Class<V> valueType, java.util.Map<K, V> def) {
                super(valueType, def);
                this.keyType = Objects.requireNonNull(keyType, "keyType");
                this.valueType = Objects.requireNonNull(valueType, "valueType");
            }

            @Override
            protected java.util.Map<? extends K, ? extends V> loadConfig(YamlConfigReader reader) {
                return reader.loadMap(this.keyType, this.valueType);
            }

            @Override
            protected void updateMap(Class<?> keyType, Class<?> valueType) {
                this.keyType = checkCompatible(this.keyType, keyType);
                this.valueType = checkCompatible(this.valueType, valueType);
            }

        }

    }

}
