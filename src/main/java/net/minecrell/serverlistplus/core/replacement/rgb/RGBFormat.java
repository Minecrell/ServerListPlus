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

package net.minecrell.serverlistplus.core.replacement.rgb;

import net.minecrell.serverlistplus.core.replacement.StaticReplacer;

public enum RGBFormat {
    UNSUPPORTED(0),
    ADVENTURE(8) { // §#rrggbb
        @Override
        public StringBuilder append(StringBuilder builder, int r, int g, int b) {
            return builder
                    .append(SECTION_CHAR)
                    .append('#')
                    .append(getHexChar(r >> 4))
                    .append(getHexChar(r))
                    .append(getHexChar(g >> 4))
                    .append(getHexChar(g))
                    .append(getHexChar(b >> 4))
                    .append(getHexChar(b));
        }
    },
    WEIRD_BUNGEE(14) { // §x§r§r§g§g§b§b
        @Override
        public StaticReplacer getReplacer() {
            return WeirdBungeeRGBReplacer.INSTANCE;
        }

        @Override
        public StringBuilder append(StringBuilder builder, int r, int g, int b) {
            return builder
                    .append(SECTION_CHAR)
                    .append('x')
                    .append(SECTION_CHAR)
                    .append(getHexChar(r >> 4))
                    .append(SECTION_CHAR)
                    .append(getHexChar(r))
                    .append(SECTION_CHAR)
                    .append(getHexChar(g >> 4))
                    .append(SECTION_CHAR)
                    .append(getHexChar(g))
                    .append(SECTION_CHAR)
                    .append(getHexChar(b >> 4))
                    .append(SECTION_CHAR)
                    .append(getHexChar(b));
        }
    };

    static final char SECTION_CHAR = '§';
    private final int length;

    RGBFormat(int length) {
        this.length = length;
    }

    public final int getLength() {
        return length;
    }

    public StaticReplacer getReplacer() {
        return null;
    }

    public StringBuilder append(StringBuilder builder, int r, int g, int b) {
        return builder;
    }

    private static final char[] hexTable = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    protected static char getHexChar(int n) {
        return hexTable[n & 0xf];
    }

}
