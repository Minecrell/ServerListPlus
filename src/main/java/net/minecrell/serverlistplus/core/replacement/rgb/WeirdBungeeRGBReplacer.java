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

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.replacement.StaticReplacer;

import java.util.regex.Pattern;

/**
 * Spigot/BungeeCord use an incredibly ugly format for RGB colors,
 * e.g. &x&a&b&c&d&e&f for #abcdef. It's incredibly annoying to read/write.
 * This {@link StaticReplacer} translates from the easier readable format used in
 * other projects (&#abcdef) to Spigot/Bungee's format to allow using the nicer
 * format on all platforms where ServerListPlus runs.
 */
final class WeirdBungeeRGBReplacer implements StaticReplacer {

    static final WeirdBungeeRGBReplacer INSTANCE = new WeirdBungeeRGBReplacer();
    private static final Pattern SANE_HEX_PATTERN = Pattern.compile(
            "&#([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])", Pattern.CASE_INSENSITIVE);

    private WeirdBungeeRGBReplacer() {}

    @Override
    public String replace(ServerListPlusCore core, String s) {
        // Translate to insane Spigot/Bungee equivalent, e.g. &#abcdef -> &x&a&b&c&d&e&f
        return SANE_HEX_PATTERN.matcher(s).replaceAll("&x&$1&$2&$3&$4&$5&$6");
    }

}
