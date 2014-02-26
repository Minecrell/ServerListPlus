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
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecrell.serverlistplus.api.ServerPingResponse;
import net.minecrell.serverlistplus.api.configuration.Configuration;
import net.minecrell.serverlistplus.api.plugin.ServerListPlusPlugin;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MutableClassToInstanceMap;

public class Helper {
    private Helper() {}

    private final static Splitter NEWLINE_SPLITTER = Splitter.on('\n');
    private final static Joiner NEWLINE_JOINER = Joiner.on('\n');

    public static String[] splitLines(String lines) {
        return Iterables.toArray(NEWLINE_SPLITTER.split(lines), String.class);
    }

    public static String joinLines(Object... lines) {
        return NEWLINE_JOINER.join(lines);
    }

    public static String ordinalNumber(int i) {
        return i + getOrdinalPrefix(i);
    }

    private static String getOrdinalPrefix(int i) {
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return "th";
            default:
                switch (i % 10) {
                    case 1: return "st";
                    case 2: return "nd";
                    case 3: return "rd";
                    default: return "th";
                }
        }
    }

    public static Configuration[] toConfigArray(Collection<Configuration> c) {
        if (c == null) return null;
        return c.toArray(new Configuration[c.size()]);
    }

    public static String[] toStringArray(Collection<String> c) {
        if (c == null) return null;
        return c.toArray(new String[c.size()]);
    }

    public static boolean nullOrEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }

    public static boolean nullOrEmpty(Collection<?> c) {
        return (c == null || c.size() == 0);
    }

    public static <T> T[] nullWhenEmpty(T[] array) {
        return (!nullOrEmpty(array)) ? array : null;
    }

    public static <T> T nextEntry(Random random, T[] array) {
        return array[random.nextInt(array.length)];
    }

    public static String replaceFirstAndOthers(Pattern pattern, String s, String firstReplace, String replacement) {
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            matcher.appendReplacement(sb, firstReplace);
            while (matcher.find())
                matcher.appendReplacement(sb, replacement);
            matcher.appendTail(sb);
            return sb.toString();
        } else return s;
    }

    public static List<String> colorize(final ServerListPlusPlugin plugin, List<String> lines) {
        return Lists.transform(lines, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return plugin.colorizeString(input);
            }
        });
    }

    public static <T> ClassToInstanceMap<T> createLinkedClassMap() {
        return MutableClassToInstanceMap.create(new LinkedHashMap<Class<? extends T>, T>());
    }

    public static <T> ClassToInstanceMap<T> copyLinkedClassMap(ClassToInstanceMap<T> src) {
        return MutableClassToInstanceMap.create(new LinkedHashMap<>(src));
    }

    public static <K, V, T extends Map<K, V>> int mergeMaps(T main, Map<K, V> merge) {
        int counter = 0;
        for (Map.Entry<K, V> entry : merge.entrySet())
            if (!main.containsKey(entry.getKey())) {
                main.put(entry.getKey(), entry.getValue()); counter++;
            }
        return counter;
    }

    public static EnumSet<ServerPingResponse.Modify> getModifications(ServerPingResponse.Modify... modifications) {
        if (Helper.nullOrEmpty(modifications)) return EnumSet.allOf(ServerPingResponse.Modify.class);
        EnumSet<ServerPingResponse.Modify> set = EnumSet.noneOf(ServerPingResponse.Modify.class);
        for (ServerPingResponse.Modify modify : modifications) {
            set.add(modify); set.addAll(modify.getChildren());
            if (modify == ServerPingResponse.Modify.ALL) break;
        } return set;
    }
}
