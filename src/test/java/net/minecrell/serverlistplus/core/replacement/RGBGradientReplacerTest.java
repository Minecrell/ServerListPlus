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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.replacement.rgb.RGBFormat;
import net.minecrell.serverlistplus.core.replacement.rgb.RGBGradientReplacer;
import net.minecrell.serverlistplus.core.util.FormattingCodes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RGBGradientReplacerTest {

    @Parameterized.Parameters(name = "{0}")
    public static Object[] data() {
        return new RGBFormat[]{ RGBFormat.ADVENTURE, RGBFormat.WEIRD_BUNGEE };
    }

    @Parameterized.Parameter
    public RGBFormat rgbFormat;

    @Test
    public void testPreserveInput() {
        // Make sure the replacer always preserves the entire input
        // no matter how long the string is or how many colors are given

        ServerListPlusCore core = mock(ServerListPlusCore.class);
        ServerListPlusPlugin plugin = mock(ServerListPlusPlugin.class);
        when(core.getPlugin()).thenReturn(plugin);
        when(plugin.getRGBFormat()).thenReturn(rgbFormat);

        StringBuilder b = new StringBuilder();
        for (int i = 2; i < 32; ++i) {
            for (int j = 0; j < 256; ++j) {
                b.setLength(0);

                b.append("%gradient");
                for (int k = 0; k < i; ++k) {
                    b.append("#000000");
                }
                b.append('%');

                int start = b.length();
                for (int k = 0; k < j; ++k) {
                    b.append('A');
                }
                String expected = b.substring(start);
                assertEquals(j, expected.length());

                b.append("%gradient%");

                String result = RGBGradientReplacer.INSTANCE.replace(core, b.toString());
                assertEquals(expected.length() * (rgbFormat.getLength() + 1), result.length());
                assertEquals(expected, FormattingCodes.stripLegacyHex(result));
            }
        }
    }

}
