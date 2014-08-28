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

package net.minecrell.serverlistplus.core.replacement.util;

import net.minecrell.serverlistplus.core.util.Helper;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Iterators;

public final class Patterns {
    private Patterns() {}

    public static boolean find(Pattern pattern, String s) {
        return pattern.matcher(s).find();
    }

    public static String replace(String s, Pattern pattern, Object replacement) {
        if (replacement == null) return s;
        return pattern.matcher(s).replaceAll(replacement.toString());
    }

    public static String replace(String s, Pattern pattern, Object... replacements) {
        return Helper.isNullOrEmpty(replacements) ? s : replace(s, pattern, Iterators.forArray(replacements));
    }

    public static String replace(String s, Pattern pattern, Iterator<?> replacements) {
        return replace(s, pattern, replacements, null);
    }

    public static String replace(Matcher matcher, String s, Iterator<?> replacements) {
        return replace(matcher, s, replacements, null);
    }

    public static String replace(String s, Pattern pattern, Iterator<?> replacements, Object others) {
        if (Helper.isNullOrEmpty(replacements)) return replace(s, pattern, others);
        Matcher matcher = pattern.matcher(s);
        return replace(matcher, s, replacements, others);
    }

    public static String replace(Matcher matcher, String s, Iterator<?> replacements, Object others) {
        if (Helper.isNullOrEmpty(replacements)) return replace(s, matcher.pattern(), others);
        if (!matcher.find()) return s;

        StringBuffer result = new StringBuffer();
        String fallback = null;
        do {
            matcher.appendReplacement(result, fallback != null ? fallback : replacements.next().toString());
            if (!replacements.hasNext())
                if (others != null) fallback = others.toString();
                else break;
        } while (matcher.find());

        matcher.appendTail(result);
        return result.toString();
    }
}
