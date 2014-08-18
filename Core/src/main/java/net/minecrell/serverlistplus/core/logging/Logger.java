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

package net.minecrell.serverlistplus.core.logging;

import java.util.logging.Level;

public interface Logger<E extends Throwable> {
    public static final Level
            DEBUG = Level.FINE,
            REPORT = Level.CONFIG,
            INFO = Level.INFO,
            WARN = Level.WARNING,
            ERROR = Level.SEVERE;

    public Logger log(Level level, String message);
    public Logger log(Level level, String message, Object arg);
    public Logger log(Level level, String message, Object... args);
    public Logger logf(Level level, String message, Object... args);

    public Logger log(Throwable thrown, String message);
    public Logger log(Throwable thrown, String message, Object arg);
    public Logger log(Throwable thrown, String message, Object... args);
    public Logger logf(Throwable thrown, String message, Object... args);

    public Logger log(Level level, Throwable thrown, String message);
    public Logger log(Level level, Throwable thrown, String message, Object arg);
    public Logger log(Level level, Throwable thrown, String message, Object... args);
    public Logger logf(Level level, Throwable thrown, String message, Object... args);

    public E process(Throwable thrown, String message);
    public E process(Throwable thrown, String message, Object arg);
    public E process(Throwable thrown, String message, Object... args);
    public E processf(Throwable thrown, String message, Object... args);

    public E process(Level level, Throwable thrown, String message);
    public E process(Level level, Throwable thrown, String message, Object arg);
    public E process(Level level, Throwable thrown, String message, Object... args);
    public E processf(Level level, Throwable thrown, String message, Object... args);
}
