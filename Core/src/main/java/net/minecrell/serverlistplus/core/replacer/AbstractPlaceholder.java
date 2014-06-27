/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your complete server status ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.core.replacer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

public abstract class AbstractPlaceholder implements DynamicPlaceholder {
    protected final Pattern pattern;

    public AbstractPlaceholder(Pattern pattern) {
        this.pattern = Preconditions.checkNotNull(pattern, "pattern");
    }

    @Override
    public Pattern pattern() {
        return pattern;
    }

    @Override
    public Matcher matcher(String s) {
        return pattern.matcher(s);
    }

    @Override
    public boolean find(String s) {
        return this.matcher(s).find();
    }

    @Override
    public String replace(String s, String replacement) {
        return this.matcher(s).replaceAll(replacement);
    }
}
