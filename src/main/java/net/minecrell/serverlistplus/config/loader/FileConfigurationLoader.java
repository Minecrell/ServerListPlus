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

    protected Path getConfigPath(String key) {
        return configDir.resolve(key + fileExtension);
    }

    @Override
    public abstract <T> T loadConfig(String name, Class<T> type) throws ConfigurationFileException;

    @Override
    public abstract <T> List<T> loadListConfig(String name, Class<T> type) throws ConfigurationFileException;

    @Override
    public abstract <K, V> Map<K, V> loadMapConfig(String name, Class<K> keyType, Class<V> valueType) throws ConfigurationFileException;

}
