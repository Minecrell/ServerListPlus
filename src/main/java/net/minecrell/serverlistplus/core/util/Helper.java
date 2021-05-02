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

package net.minecrell.serverlistplus.core.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class Helper {
    private Helper() {}

    public final static Gson JSON = new GsonBuilder()
            .registerTypeAdapter(Date.class, new ISO8601Serializer())
            .enableComplexMapKeySerialization()
            .create();

    private final static Joiner NEW_LINE_JOINER = Joiner.on('\n');

    public static String joinLines(String... lines) {
        return NEW_LINE_JOINER.join(lines);
    }

    private static final Splitter NEW_LINE_SPLITTER = Splitter.on('\n');

    public static Iterable<String> splitLines(String s) {
        return NEW_LINE_SPLITTER.split(s);
    }

    public static List<String> splitLinesToList(String s) {
        return NEW_LINE_SPLITTER.splitToList(s);
    }

    public static boolean isNullOrEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNullOrEmpty(Iterator<?> iterator) {
        return iterator == null || !iterator.hasNext();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <T> ImmutableList<T> makeImmutableList(Collection<T> elements) {
        if (isNullOrEmpty(elements)) return null;
        return ImmutableList.copyOf(elements);
    }

    public static String[] toStringArray(Collection<String> c) {
        return c != null ? c.toArray(new String[c.size()]) : null;
    }

    public static <T> T getLastElement(List<T> list) {
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public static String causedException(Throwable e) {
        Throwable cause = Throwables.getRootCause(e);
        return cause.getClass().getName() + ": " + cause.getMessage();
    }

    // String Utils
    public static boolean startsWithIgnoreCase(String s, String start) {
        return s.regionMatches(true, 0, start, 0, start.length());
    }

    public static String toLowerCase(String s) {
        return s.toLowerCase(Locale.ENGLISH);
    }

    public static String substringBefore(String s, char c) {
        int pos = s.indexOf(c);
        return pos >= 0 ? s.substring(0, pos) : s;
    }

}
