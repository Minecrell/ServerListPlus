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

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.util.Helper;

import java.awt.image.BufferedImage;

import static net.minecrell.serverlistplus.core.logging.Logger.Level.DEBUG;
import static net.minecrell.serverlistplus.core.logging.Logger.Level.WARN;

public abstract class FaviconCache<T> {
    private final ServerListPlusPlugin plugin;
    private LoadingCache<FaviconSource, Optional<T>> loadingCache;

    private final CacheLoader<FaviconSource, Optional<T>> cacheLoader =
        new CacheLoader<FaviconSource, Optional<T>>() {
            @Override
            public Optional<T> load(FaviconSource source) throws Exception {
                BufferedImage image;
                try {
                    image = FaviconHelper.load(plugin.getCore(), source);
                } catch (Exception e) {
                    plugin.getCore().getLogger()
                        .log(WARN, "Unable to load favicon from {}: {} -> {}",
                            source.getLoader(), source.getSource(), Helper.causedException(e))
                        .log(DEBUG, e, "Unable to load favicon from {}: {}",
                            source.getLoader(), source.getSource());
                    return Optional.absent();
                }

                return Optional.of(createFavicon(image));
            }
        };

    public FaviconCache(ServerListPlusPlugin plugin, CacheBuilderSpec spec) {
        this.plugin = plugin;
        reload(spec);
    }

    public Optional<T> get(FaviconSource source) {
        return loadingCache.getUnchecked(source);
    }

    public Optional<T> getIfPresent(FaviconSource source) {
        return loadingCache.getIfPresent(source);
    }

    public LoadingCache<FaviconSource, Optional<T>> getLoadingCache() {
        return loadingCache;
    }

    public void clear() {
        loadingCache.invalidateAll();
        loadingCache.cleanUp();
    }

    public void reload(CacheBuilderSpec spec) {
        loadingCache = CacheBuilder.from(spec).build(cacheLoader);
    }

    protected abstract T createFavicon(BufferedImage image) throws Exception;
}
