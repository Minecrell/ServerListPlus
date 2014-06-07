/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your complete server status ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MutableClassToInstanceMap;

public class ClassToInstanceStorage<T> implements InstanceStorage<T> {
    protected final ClassToInstanceMap<T> instances;

    protected ClassToInstanceStorage(ClassToInstanceMap<T> instances) {
        this.instances = instances;
    }

    @Override
    public ImmutableCollection<T> get() {
        return ImmutableList.copyOf(instances.values());
    }

    @Override
    public Map<Class<? extends T>, T> getMap() {
        return instances;
    }

    @Override
    public <V extends T> V get(Class<V> clazz) {
        return instances.getInstance(clazz);
    }

    @Override
    public boolean has(Class<? extends T> clazz) {
        return instances.containsKey(clazz);
    }

    @Override
    public <V extends T> void set(Class<V> clazz, V instance) {
        instances.putInstance(clazz, instance);
    }

    @Override
    public void setUnsafe(Class<? extends T> clazz, T instance) {
        instances.put(clazz, instance);
    }

    @Override
    public boolean remove(Class<? extends T> clazz) {
        return instances.remove(clazz) != null;
    }

    @Override
    public int count() {
        return instances.size();
    }

    public static <T> ClassToInstanceStorage<T> create(ClassToInstanceMap<T> instances) {
        return new ClassToInstanceStorage<>(instances);
    }

    public static <T> ClassToInstanceStorage<T> create() {
        return create(MutableClassToInstanceMap.<T>create());
    }

    public static <T> ClassToInstanceStorage<T> createLinked() {
        return create(MutableClassToInstanceMap.create(new LinkedHashMap<Class<? extends T>, T>()));
    }
}
