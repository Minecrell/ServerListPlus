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

import java.util.concurrent.TimeUnit;

public final class TimeUnitValue {

    private static final String[] symbols = {"ns", "μs", "ms", "s", "min", "h", "d"};
    private static final TimeUnit[] units = TimeUnit.values();

    private final TimeUnit unit;
    private final long value;

    public TimeUnitValue(TimeUnit unit, long value) {
        this.unit = unit;
        this.value = value;
    }

    @Override
    public String toString() {
        return value + symbols[unit.ordinal()];
    }

}
