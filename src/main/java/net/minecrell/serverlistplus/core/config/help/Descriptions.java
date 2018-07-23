/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
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

package net.minecrell.serverlistplus.core.config.help;

public final class Descriptions {
    private Descriptions() {}

    /**
     * Gets the {@link Description} of a specific object.
     * @param obj The object to get the {@link Description} from.
     * @return The {@link Description} lines if the annotation is available for the specified object,
     * or <code>null</code> if the annotation is not available.
     */
    public static String[] of(Object obj) {
        return of(obj.getClass());
    }

    /**
     * Gets the {@link Description} of a specific class.
     * @param clazz The class to get the {@link Description} from.
     * @return The {@link Description} lines if the annotation is available for the specified class,
     * or <code>null</code> if the annotation is not available.
     */
    public static String[] of(Class<?> clazz) {
        // Get description from class annotation
        Description description = clazz.getAnnotation(Description.class);
        return description != null ? description.value() : null;
    }
}
