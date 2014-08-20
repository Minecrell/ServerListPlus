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

public enum Format {
    BLACK ('0'),
    DARK_BLUE ('1'),
    DARK_GREEN ('2'),
    DARK_AQUA ('3'),
    DARK_RED ('4'),
    DARK_PURPLE ('5'),
    GOLD ('6'),
    GRAY ('7'),
    DARK_GRAY ('8'),
    BLUE ('9'),
    GREEN ('a'),
    AQUA ('b'),
    RED ('c'),
    LIGHT_PURPLE ('d'),
    YELLOW ('e'),
    WHITE ('f'),

    OBFUSCATED ('k'),
    BOLD ('l'),
    STRIKETHROUGH ('m'),
    UNDERLINE ('n'),
    ITALIC ('o'),
    RESET ('r');

    public final static char FORMAT_CHAR = '\u00A7';
    private final String format;

    private Format(char code) {
        this.format = new String(new char[] { FORMAT_CHAR, code });
    }

    @Override
    public String toString() {
        return format;
    }
}
