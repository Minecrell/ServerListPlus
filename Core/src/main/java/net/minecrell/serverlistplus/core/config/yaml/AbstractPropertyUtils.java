/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
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

package net.minecrell.serverlistplus.core.config.yaml;

import net.minecrell.serverlistplus.core.util.Helper;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.FieldProperty;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public abstract class AbstractPropertyUtils extends PropertyUtils {
    protected AbstractPropertyUtils() {
        setBeanAccess(BeanAccess.FIELD);
    }

    @Override // Order properties in the configuration as defined in the source code
    protected Set<Property> createPropertySet(Class<?> type, BeanAccess bAccess) throws IntrospectionException {
        return new LinkedHashSet<>(getPropertiesMap(type, bAccess).values());
    }

    private final Map<Class<?>, Map<String, Property>> propertiesCache = new HashMap<>();

    @Override
    protected Map<String, Property> getPropertiesMap(Class<?> type, BeanAccess bAccess) throws IntrospectionException {
        if (bAccess == BeanAccess.FIELD) {
            Map<String, Property> properties = new LinkedHashMap<>();
            findProperties(type, properties);
            propertiesCache.put(type, properties);
            return properties;
        } else return super.getPropertiesMap(type, bAccess);
    }

    private static void findProperties(Class<?> clazz, Map<String, Property> properties) {
        Class<?> superClass = clazz.getSuperclass(); // Process super classes first
        if (superClass != null) findProperties(superClass, properties);

        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)
                    && !properties.containsKey(field.getName())) {
                properties.put(Helper.toLowerCase(field.getName()), new FieldProperty(field));
            }
        }
    }
}
