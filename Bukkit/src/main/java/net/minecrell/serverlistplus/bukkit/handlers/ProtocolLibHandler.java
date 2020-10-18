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

package net.minecrell.serverlistplus.bukkit.handlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import net.minecrell.serverlistplus.bukkit.BukkitPlugin;
import net.minecrell.serverlistplus.core.status.ResponseFetcher;
import net.minecrell.serverlistplus.core.status.StatusRequest;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.UUIDs;

import java.net.InetSocketAddress;
import java.util.Collections;

public class ProtocolLibHandler extends StatusHandler {
    private StatusPacketListener listener;

    public ProtocolLibHandler(BukkitPlugin plugin) {
        super(plugin);
    }

    public final class StatusPacketListener extends PacketAdapter {
        public StatusPacketListener() {
            super(PacketAdapter.params(bukkit, PacketType.Status.Server.OUT_SERVER_INFO,
                    PacketType.Handshake.Client.SET_PROTOCOL).optionAsync());
        }

        @Override // Handshake
        public void onPacketReceiving(PacketEvent event) {
            if (event.isCancelled()) {
                return;
            }
            
            if (bukkit.getCore() == null) return; // Too early, we haven't finished initializing yet

            PacketContainer packet = event.getPacket();
            if (packet.getProtocols().read(0) != PacketType.Protocol.STATUS) return;

            StatusRequest request = bukkit.getRequest(event.getPlayer().getAddress());
            request.setProtocolVersion(packet.getIntegers().read(0));

            String host = packet.getStrings().read(0);
            int port = packet.getIntegers().read(1);
            request.setTarget(host, port);
        }

        @Override // Status ping
        public void onPacketSending(PacketEvent event) {
            if (event.isCancelled()) {
                return;
            }
            
            if (bukkit.getCore() == null) return; // Too early, we haven't finished initializing yet

            final WrappedServerPing ping = event.getPacket().getServerPings().read(0);
            // Make sure players have not been hidden when getting the player count
            boolean playersVisible = ping.isPlayersVisible();

            InetSocketAddress client = event.getPlayer().getAddress();
            StatusRequest request = bukkit.getRequest(client);
            bukkit.requestCompleted(event.getPlayer().getAddress());

            StatusResponse response = request.createResponse(bukkit.getCore().getStatus(),
                            // Return unknown player counts if it has been hidden
                            new ResponseFetcher() {
                                @Override
                                public Integer getOnlinePlayers() {
                                    return ping.getPlayersOnline();
                                }

                                @Override
                                public Integer getMaxPlayers() {
                                    return ping.getPlayersMaximum();
                                }

                                @Override
                                public int getProtocolVersion() {
                                    return ping.getVersionProtocol();
                                }
                            });

            // Description is modified in BukkitEventHandler, but we modify it here again,
            // because the BukkitEventHandler has no access to information like virtual hosts.
            String message = response.getDescription();
            if (message != null) ping.setMotD(message);

            // Version name
            message = response.getVersion();
            if (message != null) ping.setVersionName(message);
            // Protocol version
            Integer protocol = response.getProtocolVersion();
            if (protocol != null) ping.setVersionProtocol(protocol);

            if (playersVisible) {
                if (response.hidePlayers()) {
                    ping.setPlayersVisible(false);
                } else {
                    // Online players
                    Integer count = response.getOnlinePlayers();
                    if (count != null) ping.setPlayersOnline(count);

                    // Max players are modified in BukkitEventHandler
                    count = response.getMaxPlayers();
                    if (count != null) ping.setPlayersMaximum(count);

                    // Player hover
                    message = response.getPlayerHover();
                    if (message != null) {
                        if (message.isEmpty()) {
                            ping.setPlayers(Collections.<WrappedGameProfile>emptyList());
                        } else if (response.useMultipleSamples()) {
                            count = response.getDynamicSamples();

                            ping.setPlayers(Iterables.transform(
                                    count != null ? Helper.splitLines(message, count) : Helper.splitLines(message),
                                    new Function<String, WrappedGameProfile>() {
                                @Override
                                public WrappedGameProfile apply(String input) {
                                    return new WrappedGameProfile(UUIDs.EMPTY, input);
                                }
                            }));
                        } else
                            ping.setPlayers(Collections.singleton(
                                    new WrappedGameProfile(UUIDs.EMPTY, message)));
                    }
                }
            }
        }
    }

    @Override
    public boolean register() {
        if (listener == null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(this.listener = new StatusPacketListener());
            return true;
        } else
            return false;
    }

    @Override
    public boolean unregister() {
        if (listener != null) {
            ProtocolLibrary.getProtocolManager().removePacketListener(listener);
            this.listener = null;
            return true;
        } else
            return false;
    }
}
