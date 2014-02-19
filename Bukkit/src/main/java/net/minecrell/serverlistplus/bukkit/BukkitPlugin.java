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

package net.minecrell.serverlistplus.bukkit;

import java.util.logging.Level;

import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.api.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.api.plugin.ServerType;
import net.minecrell.serverlistplus.bukkit.util.AbstractBukkitPlugin;
import net.minecrell.serverlistplus.core.DefaultServerListPlusCore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class BukkitPlugin extends AbstractBukkitPlugin implements ServerListPlusPlugin {
    private ServerListPlusCore core;

    @Override
    public void onEnable() {
        try {
            this.core = new DefaultServerListPlusCore(this);
        } catch (ServerListPlusException e) {
            this.getLogger().info("Please fix the error before restarting the server!");
            this.disablePlugin(); return;
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "An internal error occurred while initializing the ServerListPlus core!", e);
            this.disablePlugin(); return;
        }

        this.getCommand("ServerListPlus").setExecutor(new ServerListPlusCommand());
        this.configurationReloaded();

        this.getLogger().info(this.getDisplayVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info(this.getDisplayVersion() + " disabled.");
    }

    public final class ServerListPlusCommand implements CommandExecutor {
        private ServerListPlusCommand() {}

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            core.processCommand(new BukkitCommandSender(sender), cmd.getName(), label, args); return true;
        }
    }

    @Override
    public void configurationReloaded() {
        if (core == null) return;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUKKIT;
    }

    @Override
    public String colorizeString(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
