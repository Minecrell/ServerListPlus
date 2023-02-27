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

import net.minecraft.network.protocol.status.ServerStatus;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.StatusResponse;
import org.spongepowered.common.ProtocolMinecraftVersion;

import java.util.OptionalInt;

final class MojangStatusProtocolHandler implements StatusProtocolHandler {

    MojangStatusProtocolHandler(Logger logger) {
        ServerStatus status = new ServerStatus();
        ServerStatus.Version version = new ServerStatus.Version("ServerListPlus", 9999);
        status.setVersion(version);
        logger.debug("Platform protocol version: {}", getProtocolVersion(Sponge.platform().minecraftVersion()));
        logger.info("Using implementation-specific code with Mojang mappings. Full functionality is available.");
    }

    private static OptionalInt getProtocolVersion(MinecraftVersion version) {
        if (version instanceof ProtocolMinecraftVersion)
            return OptionalInt.of(((ProtocolMinecraftVersion) version).getProtocol());
        return OptionalInt.empty();
    }

    @Override
    public OptionalInt getProtocolVersion(ClientPingServerEvent event) {
        return getProtocolVersion(event.client().version());
    }

    @Override
    public OptionalInt getProtocolVersion(StatusResponse response) {
        return getProtocolVersion(response.version());
    }

    @Override
    public void setVersion(ClientPingServerEvent.Response ping, net.minecrell.serverlistplus.core.status.StatusResponse response) {
        if (!(ping instanceof ServerStatus))
            return;

        String version = response.getVersion();
        Integer protocol = response.getProtocolVersion();

        if (version != null || protocol != null) {
            ((ServerStatus) ping).setVersion(new ServerStatus.Version(
                    version != null ? version : ping.version().name(),
                    protocol != null ? protocol : getProtocolVersion(ping).orElse(-1)
            ));
        }
    }
}
