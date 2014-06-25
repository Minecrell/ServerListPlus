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

package net.minecrell.serverlistplus.core.favicon;

import net.minecrell.serverlistplus.core.ServerListPlusCore;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public final class FaviconHelper {
    private FaviconHelper() {}

    public static BufferedImage fromStream(InputStream in) throws IOException {
        return ImageIO.read(in);
    }

    public static BufferedImage fromURL(URL url) throws IOException {
        return ImageIO.read(url);
    }

    public static BufferedImage load(ServerListPlusCore core, FaviconSource source) throws IOException {
        return source.getLoader().load(core, source.getSource());
    }

    public static BufferedImage loadSafely(ServerListPlusCore core, FaviconSource source) {
        try { // Try loading the favicon
            return load(core, source);
        } catch (IOException e) {
            core.getLogger().warningF("Unable to load favicon from %s: %s\n%s",
                    source.getLoader().toString(), source.getSource(), e.getMessage());
            core.getLogger().debugF(e, "Unable to load favicon from %s: %s",
                    source.getLoader().toString(), source.getSource());
            return null;
        }
    }
}
