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

package net.minecrell.serverlistplus.bungee.status;

import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.minecrell.serverlistplus.status.handler.StatusHandler;

public class BungeeStatusListener implements Listener {

    private final StatusHandler handler;

    public BungeeStatusListener(StatusHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        handler.handle(new BungeeStatusPing(new BungeeStatusClient(event.getConnection()), event.getResponse()));
    }

}
