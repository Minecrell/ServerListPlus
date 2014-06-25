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

import net.minecrell.serverlistplus.core.config.io.IOUtil;
import net.minecrell.serverlistplus.core.plugin.ServerType;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ServerListPlusLogger {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String LOG_PREFIX = "[Core] "; // Prefix used by core log messages
    private static final String PLUGIN_PREFIX = "[ServerListPlus] "; // Prefix used by plugin logger

    private static final String LOG_FILE = "ServerListPlus.log";

    private static final Level DEFAULT_EXCEPTION_LEVEL = Level.SEVERE;

    private final ServerListPlusCore core;

    public ServerListPlusLogger(ServerListPlusCore core) {
        this.core = core;

        // Set BungeeCord logger level to ALL so it logs FINE messages when pull requests wasn't merged yet
        boolean bungee = core.getPlugin().getServerType() == ServerType.BUNGEE;
        if (bungee && (this.getLogger().getParent() == null
                || this.getLogger().getParent().getLevel() != Level.ALL)) {
            this.getLogger().setLevel(Level.ALL);
        }

        // Register a file handler for the logger but only if it has a parent to have compatibility with older
        // BungeeCord versions.
        if (!bungee || this.getLogger().getParent() != null)
            try {
                // Register the file handler for the logger
                Path logFile = core.getPlugin().getPluginFolder().resolve(LOG_FILE);
                if (!Files.isDirectory(logFile.getParent())) Files.createDirectories(logFile.getParent());
                Files.write(logFile, Collections.singleton(
                            "--- # " + DATE_FORMAT.format(System.currentTimeMillis())
                    ), IOUtil.CHARSET, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

                FileHandler handler = new FileHandler(logFile.toString(),
                        1024 * 1024 /* 1 MB */, 1, true /* append */);
                handler.setEncoding(IOUtil.CHARSET.name());
                handler.setLevel(Level.ALL);
                handler.setFormatter(new LogFormatter(core.getPlugin().getServerType()));
                this.getLogger().addHandler(handler);
            } catch (IOException e) {
                this.warning(e, "Unable to register file handler for the logger!");
            }
    }

    private Logger getLogger() {
        return core.getPlugin().getLogger();
    }

    public String formatMessage(String message, Object... args) {
        return String.format(message, args);
    }

    public ServerListPlusLogger debug(String message) {
        return this.log(Level.FINE, message);
    }

    public ServerListPlusLogger debug(Exception e, String message) {
        return this.log(Level.FINE, e, message);
    }

    public ServerListPlusLogger debugF(String message, Object... args) {
        return this.logF(Level.FINE, message, args);
    }

    public ServerListPlusLogger debugF(Exception e, String message, Object... args) {
        return this.logF(Level.FINE, e, message, args);
    }

    public ServerListPlusLogger info(String message) {
        return this.log(Level.INFO, message);
    }

    public ServerListPlusLogger info(Exception e, String message) {
        return this.log(Level.INFO, e, message);
    }

    public ServerListPlusLogger infoF(String message, Object... args) {
        return this.logF(Level.INFO, message, args);
    }

    public ServerListPlusLogger infoF(Exception e, String message, Object... args) {
        return this.logF(Level.INFO, e, message, args);
    }

    public ServerListPlusLogger warning(String message) {
        return this.log(Level.WARNING, message);
    }

    public ServerListPlusLogger warning(Exception e, String message) {
        return this.log(Level.WARNING, e, message);
    }

    public ServerListPlusLogger warningF(String message, Object... args) {
        return this.logF(Level.WARNING, message, args);
    }

    public ServerListPlusLogger warningF(Exception e, String message, Object... args) {
        return this.logF(Level.WARNING, e, message, args);
    }

    public ServerListPlusLogger severe(String message) {
        return this.log(Level.SEVERE, message);
    }

    public ServerListPlusLogger severe(Exception e, String message) {
        return this.log(Level.SEVERE, e, message);
    }

    public ServerListPlusLogger severeF(String message, Object... args) {
        return this.logF(Level.SEVERE, message, args);
    }

    public ServerListPlusLogger severeF(Exception e, String message, Object... args) {
        return this.logF(Level.SEVERE, e, message, args);
    }

    public ServerListPlusLogger log(Level level, String message) {
        this.getLogger().log(level, LOG_PREFIX + message); return this;
    }

    public ServerListPlusLogger logF(Level level, String message, Object... args) {
        return this.log(level, this.formatMessage(message, args));
    }

    public ServerListPlusLogger log(Exception e, String message) {
        return this.log(DEFAULT_EXCEPTION_LEVEL, e, message);
    }

    public ServerListPlusLogger logF(Exception e, String message, Object... args) {
        return this.logF(DEFAULT_EXCEPTION_LEVEL, e, message, args);
    }

    public ServerListPlusLogger log(Level level, Exception e, String message) {
        this.getLogger().log(level, LOG_PREFIX + message, e); return this;
    }

    public ServerListPlusLogger logF(Level level, Exception e, String message, Object... args) {
        return this.log(level, e, this.formatMessage(message, args));
    }

    public ServerListPlusException process(Exception e, String message) {
        return this.process(DEFAULT_EXCEPTION_LEVEL, e, message);
    }

    public ServerListPlusException processF(Exception e, String message, Object... args) {
        return this.processF(DEFAULT_EXCEPTION_LEVEL, e, message, args);
    }

    public ServerListPlusException process(Level level, Exception e, String message) {
        if (e != null && e.getClass() == CoreServerListPlusException.class) return (ServerListPlusException) e;
        this.log(level, e, message);
        return new CoreServerListPlusException(message, e);
    }

    public ServerListPlusException processF(Level level, Exception e, String message, Object... args) {
        return this.process(level, e, this.formatMessage(message, args));
    }

    private static final class CoreServerListPlusException extends ServerListPlusException {
        private CoreServerListPlusException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class LogFormatter extends Formatter {
        private final String pluginPrefix;

        private LogFormatter(ServerType type) {
            this.pluginPrefix = "[" + type + "] ";
        }

        @Override
        public String format(LogRecord record) {
            StringBuilder formatted = new StringBuilder().append(DATE_FORMAT.format(record.getMillis()))
                    .append(" [").append(record.getLevel().getName()).append("] ");

            String message = formatMessage(record);
            if (message.startsWith(PLUGIN_PREFIX)) // Remove plugin prefix, as this log is only for ServerListPlus
                message = message.substring(PLUGIN_PREFIX.length());

            // If there is not the core prefix, the log messages has to come from the plugin, add plugin type
            if (!message.startsWith(LOG_PREFIX))
                formatted.append(pluginPrefix).append(message);
            // Indent core messages so they start at the same column as the other ones
            else formatted.append(LOG_PREFIX).append("  ").append(message.substring(LOG_PREFIX.length()));

            // Append message and new line
            formatted.append('\n');

            // Print the exception if the record has one
            if (record.getThrown() != null) {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                formatted.append(writer);
            }

            return formatted.toString();
        }
    }
}
