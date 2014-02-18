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

package net.minecrell.serverlistplus.core;

import lombok.Getter;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.api.ServerPingResponse;
import net.minecrell.serverlistplus.api.plugin.ServerCommandSender;
import net.minecrell.serverlistplus.api.plugin.ServerListPlusPlugin;
import net.minecrell.serverlistplus.core.configuration.util.IOUtil;
import net.minecrell.serverlistplus.core.util.Helper;

public final class DefaultServerListPlusCore implements ServerListPlusCore {
    private final @Getter ServerListPlusPlugin plugin;

    private static final String INFO_COMMAND_FILENAME = "INFO";
    private final String[] infoCommand;

    public DefaultServerListPlusCore(ServerListPlusPlugin plugin) {
        this.plugin = plugin;
        this.infoCommand = this.loadInfoCommandLines();
    }

    @Override
    public String getName() {
        return this.getClass().getPackage().getImplementationTitle();
    }

    @Override
    public String getVersion() {
        return this.getClass().getPackage().getImplementationVersion();
    }

    public String getDisplayVersion() {
        return this.getName() + " v" + this.getVersion();
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public void reload() throws ServerListPlusException {

    }

    @Override
    public void processRequest(InetAddress client, ServerPingResponse response) {

    }

    @Override
    public void processRequest(InetAddress client, String host, ServerPingResponse response) {

    }

    @Override
    public void processLogin(String playerName, InetAddress client) {

    }

    @Override
    public void processCommand(ServerCommandSender sender, String cmd, String label, String[] args) {
        String subCommand = (args.length > 0) ? args[0] : null;
        if (subCommand != null) {
            if (subCommand.equalsIgnoreCase("reload")) {
                this.getLogger().info("Reloading configuration per request by '" + sender + "'!");

                // TODO: Implementation independent colored messages

                try {
                    this.reload();
                    this.sendColoredMessage(sender, "&aConfiguration successfully reloaded!");
                } catch (ServerListPlusException e) {
                    this.sendColoredMessage(sender, "&cAn internal error occurred while reloading the configuration! Sorry!");
                } return;
            }
        }

        for (String line : infoCommand) sender.sendMessage(line);
    }

    private void sendColoredMessage(ServerCommandSender sender, String... messages) {
        for (String message : messages) sender.sendMessage(this.getPlugin().colorizeString(message));
    }

    @Override
    public ServerListPlusException processException(String message, Throwable e) {
        return this.processException(Level.SEVERE, message, e);
    }

    @Override
    public ServerListPlusException processException(Level level, String message, Throwable e) {
        if (e != null && e.getClass() == CoreServerListPlusException.class) return (CoreServerListPlusException) e;
        this.getLogger().log(level, message, e);
        return new CoreServerListPlusException(message, e);
    }

    private String[] loadInfoCommandLines() {
        try (InputStream in = this.getClass().getResourceAsStream(INFO_COMMAND_FILENAME)) {
            return Helper.toStringArray(Helper.colorize(this.getPlugin(), IOUtil.readLines(in)));
        } catch (Throwable e) {
            this.processException(Level.WARNING, "Unable to load info command!", e);
            return new String[0]; // Return empty String array
        }
    }
}
