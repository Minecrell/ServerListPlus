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

import java.util.regex.Matcher;

/**
 * Implements functionality similar to {@link Matcher#appendReplacement(StringBuffer, String)}
 * etc, but without the need to quote all the replacements...
 */
final class LiteralMatcherReplacer {
    private final String original;
    private final Matcher matcher;
    private final StringBuilder builder;
    private int lastAppend = 0;

    LiteralMatcherReplacer(String original, Matcher matcher) {
        this.original = original;
        this.matcher = matcher;
        this.builder = new StringBuilder(original.length());
    }

    boolean find() {
        return matcher.find();
    }

    String replaceAll(Object replacement) {
        if (replacement == null || !find())
            return original;

        do {
            append(replacement);
        } while (find());
        appendTail();

        return toString();
    }

    void append(Object replacement) {
        if (replacement == null)
            return;

        int start = matcher.start();
        if (lastAppend < start) {
            builder.append(original, lastAppend, start);
        }

        if (replacement instanceof CharSequence) {
            builder.append((CharSequence) replacement);
        } else {
            builder.append(replacement);
        }

        lastAppend = matcher.end();
    }

    void appendTail() {
        if (lastAppend < original.length()) {
            builder.append(original, lastAppend, original.length());
            lastAppend = original.length();
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
