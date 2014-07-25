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

package net.minecrell.serverlistplus.bukkit;

import net.minecrell.serverlistplus.core.config.io.IOUtil;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.plugin.ServerType;

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

import org.apache.logging.log4j.LogManager;

public final class BukkitLogger {
    private BukkitLogger() {}

    private static final String LOG_FILE = "ServerListPlus.log";

    public static void enableDebugLevels(ServerListPlusPlugin plugin) throws Throwable {
        if (LogManager.getRootLogger().isTraceEnabled()) return;

        // Bukkit doesn't want to print lower log levels, see https://bukkit.atlassian.net/browse/BUKKIT-5712
        // That's why we just add our own log file.

        Path logFile = plugin.getPluginFolder().resolve(LOG_FILE);
        if (!Files.isDirectory(logFile.getParent())) Files.createDirectories(logFile.getParent());

        FileHandler handler = new FileHandler(logFile.toString(), 1024 * 1024 /* 1 MB */, 1, true /* append */);
        handler.setEncoding(IOUtil.CHARSET.name());
        handler.setLevel(Level.ALL);
        handler.setFormatter(new LogFormatter(plugin.getServerType()));
        plugin.getLogger().addHandler(handler);
    }

    public static class LogFormatter extends Formatter {
        private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private static final String LOG_PREFIX = "[Core] "; // Prefix used by core log messages
        private static final String PLUGIN_PREFIX = "[ServerListPlus] "; // Prefix used by plugin logger

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
                formatted.append(pluginPrefix);

            // Append message and new line
            formatted.append(message).append('\n');

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
