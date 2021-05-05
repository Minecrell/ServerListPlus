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

package net.minecrell.serverlistplus.core.replacement.util;

import static org.junit.Assert.assertEquals;

import net.minecrell.serverlistplus.core.util.ContinousIterator;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternsTest {

    private static final Pattern HELLO_PATTERN = Pattern.compile("%hello(\\d+)%", Pattern.CASE_INSENSITIVE);

    @Test
    public void testReplaceAll() {
        assertEquals("$Hello World!$", Patterns.replace("%hello42%", HELLO_PATTERN, "$Hello World!$"));
        assertEquals("Hello World$ World$$!", Patterns.replace("Hello %hello0% %hello1%$!", HELLO_PATTERN, "World$"));
    }

    @Test
    public void testReplaceToNumber() {
        String text = "Hello %hello42% and %hello0%!";
        final Matcher matcher = HELLO_PATTERN.matcher(text);

        assertEquals("Hello $42$ and $0$!", Patterns.replace(matcher, text, new ContinousIterator<String>() {
            @Override
            public String next() {
                return '$' + matcher.group(1) + '$';
            }
        }));
    }

}
