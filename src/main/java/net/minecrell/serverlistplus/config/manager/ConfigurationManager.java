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
import net.minecrell.serverlistplus.config.MappedElement;
import net.minecrell.serverlistplus.manager.Manager;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public abstract class ConfigurationManager extends Manager {

    protected final Path configDir;

    protected ConfigurationManager(ServerListPlus core, Path configDir) {
        super(core);
        this.configDir = Objects.requireNonNull(configDir, "configDir");
    }

    public final Path getConfigDir() {
        return this.configDir;
    }


    public abstract <T> void registerConfig(String key, Class<T> type, T def);

    public abstract <T> void registerConfigList(String key, Class<T> type, List<T> def);

    public abstract <K, V> void registerConfigMap(String key, Class<K> keyType, Class<V> valueType, Map<K, V> def);

    public abstract void registerMappedElement(Class<? extends MappedElement> type, String key, Class<? extends MappedElement> element);

    @Nullable
    public abstract <T> T getConfig(String key);

    public abstract void reload();

    public abstract boolean reload(String key);

    public abstract void save();

    public abstract boolean save(String key) ;

}
