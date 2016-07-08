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

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.GenericProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class OptionProperty extends GenericProperty {

    private final Method getter;
    private final Method setter;

    OptionProperty(String name, Field field) throws NoSuchMethodException {
        super(name, field.getType(), field.getGenericType());

        Class<?> clazz = field.getDeclaringClass();
        String suffix = capitalize(field.getName());
        this.getter = clazz.getMethod((field.getType() == boolean.class ? "is" : "get") + suffix);
        this.setter = clazz.getMethod("set" + suffix, field.getType());
    }

    @Override
    public void set(Object object, Object value) throws Exception {
        setter.invoke(object, value);
    }

    @Override
    public Object get(Object object) {
        try {
            return getter.invoke(object);
        } catch (Exception e) {
            throw new YAMLException("Unable to access option accessor " + getName() + " on object " + object, e);
        }
    }

    private static String capitalize(String s) {
        char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

}
