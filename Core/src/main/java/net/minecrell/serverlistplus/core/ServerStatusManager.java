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
import net.minecrell.serverlistplus.core.replacer.DynamicReplacer;
import net.minecrell.serverlistplus.core.replacer.ReplacementManager;
import net.minecrell.serverlistplus.core.util.CoreManager;
import net.minecrell.serverlistplus.core.util.Helper;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class ServerStatusManager extends CoreManager {
    public static final String EMPTY_ID = "0-0-0-0-0";
    public static final UUID EMPTY_UUID = UUID.fromString(EMPTY_ID);

    private static class ServerStatus {
        private final ImmutableList<String> description, playerHover;
        private final ImmutableList<Integer> online, max;
        private final ImmutableList<String> version;
        private final Integer protocol;

        private ServerStatus(ImmutableList<String> description, ImmutableList<String> playerHover,
                             ImmutableList<Integer> online, ImmutableList<Integer> max, ImmutableList<String> version,
                             Integer protocol) {
            this.description = description;
            this.playerHover = playerHover;
            this.online = online;
            this.max = max;
            this.version = version;
            this.protocol = protocol;
        }
    }

    private ServerStatus def, personalized;
    private Multimap<String, DynamicReplacer> replacers;

    public ServerStatusManager(ServerListPlusCore core) {
        super(core);
    }

    public void reload() {
        if (replacers != null)
            replacers.clear();
        this.replacers = HashMultimap.create();

        ServerStatusConf conf = core.getConf().getStorage().get(ServerStatusConf.class);
        if (conf != null) {
            this.def = reload(conf.Default);
            this.personalized = reload(conf.Personalized);
        } else {
            this.def = this.personalized = null;
        }

        core.getPlugin().statusChanged(this);
    }

    private ImmutableList<String> readMessages(List<String> messages) {
        String[] result = null;
        if (!Helper.nullOrEmpty(messages)) {
            result = new String[messages.size()];
            for (int i = 0; i < result.length; i++)
                result[i] = ReplacementManager.replaceStatic(core, messages.get(i));
            for (String s : result)
                replacers.putAll(s, ReplacementManager.findDynamic(s));
        }

        return Helper.makeImmutable(result);
    }

    private ServerStatus reload(ServerStatusConf.StatusConf conf) {
        if (conf != null) {
            ImmutableList<String> descriptions = readMessages(conf.Description), playerHover = null;
            ImmutableList<Integer> online = null, max = null;
            ImmutableList<String> version = null; Integer protocol = null;

            if (conf.Players != null) {
                playerHover = readMessages(conf.Players.Hover);
                online = Helper.makeImmutable(conf.Players.Online);
                max = Helper.makeImmutable(conf.Players.Max);
            }

            if (conf.Version != null) {
                version = readMessages(conf.Version.Name);
                protocol = conf.Version.Protocol;
            }

            if (descriptions == null && playerHover == null && online == null && max == null && version == null &&
                    protocol == null) return null;
            return new ServerStatus(descriptions, playerHover, online, max, version, protocol);
        } else return null;
    }

    public boolean isEnabled() {
        return core.getProfiles().isEnabled();
    }

    public boolean hasChanges() {
        return isEnabled() && (def != null || personalized != null);
    }

    public Response createResponse(InetAddress client, ResponseFetcher fetcher) {
        return this.createResponse(core.resolveClient(client), fetcher);
    }

    public Response createResponse(String playerName, ResponseFetcher fetcher) {
        return new Response(playerName, fetcher);
    }

    public static class ResponseFetcher {

        public Integer fetchPlayersOnline() {
            return null;
        }

        public Integer fetchMaxPlayers() {
            return null;
        }
    }

    public class Response {
        private final ResponseFetcher fetcher;
        private final String playerName;
        private Integer online, max;

        private Response(String playerName, ResponseFetcher fetcher) {
            this.fetcher = Preconditions.checkNotNull(fetcher, "fetcher");
            this.playerName = personalized != null ? playerName : null;
        }

        public ServerListPlusCore getCore() {
            return core;
        }

        public String getPlayerName() {
            return playerName;
        }

        public Integer fetchPlayersOnline() {
            if (online == null) {
                // First try to get it from the configuration
                this.online = this.getPlayersOnline();
                if (online == null)
                    // Ok, let's get it from the response instead
                    this.online = fetcher.fetchPlayersOnline();
            }

            return online;
        }

        public Integer getPlayersOnline() {
            if (online == null)
                this.online = random(playerName != null && personalized.online != null ? personalized.online : def.online);
            return online;
        }

        public Integer fetchMaxPlayers() {
            if (max == null) {
                // First try to get it from the configuration
                this.max = this.getMaxPlayers();
                if (max == null)
                    // Ok, let's get it from the response instead
                    this.max = fetcher.fetchMaxPlayers();
            }

            return max;
        }

        public Integer getMaxPlayers() {
            if (max == null)
                this.max = random(playerName != null && personalized.max != null ? personalized.max : def.max);
            return max;
        }

        public String getDescription() {
            return prepareRandom(this, playerName != null && personalized.description != null ? personalized.description :
                    def.description);
        }

        public String getPlayerHover() {
            return prepareRandom(this, playerName != null && personalized.playerHover != null ? personalized.playerHover :
                    def.playerHover);
        }

        public String getVersion() {
            return prepareRandom(this, playerName != null && personalized.version != null ? personalized.version :
                    def.version);
        }

        public Integer getProtocol() {
            return playerName != null && personalized.protocol != null ? personalized.protocol : def.protocol;
        }
    }

    private String prepareRandom(Response response, List<String> list) {
        String s = random(list);
        return s != null ? ReplacementManager.replaceDynamic(response, s, replacers.get(s)) : null;
    }

    private static <T> T random(List<T> list) {
        if (list == null) return null;
        return list.size() > 1 ? Helper.nextEntry(ThreadLocalRandom.current(), list) : list.get(0);
    }
}
