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

package net.minecrell.serverlistplus.config.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public abstract class FileConfigurationLoader implements ConfigurationLoader {

    private final Path configDir;
    private final String fileExtension;

    protected FileConfigurationLoader(Path configDir, String fileExtension) {
        this.configDir = configDir;
        this.fileExtension = fileExtension;
    }

    public final Path getConfigDir() {
        return this.configDir;
    }

    protected String getConfigFileName(String name) {
        return name + fileExtension;
    }

    protected Path getConfigPath(String name) {
        return configDir.resolve(getConfigFileName(name));
    }

    protected Path prepareConfig(String name, Class<?> context) throws ConfigurationFileException {
        Path path = getConfigPath(name);

        if (Files.notExists(path)) {
            copyDefault(name, path, context);
        }

        return path;
    }

    private void copyDefault(String name, Path path, Class<?> context) throws ConfigurationFileException {
        URL resource = context.getResource(getConfigFileName(name));
        if (resource == null) {
            return;
        }

        try {
            Files.createDirectories(configDir);
            try (InputStream in = resource.openStream()) {
                Files.copy(in, path);
            }
        } catch (IOException e) {
            throw new ConfigurationFileException("Failed to copy default configuration '" + name + "' to " + path, e, name, path);
        }
    }

    @Override
    public abstract <T> T loadConfig(String name, Class<T> type) throws ConfigurationFileException;

    @Override
    public abstract <T> List<T> loadListConfig(String name, Class<T> type) throws ConfigurationFileException;

    @Override
    public abstract <K, V> Map<K, V> loadMapConfig(String name, Class<K> keyType, Class<V> valueType) throws ConfigurationFileException;

}
