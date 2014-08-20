/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.google.common.base.Preconditions;

import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

public enum ResizeStrategy {
    NONE {
        @Override
        protected BufferedImage drawImage(BufferedImage original, BufferedImage resized) {
            return original;
        }

        @Override
        protected BufferedImage resizeImage(BufferedImage original, int width, int height) {
            return original;
        }
    }, SCALE {
        @Override
        protected BufferedImage drawImage(BufferedImage original, BufferedImage resized) {
            Graphics2D g = resized.createGraphics();
            // Minecraft-ish blocky scaling
            g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(original, 0, 0, resized.getWidth(), resized.getHeight(), null);
            g.dispose();
            return resized;
        }
    };

    protected abstract BufferedImage drawImage(BufferedImage original, BufferedImage resized);

    protected BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return drawImage(original, resized);
    }

    public final BufferedImage resize(BufferedImage img, int width, int height) {
        Preconditions.checkArgument(width > 0, "Width must be > 0");
        Preconditions.checkArgument(height > 0, "Height must be > 0");
        if (img.getWidth() == width && img.getHeight() == height) return img;
        return resizeImage(img, width, height);
    }
}
