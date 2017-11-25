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

package net.minecrell.serverlistplus.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.minecrell.serverlistplus.ServerListPlus;
import net.minecrell.serverlistplus.bungee.status.BungeeStatusListener;
import net.minecrell.serverlistplus.config.loader.yaml.YamlConfigurationLoader;
import net.minecrell.serverlistplus.Platform;
import net.minecrell.serverlistplus.status.handler.StatusHandlerManager;
import org.slf4j.LoggerFactory;

public final class BungeePlugin extends Plugin {

    private ServerListPlus core;

    @Override
    public void onLoad() {
        this.core = new ServerListPlus(Platform.BUNGEE, LoggerFactory.getLogger(getLogger().getName()),
                new YamlConfigurationLoader(getDataFolder().toPath()));
    }

    @Override
    public void onEnable() {
        core.initialize();
        core.enable();

        getProxy().getPluginManager().registerListener(this,
                new BungeeStatusListener(core.getComponent(StatusHandlerManager.class)));
    }

    @Override
    public void onDisable() {
        core.disable();
    }

}
