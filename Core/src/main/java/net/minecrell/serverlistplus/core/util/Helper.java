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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public final class Helper {
    private Helper() {}

    private final static Joiner NEWLINE_JOINER = Joiner.on('\n');

    public static String lines(String... lines) {
        return NEWLINE_JOINER.join(Iterators.forArray(lines));
    }

    public static boolean nullOrEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nullOrEmpty(Iterable<?> iterable) {
        return iterable == null || Iterables.isEmpty(iterable);
    }

    public static <T> ImmutableList<T> makeImmutableList(Collection<T> elements) {
        if (nullOrEmpty(elements)) return null;
        return ImmutableList.copyOf(elements);
    }

    public static <T> ImmutableSet<T> makeImmutableSet(Collection<T> elements) {
        if (nullOrEmpty(elements)) return null;
        return ImmutableSet.copyOf(elements);
    }

    public static <K, V, T extends Map<K, V>> int mergeMaps(T main, Map<K, V> merge) {
        int counter = 0;
        for (Map.Entry<K, V> entry : merge.entrySet())
            // Check if map contains the key...
            if (!main.containsKey(entry.getKey())) {
                // And if not, create it!
                main.put(entry.getKey(), entry.getValue()); counter++;
            }
        return counter;
    }

    public static String[] toStringArray(Collection<String> c) {
        return c != null ? c.toArray(new String[c.size()]) : null;
    }

    public static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    public static <T> T nextEntry(T[] array) {
        if (Helper.nullOrEmpty(array)) return null;
        return array.length > 1 ? array[random().nextInt(array.length)] : array[0];
    }

    public static <T> T nextEntry(List<T> list) {
        if (Helper.nullOrEmpty(list)) return null;
        return list.size() > 1 ? list.get(random().nextInt(list.size())) : list.get(0);
    }

    public static Integer nextNumber(IntRange range) {
        if (range == null) return null;
        return range.isSingle() ? range.from() : random().nextInt(range.from(), range.to());
    }

    public static String causedError(Throwable e) {
        Throwable cause = Throwables.getRootCause(e);
        return cause.getClass().getName() + ": " + cause.getMessage();
    }

    public static String replace(String replace, String s, Object replacement) {
        if (replacement == null) return s;
        final int stringLength = s.length();
        final StringBuilder result = new StringBuilder(stringLength);

        int i = s.indexOf(replace);
        if (i == -1) return s;

        int pos = 0;
        final String replacementString = replacement.toString();
        final int replaceLength = replace.length();
        do {
            result.append(s, pos, i);
            pos = i + replaceLength;
            result.append(replacementString);

            if (pos == stringLength) break;
            i = s.indexOf(replace, pos);
        } while (i != -1);

        if (pos < stringLength)
            result.append(s, pos, stringLength);

        return result.toString();
    }

    public static String replace(String replace, String s, Object[] replacements) {
        if (nullOrEmpty(replacements)) return s;
        final int stringLength = s.length();
        final StringBuilder result = new StringBuilder(stringLength);

        int i = s.indexOf(replace);
        if (i == -1) return s;

        int pos = 0;
        final int replaceLength = replace.length();
        for (Object replacement : replacements) {
            result.append(s, pos, i);
            pos = i + replaceLength;
            result.append(replacement);

            if (pos == stringLength) break;
            i = s.indexOf(replace, pos);
            if (i == -1) break;
        }

        if (pos < stringLength)
            result.append(s, pos, stringLength);

        return result.toString();
    }

    public static boolean startsWithIgnoreCase(String s, String start) {
        return s.regionMatches(true, 0, start, 0, start.length());
    }
}
