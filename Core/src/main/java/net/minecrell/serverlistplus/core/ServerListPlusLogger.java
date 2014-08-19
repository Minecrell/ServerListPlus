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

package net.minecrell.serverlistplus.core;

import net.minecrell.serverlistplus.core.logging.JavaLogger;
import net.minecrell.serverlistplus.core.logging.Logger;
import net.minecrell.serverlistplus.core.util.Helper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class ServerListPlusLogger extends JavaLogger<ServerListPlusException> {
    private static final String LOG_PREFIX = "[Core] "; // Prefix used by core log messages

    private final ServerListPlusCore core;

    public ServerListPlusLogger(ServerListPlusCore core) {
        super(ServerListPlusCoreException.class);
        this.core = core;

        try {
            if (deleteOldFiles(core.getPlugin().getPluginFolder()))
                log(WARN, "Unable to delete all old log files.");
        } catch (Exception e) {
            log(WARN, e, "Unable to delete old log files.");
        }
    }

    @Override
    public java.util.logging.Logger getLogger() {
        return core.getPlugin().getLogger();
    }

    private boolean deleteOldFiles(Path folder) throws IOException {
        if (Files.notExists(folder)) return false;
        boolean failed = false;

        try (DirectoryStream<Path> files = Files.newDirectoryStream(folder, "ServerListPlus*.log*")) {
            for (Path path : files) {
                try {
                    Files.delete(path);
                    log(DEBUG, "Deleted old log file: " + path.getFileName());
                } catch (IOException e) {
                    log(DEBUG, "Unable to delete old log file: " + path.getFileName() + " -> " +
                            Helper.causedError(e));
                    failed = true;
                }
            }
        }

        return failed;
    }

    @Override
    public Logger log(Level level, String message) {
        return super.log(level, LOG_PREFIX + message);
    }

    @Override
    public Logger log(Level level, Throwable thrown, String message) {
        return super.log(level, thrown, LOG_PREFIX + message);
    }

    @Override
    protected ServerListPlusException createException(String message, Throwable thrown) {
        return new ServerListPlusCoreException(message, thrown);
    }

    private static final class ServerListPlusCoreException extends ServerListPlusException {
        private ServerListPlusCoreException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
