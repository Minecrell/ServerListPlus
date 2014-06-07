package net.minecrell.serverlistplus.core;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerListPlusLogger {
    private static final Level DEFAULT_EXCEPTION_LEVEL = Level.SEVERE;
    private static final String PREFIX = "[Core] ";

    private final ServerListPlusCore core;

    public ServerListPlusLogger(ServerListPlusCore core) {
        this.core = core;
    }

    private Logger getLogger() {
        return core.getPlugin().getLogger();
    }

    public String formatMessage(String message, Object... args) {
        return String.format(message, args);
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
}
