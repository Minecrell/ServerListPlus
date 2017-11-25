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

package net.minecrell.serverlistplus.status.handler;

import net.minecrell.serverlistplus.module.Component;
import net.minecrell.serverlistplus.status.StatusPing;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StatusHandlerManager implements StatusHandler, Component {

    private final Logger logger;

    private final List<StatusHandler> registeredHandlers = new ArrayList<>();
    private StatusHandler[] handlers = StatusHandler.EMPTY_ARRAY;

    public StatusHandlerManager(Logger logger) {
        this.logger = logger;
    }

    public List<StatusHandler> getHandlers() {
        return Collections.unmodifiableList(registeredHandlers);
    }

    public void registerHandler(StatusHandler handler) {
        if (this.registeredHandlers.contains(handler)) {
            throw new IllegalArgumentException("Handler is already registered");
        }

        this.registeredHandlers.add(handler);
    }

    public void unregisterHandler(StatusHandler handler) {
        if (!this.registeredHandlers.remove(handler)) {
            throw new IllegalArgumentException("Handler is not registered");
        }
    }

    public void updateHandlers() {
        StatusHandler[] handlers = registeredHandlers.toArray(StatusHandler.EMPTY_ARRAY);
        Arrays.sort(handlers, StatusHandler.COMPARATOR);
        this.handlers = handlers;
    }

    @Override
    public void enable() {
        updateHandlers();
    }

    @Override
    public void handle(StatusPing ping) {
        for (StatusHandler handler : handlers) {
            try {
                handler.handle(ping);
            } catch (Exception e) {
                logger.error("Failed to handle status ping in {}", handler);
            }
        }
    }

}
