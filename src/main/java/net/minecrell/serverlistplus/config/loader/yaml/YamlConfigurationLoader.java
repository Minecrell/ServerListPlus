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

package net.minecrell.serverlistplus.config.loader.yaml;

import net.minecrell.serverlistplus.config.loader.ConfigurationFileException;
import net.minecrell.serverlistplus.config.loader.FileConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class YamlConfigurationLoader extends FileConfigurationLoader {

    private static final String FILE_EXTENSION = ".yml";

    private final Yaml yaml;
    private final ConfigurationConstructor constructor;

    public YamlConfigurationLoader(Path configDir) {
        super(configDir, FILE_EXTENSION);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setWidth(Integer.MAX_VALUE);

        this.constructor = new ConfigurationConstructor();
        this.yaml = new Yaml(this.constructor, new Representer(), options);
    }

    @Override
    public <T> void registerType(Class<T> baseType, String typeId, Class<? extends T> type) {
        this.constructor.registerType(baseType, new Tag('!' + typeId), type);
    }

    private <T> T load(String name, Class<T> type) throws ConfigurationFileException {
        Path path = getConfigPath(name);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return yaml.loadAs(reader, type);
        } catch (YAMLException e) {
            throw new ConfigurationFileException("Failed to parse configuration '" + name + "' from " + path + ". "
                    + "Please check your configuration for syntax errors.", e, name, path);
        } catch (CharacterCodingException e) {
            throw new ConfigurationFileException("Configuration '" + name + "' from " + path + " contains invalid special characters. "
                    + "Make sure you save it using UTF-8.", e, name, path);
        } catch (IOException e) {
            throw new ConfigurationFileException("Failed to load configuration '" + name + "' from " + path, e, name, path);
        }
    }

    @Override
    public <T> T loadConfig(String name, Class<T> type) throws ConfigurationFileException {
        this.constructor.reset();
        return load(name, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> loadListConfig(String name, Class<T> type) throws ConfigurationFileException {
        this.constructor.setListType(type);
        return load(name, List.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> loadMapConfig(String name, Class<K> keyType, Class<V> valueType) throws ConfigurationFileException {
        this.constructor.setMapType(keyType, valueType);
        return load(name, Map.class);
    }

}
