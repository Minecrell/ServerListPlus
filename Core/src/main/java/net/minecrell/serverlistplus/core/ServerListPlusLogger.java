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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ServerListPlusLogger {
    private static final String LOG_FILE = "ServerListPlus.log";

    private static final Level DEFAULT_EXCEPTION_LEVEL = Level.SEVERE;
    private static final String PREFIX = "[Core] ";

    private final ServerListPlusCore core;

    public ServerListPlusLogger(ServerListPlusCore core) {
        this.core = core;

        System.out.println(this.getLogger().getLevel().getLocalizedName());

        // Register a file handler for the logger but only if it has a parent to have compatibility with older
        // BungeeCord versions.

        if (this.getLogger().getParent() != null) {
            try {
                // Register the file handler for the logger
                Path logFile = core.getPlugin().getPluginFolder().resolve(LOG_FILE);
                if (!Files.isDirectory(logFile.getParent())) Files.createDirectories(logFile.getParent());
                FileHandler handler = new FileHandler(logFile.toString(),
                        1024 * 1024 /* 1 MB */, 1, true /* append */);
                handler.setLevel(Level.ALL);
                handler.setFormatter(new LogFormatter());
                this.getLogger().addHandler(handler);
            } catch (IOException e) {
                this.warning(e, "Unable to register file handler for the logger!");
            }
        }
    }

    private Logger getLogger() {
        return core.getPlugin().getLogger();
    }

    public String formatMessage(String message, Object... args) {
        return String.format(message, args);
    }

    public void debug(String message) {
        this.log(Level.FINE, message);
    }

    public void debug(Exception e, String message) {
        this.log(Level.FINE, e, message);
    }

    public void debugF(String message, Object... args) {
        this.logF(Level.FINE, message, args);
    }

    public void debugF(Exception e, String message, Object... args) {
        this.logF(Level.FINE, e, message, args);
    }

    public void info(String message) {
        this.log(Level.INFO, message);
    }

    public void info(Exception e, String message) {
        this.log(Level.INFO, e, message);
    }

    public void infoF(String message, Object... args) {
        this.logF(Level.INFO, message, args);
    }

    public void infoF(Exception e, String message, Object... args) {
        this.logF(Level.INFO, e, message, args);
    }

    public void warning(String message) {
        this.log(Level.WARNING, message);
    }

    public void warning(Exception e, String message) {
        this.log(Level.WARNING, e, message);
    }

    public void warningF(String message, Object... args) {
        this.logF(Level.WARNING, message, args);
    }

    public void warningF(Exception e, String message, Object... args) {
        this.logF(Level.WARNING, e, message, args);
    }

    public void severe(String message) {
        this.log(Level.SEVERE, message);
    }

    public void severe(Exception e, String message) {
        this.log(Level.SEVERE, e, message);
    }

    public void severeF(String message, Object... args) {
        this.logF(Level.SEVERE, message, args);
    }

    public void severeF(Exception e, String message, Object... args) {
        this.logF(Level.SEVERE, e, message, args);
    }

    public void log(Level level, String message) {
        this.getLogger().log(level, PREFIX + message);
    }

    public void logF(Level level, String message, Object... args) {
        this.log(level, this.formatMessage(message, args));
    }

    public boolean log(Exception e, String message) {
        return this.log(DEFAULT_EXCEPTION_LEVEL, e, message);
    }

    public boolean logF(Exception e, String message, Object... args) {
        return this.logF(DEFAULT_EXCEPTION_LEVEL, e, message, args);
    }

    public boolean log(Level level, Exception e, String message) {
        if (!checkException(e)) return false;
        this.getLogger().log(level, PREFIX + message, e); return true;
    }

    public boolean logF(Level level, Exception e, String message, Object... args) {
        return this.log(level, e, this.formatMessage(message, args));
    }


    public ServerListPlusException process(Exception e, String message) {
        return this.process(DEFAULT_EXCEPTION_LEVEL, e, message);
    }

    public ServerListPlusException processF(Exception e, String message, Object... args) {
        return this.processF(DEFAULT_EXCEPTION_LEVEL, e, message, args);
    }

    public ServerListPlusException process(Level level, Exception e, String message) {
        return this.log(level, e, message) ? new CoreServerListPlusException(message, e)
                : (ServerListPlusException) e;
    }

    public ServerListPlusException processF(Level level, Exception e, String message, Object... args) {
        return this.process(level, e, this.formatMessage(message, args));
    }

    private static boolean checkException(Exception e) {
        return e == null || e.getClass() != CoreServerListPlusException.class;
    }

    private static final class CoreServerListPlusException extends ServerListPlusException {
        private CoreServerListPlusException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class LogFormatter extends Formatter {
        private static final DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder formatted = new StringBuilder()
                    .append(date.format(record.getMillis()))
                    .append(" [")
                    .append(record.getLevel().getLocalizedName())
                    .append("] ")
                    .append(formatMessage(record))
                    .append('\n');

            if (record.getThrown() != null) {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                formatted.append(writer);
            }

            return formatted.toString();
        }
    }
}
