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

package net.minecrell.serverlistplus.core.status;

import lombok.Value;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.util.IntegerRange;

import java.util.List;

@Value
public class StatusPatch {
    private final List<String> descriptions, playerHovers;

    private final List<IntegerRange> online, max;
    private final Boolean hidePlayers;
    private final List<String> slots;

    private final List<String> versions;
    private final Integer protocolVersion;

    private final List<FaviconSource> favicons;

    public boolean hasChanges() {
        return descriptions != null || playerHovers != null ||
                online != null || max != null ||
                hidePlayers != null ||
                slots != null ||
                versions != null || protocolVersion != null ||
                favicons != null;
    }

    public static StatusPatch empty() {
        return new StatusPatch(null, null, null, null, null, null, null, null, null);
    }
}
