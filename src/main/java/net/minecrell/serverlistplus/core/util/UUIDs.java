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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedLongs;

import java.util.UUID;

public final class UUIDs {

    private UUIDs() {}

    public static final UUID EMPTY = new UUID(0, 0);

    public interface Variant {

        UUID parse(String uuid);

        String toString(UUID uuid);
    }

    public static final Variant STANDARD = new Variant() {

        @Override
        public UUID parse(String uuid) {
            return UUID.fromString(uuid);
        }

        @Override
        public String toString(UUID uuid) {
            return uuid.toString();
        }

    };

    private static String toHexString(long unsigned) {
        return Strings.padStart(UnsignedLongs.toString(unsigned, 16), 16, '0');
    }

    public static final Variant NO_DASHES = new Variant() {

        @Override
        public UUID parse(String uuid) {
            checkArgument(uuid.length() == 32, "Not an UUID: " + uuid);
            return new UUID(UnsignedLongs.parseUnsignedLong(uuid.substring(0, 16), 16),
                    UnsignedLongs.parseUnsignedLong(uuid.substring(16), 16));
        }

        @Override
        public String toString(UUID uuid) {
            return toHexString(uuid.getMostSignificantBits()) + toHexString(uuid.getLeastSignificantBits());
        }

    };

    public static UUID parse(String uuid) {
        if (uuid.indexOf('-') == -1) {
            return NO_DASHES.parse(uuid);
        }
        return STANDARD.parse(uuid);
    }

    public static UUID parseLenient(String uuid) {
        if (uuid.indexOf('-') == -1) {
            return NO_DASHES.parse(uuid);
        }

        // Try to parse
        try {
            return STANDARD.parse(uuid);
        } catch (IllegalArgumentException ignored) {
        }

        return NO_DASHES.parse(uuid.replace("-", ""));
    }

}
