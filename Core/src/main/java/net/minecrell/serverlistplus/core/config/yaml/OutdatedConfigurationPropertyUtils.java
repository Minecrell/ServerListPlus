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

package net.minecrell.serverlistplus.core.config.yaml;

import net.minecrell.serverlistplus.core.ServerListPlusCore;

import java.beans.IntrospectionException;
import java.util.Map;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;

public class OutdatedConfigurationPropertyUtils extends FieldOrderPropertyUtils {
    private final ServerListPlusCore core;

    public OutdatedConfigurationPropertyUtils(ServerListPlusCore core) {
        this.core = core;
    }

    @Override
    public Property getProperty(Class<?> type, String name, BeanAccess bAccess) throws IntrospectionException {
        Map<String, Property> properties = getPropertiesMap(type, bAccess);
        Property property = properties.get(name);
        if (property == null)
            return new OutdatedMissingProperty(name);

        if (!property.isWritable())
            throw new YAMLException("Unable to find writable property '" + name + "' on class: " + type.getName());

        return property;
    }

    public static class OutdatedMissingProperty extends Property {
        public OutdatedMissingProperty(String name) {
            super(name, Object.class);
        }

        @Override
        public Class<?>[] getActualTypeArguments() {
            return new Class[0];
        }

        @Override
        public void set(Object object, Object value) throws Exception {}

        @Override
        public Object get(Object object) {
            return object;
        }
    }
}
