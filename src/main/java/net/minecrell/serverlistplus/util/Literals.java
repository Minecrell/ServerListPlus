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

package net.minecrell.serverlistplus.util;

import java.util.Objects;

public final class Literals {

    private Literals() {
    }

    public static String replace(String s, String literal, Object replacement) {
        Objects.requireNonNull(s, "s");
        Objects.requireNonNull(literal, "literal");

        int i = s.indexOf(literal);
        if (i == -1) {
            return s;
        }

        final int length = s.length();
        final StringBuilder result = new StringBuilder(length);
        final int literalLength = literal.length();

        final String replacementString = String.valueOf(replacement);

        int pos = 0;

        do {
            result.append(s, pos, i);
            pos = i + literalLength;
            result.append(replacementString);

            if (pos >= length) {
                break;
            }

            i = s.indexOf(literal, pos);
        } while (i != -1);

        if (pos < length) {
            result.append(s, pos, length);
        }

        return result.toString();
    }

    public static String replace(String s, String literal, Object... replacements) {
        Objects.requireNonNull(s, "s");
        Objects.requireNonNull(literal, "literal");
        Objects.requireNonNull(replacements, "replacements");

        if (replacements.length == 0) {
            return s;
        }

        int i = s.indexOf(literal);
        if (i == -1) {
            return s;
        }

        final int length = s.length();
        final StringBuilder result = new StringBuilder(length);
        final int literalLength = literal.length();

        int pos = 0;

        for (Object replacement : replacements) {
            result.append(s, pos, i);
            pos = i + literalLength;
            result.append(replacement);

            if (pos >= length) {
                break;
            }

            i = s.indexOf(literal, pos);
            if (i == -1) {
                break;
            }
        }

        if (pos < length) {
            result.append(s, pos, length);
        }

        return result.toString();
    }

}
