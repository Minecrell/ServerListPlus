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

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.minecrell.serverlistplus.core.replacement.util.Patterns;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PatternPlaceholder extends AbstractDynamicReplacer implements DynamicPlaceholder {
    protected final @Getter Pattern pattern;

    protected PatternPlaceholder(Pattern pattern) {
        this.pattern = Preconditions.checkNotNull(pattern, "pattern");
    }

    public Matcher matcher(String s) {
        return pattern.matcher(s);
    }

    @Override
    public boolean find(String s) {
        return matcher(s).find();
    }

    @Override
    public String replace(String s, Object replacement) {
        return Patterns.replace(s, pattern, replacement);
    }

    @Override
    public String replace(String s, Iterator<?> replacements) {
        return Patterns.replace(s, pattern, replacements);
    }

    @Override
    public String replace(String s, Iterator<?> replacements, Object others) {
        return Patterns.replace(s, pattern, replacements, others);
    }
}
