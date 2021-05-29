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

import net.minecrell.serverlistplus.core.replacement.util.Patterns;

import java.util.regex.Pattern;

public final class FormattingCodes {

    private FormattingCodes() {
    }

    private static final Pattern CONFIG_CODES_HEX = Pattern.compile("&([0-9A-FK-ORX]|#[0-9A-F]{6})", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEGACY_CODES_HEX = Pattern.compile("§([0-9A-FK-ORX]|#[0-9A-F]{6})", Pattern.CASE_INSENSITIVE);

    public static String colorizeHex(String s) {
        return CONFIG_CODES_HEX.matcher(s).replaceAll("§$1");
    }

    public static String stripLegacyHex(String s) {
        return Patterns.replace(s, LEGACY_CODES_HEX, "");
    }

}
