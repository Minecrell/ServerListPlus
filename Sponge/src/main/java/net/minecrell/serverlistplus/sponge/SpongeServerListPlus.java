/*
 * ServerListPlus
 * Copyright (C) 2016, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.sponge;

import com.google.inject.Inject;
import net.minecrell.serverlistplus.ServerListPlus;
import net.minecrell.serverlistplus.impl.ImplementationType;
import net.minecrell.serverlistplus.impl.ServerListPlusImpl;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;

@Plugin(id = "serverlistplus", name = "ServerListPlus")
public final class SpongeServerListPlus implements ServerListPlusImpl {

    private final Game game;
    private final Logger logger;

    private final ServerListPlus core;

    @Inject
    public SpongeServerListPlus(Game game, Logger logger, @ConfigDir(sharedRoot = false) Path configDir) {
        this.game = game;
        this.logger = logger;

        this.core = new ServerListPlus.Builder(ImplementationType.SPONGE, this, new Slf4jLogger(logger), configDir).build();
    }

    @Override
    public ServerListPlus getCore() {
        return this.core;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        this.core.initialize();
    }

}
