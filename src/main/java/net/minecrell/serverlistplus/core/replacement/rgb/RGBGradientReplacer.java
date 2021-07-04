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

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.math.IntMath;
import lombok.Data;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.replacement.PatternPlaceholder;
import net.minecrell.serverlistplus.core.replacement.ReplacementManager;
import net.minecrell.serverlistplus.core.replacement.util.Patterns;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.ContinousIterator;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RGBGradientReplacer extends PatternPlaceholder {

    private static final Pattern PATTERN = Pattern.compile(
            "%gradient((?:#[0-9A-F]{6}){2,})%((?:§[K-O])*)(.*?)%gradient%", Pattern.CASE_INSENSITIVE);

    private static final Splitter COLOR_SPLITTER = Splitter.on('#').omitEmptyStrings();

    public static final RGBGradientReplacer INSTANCE = new RGBGradientReplacer();

    @Data
    static class Color {
        final int r, g, b;
    }

    @Data
    static class Gradient {
        final Color start, end;
    };

    static List<Gradient> parseGradients(String text) {
        List<Gradient> gradients = new ArrayList<>();
        Color last = null;
        for (String color : COLOR_SPLITTER.split(text)) {
            Color decoded = new Color(
                    Integer.parseInt(color.substring(0, 2), 16),
                    Integer.parseInt(color.substring(2, 4), 16),
                    Integer.parseInt(color.substring(4, 6), 16)
            );
            if (last != null) {
                gradients.add(new Gradient(last, decoded));
            }
            last = decoded;
        }
        return gradients;
    }

    private RGBGradientReplacer() {
        super(PATTERN);
    }

    private static String replace(ServerListPlusCore core, String s, final boolean onlyStatic) {
        final RGBFormat rgbFormat = core.getPlugin().getRGBFormat();
        final Matcher matcher = PATTERN.matcher(s);
        return Patterns.replace(matcher, s, new ContinousIterator<Object>() {
            @Override
            public Object next() {
                String text = matcher.group(3);
                if (Strings.isNullOrEmpty(text))
                    return "";

                if (onlyStatic && ReplacementManager.hasDynamic(text)) {
                    return matcher.group();
                }

                List<Gradient> gradients = parseGradients(matcher.group(1));
                if (gradients.isEmpty())
                    return text;

                String prefix = Strings.nullToEmpty(matcher.group(2));

                int steps = text.length() - 1;
                StringBuilder builder = new StringBuilder((steps + 1) * (rgbFormat.getLength() + prefix.length() + 1));

                Color first = gradients.get(0).start;
                rgbFormat.append(builder, first.r, first.g, first.b).append(prefix).append(text.charAt(0));
                if (steps == 0) {
                    return builder;
                }

                int ngradients = gradients.size();
                int stepsPerGradient = steps / ngradients;
                int remSteps = steps % ngradients;

                int i = 0;
                for (Gradient g : gradients) {
                    steps = stepsPerGradient;
                    if (remSteps > 0) {
                        --remSteps; ++steps;
                    } else if (steps == 0) {
                        break;
                    }

                    if (steps > 1) {
                        int diffR = g.end.r - g.start.r;
                        int diffG = g.end.g - g.start.g;
                        int diffB = g.end.b - g.start.b;

                        for (int j = 1; j < steps; j++) {
                            rgbFormat.append(builder,
                                    g.start.r + IntMath.divide(diffR * j, steps + 1, RoundingMode.HALF_EVEN),
                                    g.start.g + IntMath.divide(diffG * j, steps + 1, RoundingMode.HALF_EVEN),
                                    g.start.b + IntMath.divide(diffB * j, steps + 1, RoundingMode.HALF_EVEN)
                            ).append(prefix).append(text.charAt(++i));
                        }
                    }

                    rgbFormat.append(builder, g.end.r, g.end.g, g.end.b).append(prefix).append(text.charAt(++i));
                }

                return builder;
            }
        });
    }

    @Override
    public String replace(ServerListPlusCore core, String s) {
        return replace(core, s, true);
    }

    @Override
    public String replace(StatusResponse response, String s) {
        return replace(response.getCore(), s, false);
    }

}
