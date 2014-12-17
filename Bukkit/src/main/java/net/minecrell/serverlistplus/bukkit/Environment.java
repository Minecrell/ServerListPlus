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

package net.minecrell.serverlistplus.bukkit;

import net.minecrell.serverlistplus.core.plugin.ServerType;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public final class Environment {
    private Environment() {}

    private static boolean checkClass(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static final boolean spigot = checkClass("org.spigotmc.SpigotConfig");

    public static boolean isSpigot() {
        return spigot;
    }

    public static ServerType getType() {
        return spigot ? ServerType.SPIGOT : ServerType.BUKKIT;
    }

    public static boolean checkProtocolLib(Server server) {
        Plugin plugin = server.getPluginManager().getPlugin("ProtocolLib");
        return plugin != null &&
                plugin.isEnabled() && plugin.getClass().getName().equals("com.comphenix.protocol.ProtocolLibrary");
    }
}
