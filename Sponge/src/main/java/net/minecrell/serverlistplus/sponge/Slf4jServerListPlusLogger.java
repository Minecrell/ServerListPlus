package net.minecrell.serverlistplus.sponge;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.logging.Logger;
import net.minecrell.serverlistplus.core.logging.ServerListPlusLogger;

import java.util.logging.Level;

public class Slf4jServerListPlusLogger extends ServerListPlusLogger {
    private final org.slf4j.Logger logger;

    public Slf4jServerListPlusLogger(ServerListPlusCore core, org.slf4j.Logger logger) {
        super(core);
        this.logger = logger;
    }

    @Override
    public Logger<ServerListPlusException> log(Level level, String message) {
        if (level == ERROR) {
            logger.error(LOG_PREFIX + message);
        } else if (level == WARN) {
            logger.warn(LOG_PREFIX + message);
        } else if (level == INFO) {
            logger.info(LOG_PREFIX + message);
        } else if (level == REPORT) {
            logger.debug(LOG_PREFIX + message);
        } else {
            logger.trace(LOG_PREFIX + message);
        }

        return this;
    }

    @Override
    public Logger<ServerListPlusException> log(Level level, Throwable thrown, String message) {
        if (level == ERROR) {
            logger.error(LOG_PREFIX + message, thrown);
        } else if (level == WARN) {
            logger.warn(LOG_PREFIX + message, thrown);
        } else if (level == INFO) {
            logger.info(LOG_PREFIX + message, thrown);
        } else if (level == REPORT) {
            logger.debug(LOG_PREFIX + message, thrown);
        } else {
            logger.trace(LOG_PREFIX + message, thrown);
        }

        return this;
    }
}
