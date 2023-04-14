/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
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

package net.minecrell.serverlistplus.sponge.protocol;

import net.minecrell.serverlistplus.core.status.StatusResponse;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.event.server.ClientPingServerEvent;

import java.util.OptionalInt;

public interface StatusProtocolHandler {

    OptionalInt getProtocolVersion(ClientPingServerEvent event);

    OptionalInt getProtocolVersion(org.spongepowered.api.network.status.StatusResponse response);

    void setVersion(ClientPingServerEvent.Response ping, StatusResponse response);

    static StatusProtocolHandler create(Logger logger) {
        try {
            return new SpongeStatusProtocolHandler(logger);
        } catch (Throwable eSponge) {
            logger.debug("Failed to use new protocol version support from SpongeAPI. Update Sponge?", eSponge);
            try {
                return new MojangStatusProtocolHandler(logger);
            } catch (Throwable eMojang) {
                logger.debug("Failed to load implementation-specific code with Mojang mappings", eMojang);
                try {
                    return new McpStatusProtocolHandler(logger);
                } catch (Throwable eMcp) {
                    logger.debug("Failed to load implementation-specific code with MCP mappings", eMcp);

                    logger.warn("Failed to load implementation-specific code (does it need updating?). " +
                            "Support for custom player slots will be disabled.");
                    return new DummyStatusProtocolHandler();
                }
            }
        }
    }

}
