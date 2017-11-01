/*
 * ServerListPlus
 * Copyright (C) 2017 Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.config.loader;

import java.nio.file.Path;

public class ConfigurationFileException extends ConfigurationLoaderException {

    private final Path path;

    public ConfigurationFileException(String message, String key, Path path) {
        super(message, key);
        this.path = path;
    }

    public ConfigurationFileException(String message, Throwable cause, String key, Path path) {
        super(message, cause, key);
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

}
