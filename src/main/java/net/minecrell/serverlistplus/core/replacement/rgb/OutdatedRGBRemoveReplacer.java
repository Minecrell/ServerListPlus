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
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.replacement.PatternPlaceholder;
import net.minecrell.serverlistplus.core.status.StatusResponse;

import java.util.regex.Pattern;

public class OutdatedRGBRemoveReplacer extends PatternPlaceholder {

    private static final Pattern HEX_CODES = Pattern.compile("§(?:#[0-9A-F]{6}|x(?:§[0-9A-F]){6})", Pattern.CASE_INSENSITIVE);
    private static final int RGB_PROTOCOL_VERSION = 713; // 20w17a

    public static final OutdatedRGBRemoveReplacer INSTANCE = new OutdatedRGBRemoveReplacer();

    private OutdatedRGBRemoveReplacer() {
        super(HEX_CODES);
    }

    @Override
    public boolean find(String s) {
        return super.find(s) || RGBGradientReplacer.INSTANCE.find(s);
    }

    @Override
    public String replace(ServerListPlusCore core, String s) {
        return s;
    }

    @Override
    public String replace(StatusResponse response, String s) {
        if (!response.getCore().getConf(PluginConf.class).StripRGBIfOutdated) {
            return s;
        }

        Integer protocolVersion = response.getRequest().getProtocolVersion();
        if (protocolVersion != null && protocolVersion < RGB_PROTOCOL_VERSION) {
            // Strip RGB color codes only if client cannot display them
            return replace(s, "");
        }

        return s;
    }

}
