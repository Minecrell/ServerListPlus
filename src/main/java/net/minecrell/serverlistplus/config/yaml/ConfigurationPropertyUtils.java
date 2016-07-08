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

import net.minecrell.serverlistplus.config.Option;
import net.minecrell.serverlistplus.logger.Logger;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.MissingProperty;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class ConfigurationPropertyUtils extends PropertyUtils {

    final Map<Class<?>, Map<String, Property>> propertiesCache = new HashMap<>();

    private final Logger logger;

    ConfigurationPropertyUtils(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @Override
    protected Set<Property> createPropertySet(Class<?> type, BeanAccess bAccess) throws IntrospectionException {
        // Order properties in the configuration as defined in the source code
        return new LinkedHashSet<>(getPropertiesMap(type, bAccess).values());
    }

    @Override
    protected Map<String, Property> getPropertiesMap(Class<?> type, BeanAccess bAccess) throws IntrospectionException {
        Map<String, Property> properties = propertiesCache.get(type);
        if (properties != null) {
            return properties;
        }

        properties = new LinkedHashMap<>();
        addProperties(type, properties);
        propertiesCache.put(type, properties);
        return properties;
    }

    @Override
    public Property getProperty(Class<?> type, String name, BeanAccess bAccess) throws IntrospectionException {
        Property property = super.getProperty(type, name.toLowerCase(Locale.ENGLISH), bAccess);
        if (property instanceof MissingProperty) {
            logger.warn("Unknown configuration property: {} in {}", name, type.getSimpleName());
        }
        return property;
    }

    void addProperties(Class<?> type, Map<String, Property> properties) throws IntrospectionException {
        Class<?> superClass = type.getSuperclass(); // Process super classes first
        if (superClass != null) {
            addProperties(superClass, properties);
        }

        for (Field field : type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }

            Option option = field.getAnnotation(Option.class);
            if (option == null) {
                continue;
            }

            String lowerName = option.name().toLowerCase(Locale.ENGLISH);
            if (properties.containsKey(lowerName)) {
                continue;
            }

            try {
                properties.put(lowerName, new OptionProperty(option.name(), field));
            } catch (NoSuchMethodException e) {
                throw new IntrospectionException("Failed to add option " + option.name() + " to " + type + ": " + e);
            }
        }
    }

}
