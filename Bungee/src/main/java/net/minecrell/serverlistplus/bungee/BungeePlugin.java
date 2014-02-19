/*
 *       __                           __ _     _     ___ _
 *      / _\ ___ _ ____   _____ _ __ / /(_)___| |_  / _ \ |_   _ ___
 *      \ \ / _ \ '__\ \ / / _ \ '__/ / | / __| __|/ /_)/ | | | / __|
 *      _\ \  __/ |   \ V /  __/ | / /__| \__ \ |_/ ___/| | |_| \__ \
 *      \__/\___|_|    \_/ \___|_| \____/_|___/\__\/    |_|\__,_|___/
 *                       Customize your server ping!
 *
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.bungee;

import java.util.logging.Level;

import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.api.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.api.plugin.ServerType;
import net.minecrell.serverlistplus.bungee.util.AbstractBungeePlugin;
import net.minecrell.serverlistplus.core.DefaultServerListPlusCore;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public final class BungeePlugin extends AbstractBungeePlugin implements ServerListPlusPlugin {
    private ServerListPlusCore core;

    @Override
    public void onEnable() {
        try {
            this.core = new DefaultServerListPlusCore(this);
        } catch (ServerListPlusException e) {
            this.getLogger().info("Please fix the error before restarting the server!"); return;
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "An internal error occurred while initializing the ServerListPlus core!", e); return;
        }

        this.getProxy().getPluginManager().registerCommand(this, new ServerListPlusCommand());
        this.configurationReloaded();
    }

    public final class ServerListPlusCommand extends Command {
        private ServerListPlusCommand() {
            super("ServerListPlus", "ServerListPlus.Admin", "ServerList+", "ServerList", "slp", "sl");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            // TODO: Unable to get the entered comand in BungeeCord
            core.processCommand(new BungeeCommandSender(sender), this.getName(), this.getName(), args);
        }
    }

    @Override
    public void configurationReloaded() {
        if (core == null) return;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUNGEE;
    }

    @Override
    public String colorizeString(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
