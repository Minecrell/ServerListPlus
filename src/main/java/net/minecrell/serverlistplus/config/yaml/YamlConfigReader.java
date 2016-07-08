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

package net.minecrell.serverlistplus.config.yaml;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class YamlConfigReader implements Closeable {

    private final ServerListPlusYaml yaml;
    private final BufferedReader reader;

    YamlConfigReader(ServerListPlusYaml yaml, BufferedReader reader) {
        this.yaml = yaml;
        this.reader = reader;
    }

    public <T> T load(Class<T> type) {
        return this.yaml.yaml.loadAs(this.reader, type);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> loadList(Class<T> type) {
        this.yaml.constructor.setListType(type);
        return load(List.class);
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> loadMap(Class<K> keyType, Class<V> valueType) {
        this.yaml.constructor.setMapType(keyType, valueType);
        return load(Map.class);
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

}
