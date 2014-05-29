/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your server list ping!
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

package net.minecrell.serverlistplus.core;

import net.minecrell.serverlistplus.core.config.ServerStatusConf;
import net.minecrell.serverlistplus.core.util.CoreManager;
import net.minecrell.serverlistplus.core.util.Helper;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

public class ServerStatusManager extends CoreManager {
    private static final Pattern PLAYER_PATTERN = Pattern.compile("%player%", Pattern.LITERAL);
    public static final String EMPTY_ID = "0-0-0-0-0";
    public static final UUID EMPTY_UUID = UUID.fromString(EMPTY_ID);

    private static class ServerStatus {
        private final ImmutableList<String> description, playerHover;

        private ServerStatus() {
            this(null, null);
        }

        private ServerStatus(ImmutableList<String> description, ImmutableList<String> playerHover) {
            this.description = description;
            this.playerHover = playerHover;
        }
    }

    private ServerStatus def, personalized;

    public ServerStatusManager(ServerListPlusCore core) {
        super(core);
    }

    public void reload() {
        ServerStatusConf conf = core.getConf().getStorage().get(ServerStatusConf.class);
        if (conf != null) {
            this.def = reload(conf.Default);
            this.personalized = reload(conf.Personalized);
        } else {
            this.def = this.personalized = new ServerStatus();
        }

        core.getPlugin().statusChanged(this);
    }

    private ServerStatus reload(ServerStatusConf.StatusConf conf) {
        if (conf != null) {
            String[] descriptions = null;
            List<String> confDescriptions = conf.Description;
            if (!Helper.nullOrEmpty(confDescriptions)) {
                descriptions = new String[conf.Description.size()];
                for (int i = 0; i < descriptions.length; i++)
                    descriptions[i] = core.getPlugin().colorize(confDescriptions.get(i));
            }

            String[] playerHover = null;
            if (conf.Players != null) {
                List<String> confPlayerHover = conf.Players.Hover;
                if (!Helper.nullOrEmpty(confPlayerHover)) {
                    playerHover = new String[confPlayerHover.size()];
                    for (int i = 0; i < playerHover.length; i++)
                        playerHover[i] = core.getPlugin().colorize(confPlayerHover.get(i));
                }
            }

            return new ServerStatus(descriptions != null ? ImmutableList.copyOf(descriptions) : null,
                    playerHover != null ? ImmutableList.copyOf(playerHover) : null);
        } else return new ServerStatus();
    }

    private static String personalize(String s, String playerName) {
        return PLAYER_PATTERN.matcher(s).replaceAll(playerName);
    }

    public boolean hasChanges() {
        return hasDescription() || hasPlayerHover();
    }

    public boolean hasDescription() {
        return def.description != null || personalized.description != null;
    }

    public String getDescription() {
        return def.description != null ? Helper.nextEntry(ThreadLocalRandom.current(), def.description) : null;
    }

    public String getDescription(String playerName) {
        if (playerName == null) return this.getDescription();
        return personalized.description != null ? personalize(Helper.nextEntry(ThreadLocalRandom.current(),
                personalized.description), playerName) : this.getDescription();
    }

    public boolean hasPlayerHover() {
        return def.playerHover != null || personalized.playerHover != null;
    }

    public String getPlayerHover() {
        return def.playerHover != null ? Helper.nextEntry(ThreadLocalRandom.current(), def.playerHover) : null;
    }

    public String getPlayerHover(String playerName) {
        if (playerName == null) return this.getPlayerHover();
        return personalized.playerHover != null ? personalize(Helper.nextEntry(ThreadLocalRandom.current(),
                personalized.playerHover), playerName) : this.getPlayerHover();
    }
}
