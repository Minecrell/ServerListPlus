/*
 *       __                           __ _     _     ___ _
 *      / _\ ___ _ ____   _____ _ __ / /(_)___| |_  / _ \ |_   _ ___
 *      \ \ / _ \ '__\ \ / / _ \ '__/ / | / __| __|/ /_)/ | | | / __|
 *      _\ \  __/ |   \ V /  __/ | / /__| \__ \ |_/ ___/| | |_| \__ \
 *      \__/\___|_|    \_/ \___|_| \____/_|___/\__\/    |_|\__,_|___/
 *                       Customize your server ping!
 *
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.api;

import java.util.Formatter;
import java.util.logging.Level;

public interface ServerListPlusLogger extends ServerListPlusClass {
    /**
     * Formats the specified message using a formatter.
     * @param message The log message, as a valid string format.
     * @param args The arguments to be passed to the formatter.
     * @return The formatted message.
     * @see String#format(String, Object...)
     * @see Formatter
     */
    String formatMessage(String message, Object... args);

    /**
     * Log an INFO message.
     * @param message The log message.
     * @see Level#INFO
     */
    void info(String message);

    /**
     * Log an INFO message and formats it using a formatter.
     * @param message The log message, as a valid string format.
     * @param args The arguments to be passed to the formatter.
     * @see Level#INFO
     * @see String#format(String, Object...)
     * @see Formatter
     */
    void infoF(String message, Object... args);

    /**
     * Log an WARNING message.
     * @param message The log message.
     * @see Level#WARNING
     */
    void warning(String message);

    /**
     * Log an WARNING message and formats it using a formatter.
     * @param message The log message, as a valid string format.
     * @param args The arguments to be passed to the formatter.
     * @see Level#WARNING
     * @see String#format(String, Object...)
     * @see Formatter
     */
    void warningF(String message, Object... args);

    /**
     * Log an SEVERE message.
     * @param message The log message.
     * @see Level#SEVERE
     */
    void severe(String message);

    /**
     * Log an SEVERE message and formats it using a formatter.
     * @param message The log message, as a valid string format.
     * @param args The arguments to be passed to the formatter.
     * @see Level#SEVERE
     * @see String#format(String, Object...)
     * @see Formatter
     */
    void severeF(String message, Object... args);

    /**
     * Log a message with a specified level.
     * @param level The level the messaged should be logged with.
     * @param message The log message.
     * @see Level
     */
    void log(Level level, String message);

    /**
     * Log a message with a specified level and formats it using a formatter.
     * @param level The level the messaged should be logged with.
     * @param message The log message, as a valid string format.
     * @param args The arguments to be passed to the formatter.
     * @see Level
     * @see String#format(String, Object...)
     * @see Formatter
     */
    void logF(Level level, String message, Object... args);

    /**
     * Log an exception.
     * @param e The thrown exception.
     * @param message The log message.
     * @return <code>False</code> if the exception was already logged, otherwise <code>true</code>.
     */
    boolean log(Exception e, String message);

    /**
     * Log an exception and format the message using a formatter.
     * @param e The thrown exception.
     * @param message The log message, as a valid string format.
     * @param args The arguments to be passed to the formatter.
     * @return <code>False</code> if the exception was already logged, otherwise <code>true</code>.
     * @see String#format(String, Object...)
     * @see Formatter
     */
    boolean logF(Exception e, String message, Object... args);

    /**
     * Log an exception with the specified level.
     * @param level The level the messaged should be logged with.
     * @param e The thrown exception.
     * @param message The log message.
     * @return <code>False</code> if the exception was already logged, otherwise <code>true</code>.
     */
    boolean log(Level level, Exception e, String message);

    /**
     * Log an exception with the specified level and format the message using a formatter.
     * @param level The level the messaged should be logged with.
     * @param e The thrown exception.
     * @param message The log message, as a valid string format.
     * @param args The arguments to be passed to the formatter.
     * @return <code>False</code> if the exception was already logged, otherwise <code>true</code>.
     * @see String#format(String, Object...)
     * @see Formatter
     */
    boolean logF(Level level, Exception e, String message, Object... args);

    /**
     * Processes an exception, sends it to the logger, and finally returns an {@link ServerListPlusException}.
     * @param e The thrown exception.
     * @param message The log message.
     * @return The exception to throw with the specified message and cause.
     */
    ServerListPlusException process(Exception e, String message);

    /**
     * Processes an exception, sends it to the logger, formats the message using a formatter and finally returns
     * an {@link ServerListPlusException}.
     * @param e The thrown exception.
     * @param message The log message, as a valid string format.
     * @param args The arguments to be passed to the formatter.
     * @return The exception to throw with the specified message and cause.
     * @see String#format(String, Object...)
     * @see Formatter
     */
    ServerListPlusException processF(Exception e, String message, Object... args);

    /**
     * Processes an exception, sends it to the logger with the specified level, and finally returns
     * an {@link ServerListPlusException}.
     * @param level The level the messaged should be logged with.
     * @param e The thrown exception.
     * @param message The log message.
     * @return The exception to throw with the specified message and cause.
     */
    ServerListPlusException process(Level level, Exception e, String message);

    /**
     * Processes an exception, sends it to the logger with the specified level, formats the message using a formatter
     * and finally returns an {@link ServerListPlusException}.
     * @param level The level the messaged should be logged with.
     * @param e The thrown exception.
     * @param message The log message, as a valid string format.
     * @param args The arguments to be passed to the formatter.
     * @return The exception to throw with the specified message and cause.
     * @see String#format(String, Object...)
     * @see Formatter
     */
    ServerListPlusException processF(Level level, Exception e, String message, Object... args);
}
