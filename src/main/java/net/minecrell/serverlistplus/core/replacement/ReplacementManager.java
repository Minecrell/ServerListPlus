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

package net.minecrell.serverlistplus.core.replacement;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.replacement.rgb.OutdatedRGBRemoveReplacer;
import net.minecrell.serverlistplus.core.replacement.rgb.RGBFormat;
import net.minecrell.serverlistplus.core.replacement.rgb.RGBGradientReplacer;
import net.minecrell.serverlistplus.core.status.StatusResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ReplacementManager {
    private ReplacementManager() {}

    private static final Set<StaticReplacer> staticReplacers = new HashSet<>();
    private static final List<StaticReplacer> earlyStaticReplacers = new ArrayList<>();
    private static final List<StaticReplacer> lateStaticReplacers = new ArrayList<>();
    private static final Set<DynamicReplacer> dynamicReplacers = new HashSet<>();
    private static final List<DynamicReplacer> lateDynamicReplacers = new ArrayList<>();

    static {
        // Register default replacements
        Collections.addAll(dynamicReplacers, DefaultLiteralPlaceholder.values());
        Collections.addAll(dynamicReplacers, DefaultPatternPlaceholder.values());
    }

    public static Set<StaticReplacer> getStatic() {
        return staticReplacers;
    }

    public static Set<DynamicReplacer> getDynamic() {
        return dynamicReplacers;
    }

    public static void registerDefault(ServerListPlusCore core) {
        earlyStaticReplacers.clear();
        lateStaticReplacers.clear();
        lateDynamicReplacers.clear();

        RGBFormat rgbFormat = core.getPlugin().getRGBFormat();
        if (rgbFormat != RGBFormat.UNSUPPORTED) {
            StaticReplacer replacer = rgbFormat.getReplacer();
            if (replacer != null) {
                earlyStaticReplacers.add(replacer);
            }
        }

        earlyStaticReplacers.add(ColorReplacer.INSTANCE);

        if (rgbFormat != RGBFormat.UNSUPPORTED) {
            lateStaticReplacers.add(RGBGradientReplacer.INSTANCE);
            lateDynamicReplacers.add(RGBGradientReplacer.INSTANCE);
            lateDynamicReplacers.add(OutdatedRGBRemoveReplacer.INSTANCE);
        }
    }

    public static String replaceStatic(ServerListPlusCore core, String s) {
        for (StaticReplacer replacer : earlyStaticReplacers)
            s = replacer.replace(core, s);
        for (StaticReplacer replacer : staticReplacers)
            s = replacer.replace(core, s);
        for (StaticReplacer replacer : lateStaticReplacers)
            s = replacer.replace(core, s);
        return s;
    }

    /**
     * @deprecated Use {@link #findDynamicList(String)}
     */
    @Deprecated
    public static Set<DynamicReplacer> findDynamic(String s) {
        Set<DynamicReplacer> result = new HashSet<>();
        for (DynamicReplacer replacer : dynamicReplacers)
            if (replacer.find(s)) result.add(replacer);
        return result;
    }

    public static List<DynamicReplacer> findDynamicList(String s) {
        List<DynamicReplacer> result = new ArrayList<>();
        for (DynamicReplacer replacer : dynamicReplacers)
            if (replacer.find(s)) result.add(replacer);
        for (DynamicReplacer replacer : lateDynamicReplacers)
            if (replacer.find(s)) result.add(replacer);
        return result;
    }

    public static boolean hasDynamic(String s) {
        for (DynamicReplacer replacer : dynamicReplacers)
            if (replacer.find(s)) return true;
        return false;
    }

    public static String replaceDynamic(StatusResponse response, String s, Iterable<DynamicReplacer> replacers) {
        for (DynamicReplacer replacer : replacers)
            s = replacer.replace(response, s);
        return s;
    }
}
