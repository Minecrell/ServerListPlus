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

package net.minecrell.serverlistplus.core.replacement.util;

import com.google.common.collect.Iterators;
import net.minecrell.serverlistplus.core.util.Helper;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Patterns {
    private Patterns() {}

    public static boolean find(Pattern pattern, String s) {
        return pattern.matcher(s).find();
    }

    public static String replace(String s, Pattern pattern, Object replacement) {
        if (replacement == null) return s;
        return new LiteralMatcherReplacer(s, pattern.matcher(s)).replaceAll(replacement);
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

        LiteralMatcherReplacer replacer = new LiteralMatcherReplacer(s, matcher);
        do {
            replacer.append(replacements.next());
            if (!replacements.hasNext()) {
                do {
                    replacer.append(others);
                } while (matcher.find());
                break;
            }
        } while (matcher.find());
        replacer.appendTail();
        return replacer.toString();
    }

}
