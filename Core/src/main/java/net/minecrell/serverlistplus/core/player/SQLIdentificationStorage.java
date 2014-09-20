/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
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

package net.minecrell.serverlistplus.core.player;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.config.PluginConf;

import java.net.InetAddress;

import com.google.common.cache.Cache;

// TODO
public class SQLIdentificationStorage extends AbstractIdentificationStorage {
    public static class Conf extends PluginConf.PlayerTrackingConf.StorageConf {
        public String Connection;
    }

    public SQLIdentificationStorage(ServerListPlusCore core) {
        super(core);
    }

    @Override
    public Cache<InetAddress, PlayerIdentity> getCache() {
        return null;
    }

    @Override
    public boolean has(InetAddress client) {
        return false;
    }

    @Override
    public PlayerIdentity resolve(InetAddress client) {
        return null;
    }

    @Override
    public void create(InetAddress client, PlayerIdentity identity) {

    }

    @Override
    public void update(InetAddress client) {

    }

    @Override
    public void reload() throws ServerListPlusException {

    }

    @Override
    public void enable() throws ServerListPlusException {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void disable() throws ServerListPlusException {

    }
}
