/*
 *       __                           __ _     _     ___ _
 *      / _\ ___ _ ____   _____ _ __ / /(_)___| |_  / _ \ |_   _ ___
 *      \ \ / _ \ '__\ \ / / _ \ '__/ / | / __| __|/ /_)/ | | | / __|
 *      _\ \  __/ |   \ V /  __/ | / /__| \__ \ |_/ ___/| | |_| \__ \
 *      \__/\___|_|    \_/ \___|_| \____/_|___/\__\/    |_|\__,_|___/
 *                       Customize your server ping!
 *
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.util;

import java.util.Collection;
import java.util.List;

import net.minecrell.serverlistplus.api.plugin.ServerListPlusPlugin;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class Helper {
    private Helper() {}

    public static String[] toStringArray(Collection<String> c) {
        if (c == null) return null;
        return c.toArray(new String[c.size()]);
    }

    public static <T> T[] nullWhenEmpty(T[] array) {
        if (array == null || array.length == 0) return null;
        return array;
    }

    public static List<String> colorize(final ServerListPlusPlugin plugin, List<String> lines) {
        return Lists.transform(lines, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return plugin.colorizeString(input);
            }
        });
    }
}
