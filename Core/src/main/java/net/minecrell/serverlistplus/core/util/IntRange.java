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

import java.util.regex.Pattern;

public class IntRange {
    private static final Pattern SEPARATOR = Pattern.compile("..", Pattern.LITERAL);

    private final int from, to;

    public IntRange(int single) {
        this.from = this.to = single;
    }

    public IntRange(int from, int to) {
        if (from > to) throw new IllegalArgumentException("Invalid range: from " + from + " to " + to);
        this.from = from;
        this.to = to;
    }

    public IntRange(IntRange other) {
        this(other.from, other.to);
    }

    public IntRange(String range) {
        this(from(range));
    }

    public boolean isSingle() {
        return from == to;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public static IntRange from(String range) {
        try {
            return new IntRange(Integer.parseInt(range));
        } catch (NumberFormatException ignored) {}

        // Let's try to parse the range
        String[] parts = SEPARATOR.split(range, 2);
        if (parts.length != 2) throw new IllegalArgumentException("Invalid range: " + range);

        // Now parse both numbers
        return new IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof IntRange)) return false;
        IntRange intRange = (IntRange) o;
        return from == intRange.from && to == intRange.to;
    }

    @Override
    public int hashCode() {
        return 31 * from + to;
    }
}
