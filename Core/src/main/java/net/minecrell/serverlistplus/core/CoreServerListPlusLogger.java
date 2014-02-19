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

package net.minecrell.serverlistplus.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.api.ServerListPlusLogger;
import net.minecrell.serverlistplus.core.util.CoreServerListPlusClass;

public class CoreServerListPlusLogger extends CoreServerListPlusClass implements ServerListPlusLogger {
    private static final Level DEFAULT_EXCEPTION_LEVEL = Level.SEVERE;

    public CoreServerListPlusLogger(ServerListPlusCore core) {
        super(core);
    }

    private Logger getLogger() {
        return this.getCore().getPlugin().getLogger();
    }

    @Override
    public String formatMessage(String message, Object... args) {
        return String.format(message, args);
    }

    @Override
    public void info(String message) {
        this.log(Level.INFO, message);
    }

    @Override
    public void infoF(String message, Object... args) {
        this.logF(Level.INFO, message, args);
    }

    @Override
    public void warning(String message) {
        this.log(Level.WARNING, message);
    }

    @Override
    public void warningF(String message, Object... args) {
        this.logF(Level.WARNING, message, args);
    }

    @Override
    public void severe(String message) {
        this.log(Level.SEVERE, message);
    }

    @Override
    public void severeF(String message, Object... args) {
        this.logF(Level.SEVERE, message, args);
    }

    @Override
    public void log(Level level, String message) {
        this.getLogger().log(level, message);
    }

    @Override
    public void logF(Level level, String message, Object... args) {
        this.log(level, this.formatMessage(message, args));
    }

    @Override
    public boolean log(Exception e, String message) {
        return this.log(DEFAULT_EXCEPTION_LEVEL, e, message);
    }

    @Override
    public boolean logF(Exception e, String message, Object... args) {
        return this.logF(DEFAULT_EXCEPTION_LEVEL, e, message, args);
    }

    @Override
    public boolean log(Level level, Exception e, String message) {
        if (!checkException(e)) return false;
        this.getLogger().log(level, message, e); return true;
    }

    @Override
    public boolean logF(Level level, Exception e, String message, Object... args) {
        return this.log(level, e, this.formatMessage(message, args));
    }

    @Override
    public ServerListPlusException process(Exception e, String message) {
        return this.process(DEFAULT_EXCEPTION_LEVEL, e, message);
    }

    @Override
    public ServerListPlusException processF(Exception e, String message, Object... args) {
        return this.processF(DEFAULT_EXCEPTION_LEVEL, e, message, args);
    }

    @Override
    public ServerListPlusException process(Level level, Exception e, String message) {
        return (this.log(level, e, message)) ? new CoreServerListPlusException(message, e) : (ServerListPlusException) e;
    }

    @Override
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
}
