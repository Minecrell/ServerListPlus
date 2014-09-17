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

package net.minecrell.serverlistplus.core.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import net.minecrell.serverlistplus.core.config.yaml.ConfigurationSerializable;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

@Value @RequiredArgsConstructor
public class TimeUnitValue implements ConfigurationSerializable {
    private static final BiMap<Character, TimeUnit> UNIT_LOOKUP = ImmutableBiMap.of(
            's', TimeUnit.SECONDS,
            'm', TimeUnit.MINUTES,
            'h', TimeUnit.HOURS,
            'd', TimeUnit.DAYS
    );

    private final @NonNull TimeUnit unit;
    private final long value;

    protected TimeUnitValue(TimeUnitValue other) {
        this(other.unit, other.value);
    }

    public TimeUnitValue(String s) {
        this(parse(s));
    }

    public static TimeUnitValue parse(String s) {
        try { // If no unit is specified then it should be milliseconds
            return new TimeUnitValue(TimeUnit.MILLISECONDS, Long.parseLong(s));
        } catch (NumberFormatException ignored) {}

        Preconditions.checkArgument(!s.isEmpty(), "Time is empty");
        char unitChar = s.charAt(s.length() - 1);
        TimeUnit unit = UNIT_LOOKUP.get(unitChar);
        Preconditions.checkArgument(unit != null, "Unknown time unit: " + unitChar);

        String num = s.substring(0, s.length() - 1);
        long value = 1;
        if (!num.isEmpty())
            try {
                value = Long.parseLong(num);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid time: " + num, e);
            }

        return new TimeUnitValue(unit, value);
    }

    @Override
    public String toString() {
        return serialize().toString();
    }

    @Override
    public Object serialize() {
        Character unitChar = UNIT_LOOKUP.inverse().get(unit);
        return unitChar == null ? value : Long.toString(value) + unitChar;
    }
}
