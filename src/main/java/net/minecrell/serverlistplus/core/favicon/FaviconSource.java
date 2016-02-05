/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
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

package net.minecrell.serverlistplus.core.favicon;

import com.google.common.base.Preconditions;

public class FaviconSource {
    private final String source;
    private final FaviconLoader loader;

    public FaviconSource(String source, FaviconLoader loader) {
        this.source = source;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FaviconSource)) return false;
        FaviconSource that = (FaviconSource) o;
        return loader.equals(that.loader) && !(source != null ? !source.equals(that.source) : that.source != null);

    }

    @Override
    public int hashCode() {
        return 31 * (source != null ? source.hashCode() : 0) + loader.hashCode();
    }
}
