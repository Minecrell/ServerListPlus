/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your complete server status ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.bungee.replacer;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerStatusManager;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.replacer.AbstractPlaceholder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class ServerOnlinePlaceholder extends AbstractPlaceholder {
    private final ProxyServer proxy;

    public ServerOnlinePlaceholder(ProxyServer proxy) {
        super(Pattern.compile("%online@(.+)%"));
        this.proxy = proxy;
    }

    @Override
    public String replace(ServerStatusManager.Response response, String s) {
        return this.replace(response.getCore(), s);
    }

    @Override
    public String replace(ServerListPlusCore core, String s) {
        Matcher matcher = this.matcher(s);
        if (!matcher.find()) return s;

        StringBuffer result = new StringBuffer(); String unknown = null;

        do {
            ServerInfo info = proxy.getServerInfo(matcher.group(1));
            matcher.appendReplacement(result, info != null ? Integer.toString(info.getPlayers().size()) :
                    (unknown != null ? unknown : (unknown = core.getConf(PluginConf.class).Unknown.PlayerCount)));
        } while (matcher.find());

        matcher.appendTail(result);
        return result.toString();
    }
}
