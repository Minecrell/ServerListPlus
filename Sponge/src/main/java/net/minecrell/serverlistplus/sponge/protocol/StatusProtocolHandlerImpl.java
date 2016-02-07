/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.sponge.protocol;

import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.statusprotocol.StatusProtocol;
import org.spongepowered.api.event.server.ClientPingServerEvent;

import java.util.OptionalInt;

public final class StatusProtocolHandlerImpl implements StatusProtocolHandler {

    @Override
    public boolean isDummy() {
        return false;
    }

    @Override
    public OptionalInt getProtocolVersion(ClientPingServerEvent event) {
        return StatusProtocol.getProtocolVersion(event.getClient().getVersion());
    }

    @Override
    public OptionalInt getProtocolVersion(org.spongepowered.api.network.status.StatusResponse response) {
        return StatusProtocol.getProtocolVersion(response);
    }

    @Override
    public void setVersion(ClientPingServerEvent.Response ping, StatusResponse response) {
        String version = response.getVersion();
        Integer protocol = response.getProtocolVersion();

        if (version != null || protocol != null) {
            StatusProtocol.setVersion(ping,
                    version != null ? version : ping.getVersion().getName(), protocol != null ? protocol :
                            getProtocolVersion(ping).orElse(-1));
        }
    }

}
