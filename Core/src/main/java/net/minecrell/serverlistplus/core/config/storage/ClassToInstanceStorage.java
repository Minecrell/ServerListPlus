/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.core.config.storage;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

/**
 * An {@link InstanceStorage} backed by a {@link ClassToInstanceStorage}.
 * @param <B> The type all stored instances need to inherit.
 */
public class ClassToInstanceStorage<B> implements InstanceStorage<B> {
    protected final ClassToInstanceMap<B> instances;

    protected ClassToInstanceStorage(ClassToInstanceMap<B> instances) {
        this.instances = Preconditions.checkNotNull(instances, "instances");
    }

    @Override
    public Collection<B> get() {
        return instances.values();
    }

    @Override
    public <T extends B> T get(Class<T> type) {
        return instances.getInstance(type);
    }

    @Override
    public boolean has(Class<? extends B> type) {
        return instances.containsKey(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean has(B instance) {
        return this.has((Class<? extends B>) instance.getClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends B> T set(T instance) {
        return this.set((Class<T>) instance.getClass(), instance);
    }

    @Override
    public <T extends B> T set(Class<T> type, T instance) {
        instances.putInstance(type, instance); return instance;
    }

    @Override
    public B remove(Class<? extends B> type) {
        return instances.remove(type);
    }

    @Override
    public int size() {
        return instances.size();
    }

    @Override
    public Iterator<B> iterator() {
        return instances.values().iterator();
    }

    public static <T> ClassToInstanceStorage<T> create(ClassToInstanceMap<T> instances) {
        return new ClassToInstanceStorage<>(instances);
    }

    public static <B> ClassToInstanceStorage<B> create() {
        return create(MutableClassToInstanceMap.<B>create());
    }

    public static <B> ClassToInstanceStorage<B> createLinked() {
        return create(MutableClassToInstanceMap.create(new LinkedHashMap<Class<? extends B>, B>()));
    }
}
