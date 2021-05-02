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

package net.minecrell.serverlistplus.core.favicon;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class FaviconSource {

    public static final FaviconSource NONE = new FaviconSource();

    private final String source;
    private final FaviconLoader loader;

    private FaviconSource() {
        this.source = null;
        this.loader = null;
    }

    public FaviconSource(String source, FaviconLoader loader) {
        this.source = Preconditions.checkNotNull(source);
        this.loader = Preconditions.checkNotNull(loader);
    }

    public String getSource() {
        return source;
    }

    public FaviconLoader getLoader() {
        return loader;
    }

    public FaviconSource withSource(String source) {
        return new FaviconSource(source, loader);
    }

}
