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

package net.minecrell.serverlistplus.core.replacement;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.status.StatusResponse;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ReplacementManager {
    private ReplacementManager() {}

    private static final Set<StaticReplacer> staticReplacers = new HashSet<>();
    private static final Set<DynamicReplacer> dynamicReplacers = new HashSet<>();

    static {
        // Register default replacements
        staticReplacers.add(ColorReplacer.INSTANCE);
        Collections.addAll(dynamicReplacers, DefaultLiteralPlaceholder.values());
        Collections.addAll(dynamicReplacers, DefaultPatternPlaceholder.values());
    }

    public static Set<StaticReplacer> getStatic() {
        return staticReplacers;
    }

    public static Set<DynamicReplacer> getDynamic() {
        return dynamicReplacers;
    }

    public static String replaceStatic(ServerListPlusCore core, String s) {
        for (StaticReplacer replacer : staticReplacers)
            s = replacer.replace(core, s);
        return s;
    }

    public static Set<DynamicReplacer> findDynamic(String s) {
        Set<DynamicReplacer> result = new HashSet<>();
        for (DynamicReplacer replacer : dynamicReplacers)
            if (replacer.find(s)) result.add(replacer);
        return result;
    }

    public static String replaceDynamic(StatusResponse response, String s, Iterable<DynamicReplacer> replacers) {
        for (DynamicReplacer replacer : replacers)
            s = replacer.replace(response, s);
        return s;
    }
}
