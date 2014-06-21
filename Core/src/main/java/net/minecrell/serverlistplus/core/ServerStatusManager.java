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

package net.minecrell.serverlistplus.core;

import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.ServerStatusConf;
import net.minecrell.serverlistplus.core.favicon.DefaultFaviconLoader;
import net.minecrell.serverlistplus.core.favicon.FaviconLoader;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.replacer.DynamicReplacer;
import net.minecrell.serverlistplus.core.replacer.ReplacementManager;
import net.minecrell.serverlistplus.core.util.CoreManager;
import net.minecrell.serverlistplus.core.util.Helper;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class ServerStatusManager extends CoreManager {
    public static final String EMPTY_ID = "0-0-0-0-0";
    public static final UUID EMPTY_UUID = UUID.fromString(EMPTY_ID);

    private static class ServerStatus {
        private final List<String> description, playerHover;
        private final List<Integer> online, max;
        private final List<String> version; private final Integer protocol;
        private final List<FaviconSource> favicon;

        private ServerStatus() {
            this(
                    null, null,
                    null, null,
                    null, null,
                    null
            );
        }

        private ServerStatus(List<String> description, List<String> playerHover,
                             List<Integer> online, List<Integer> max,
                             List<String> version, Integer protocol,
                             List<FaviconSource> favicon) {
            this.description = description; this.playerHover = playerHover;
            this.online = online; this.max = max;
            this.version = version; this.protocol = protocol;
            this.favicon = favicon;
        }

        private boolean hasChanges() {
            return description != null || playerHover != null
                    || online != null || max != null
                    || version != null || protocol != null
                    || favicon != null;
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
            this.def = this.personalized = new ServerStatus();
        }

        core.getPlugin().statusChanged(this);
    }

    private String prepare(String s) {
        s = ReplacementManager.replaceStatic(core, s);
        replacers.putAll(s, ReplacementManager.findDynamic(s));
        return s;
    }

    // TODO: Java 8 would be great here ;)
    private final Function<String, String> prepareFunc = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return prepare(input);
        }
    };

    private ImmutableList<String> readMessages(List<String> messages) {
        if (Helper.nullOrEmpty(messages)) return null;
        return ImmutableList.copyOf(Collections2.transform(messages, prepareFunc));
    }

    private Collection<String> readFavicons(List<String> favicons) {
        if (Helper.nullOrEmpty(favicons)) return null;
        return Collections2.transform(favicons, prepareFunc);
    }

    private Set<String> findFolderFavicons(List<String> folders) {
        if (Helper.nullOrEmpty(folders)) return null;
        final Set<String> favicons = new LinkedHashSet<>();
        boolean recursive = core.getConf(PluginConf.class).RecursiveFolderSearch;
        for (String folderPath : folders) {
            Path folder = core.getPlugin().getPluginFolder().resolve(folderPath);
            if (!Files.isDirectory(folder)) {
                core.getLogger().warning("Invalid favicon folder in configuration: " + folder);
                continue;
            }

            if (recursive)
                try {
                    Files.walkFileTree(folder, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                            new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                    if (file.getFileName().endsWith(".png")) {
                                        favicons.add(file.toString());
                                    }

                                    return FileVisitResult.CONTINUE;
                                }
                            });
                } catch (IOException e) {
                    core.getLogger().warning(e, "Unable to walk through file tree for " + folder);
                }
            else
                try (DirectoryStream<Path> dir = Files.newDirectoryStream(folder, "*.png")) {
                    for (Path file : dir) {
                        favicons.add(file.toString());
                    }
                } catch (IOException e) {
                    core.getLogger().warning(e, "Unable to get directory listing for " + folder);
                }
        }

        return Helper.makeImmutableSet(favicons);
    }

    private void addFavicons(ImmutableList.Builder<FaviconSource> list, Iterable<String> favicons,
                             FaviconLoader loader) {
        if (Helper.nullOrEmpty(favicons)) return;
        for (String favicon : favicons)
            list.add(new FaviconSource(favicon, loader));
    }

    private ServerStatus reload(ServerStatusConf.StatusConf conf) {
        if (conf != null) {
            List<String> descriptions = readMessages(conf.Description), playerHover = null;
            List<Integer> online = null, max = null;
            List<String> version = null; Integer protocol = null;
            List<FaviconSource> favicons = null;

            if (conf.Players != null) {
                playerHover = readMessages(conf.Players.Hover);
                online = Helper.makeImmutableList(conf.Players.Online);
                max = Helper.makeImmutableList(conf.Players.Max);
            }

            if (conf.Version != null) {
                version = readMessages(conf.Version.Name);
                protocol = conf.Version.Protocol;
            }

            if (conf.Favicon != null) {
                ImmutableList.Builder<FaviconSource> builder = ImmutableList.builder();
                addFavicons(builder, readFavicons(conf.Favicon.Files), DefaultFaviconLoader.FILE);
                addFavicons(builder, findFolderFavicons(conf.Favicon.Folders), DefaultFaviconLoader.FILE);
                addFavicons(builder, readFavicons(conf.Favicon.URLs), DefaultFaviconLoader.URL);
                addFavicons(builder, readFavicons(conf.Favicon.Encoded), DefaultFaviconLoader.BASE64);
                favicons = builder.build();
                if (favicons.size() == 0) favicons = null;
            }

            return new ServerStatus(descriptions, playerHover, online, max, version, protocol, favicons);
        } else return new ServerStatus();
    }

    public boolean isEnabled() {
        return core.getProfiles().isEnabled();
    }

    public boolean hasChanges() {
        return isEnabled() && ((def != null && def.hasChanges())
                || (personalized != null && personalized.hasChanges()));
    }

    public boolean hasFavicon() {
        return isEnabled() && ((def != null && def.favicon != null)
                || (personalized != null && personalized.favicon != null));
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
            this.playerName = playerName;
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
                this.online = random(playerName != null && personalized.online != null ? personalized.online :
                        def.online);
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
            return prepareRandom(this, playerName != null && personalized.description != null ?
                    personalized.description : def.description);
        }

        public String getPlayerHover() {
            return prepareRandom(this, playerName != null && personalized.playerHover != null ?
                    personalized.playerHover : def.playerHover);
        }

        public String getVersion() {
            return prepareRandom(this, playerName != null && personalized.version != null ?
                    personalized.version : def.version);
        }

        public Integer getProtocol() {
            return playerName != null && personalized.protocol != null ? personalized.protocol : def.protocol;
        }

        public FaviconSource getFavicon() {
            FaviconSource favicon = random(playerName != null && personalized.favicon != null ?
                    personalized.favicon : def.favicon);
            if (favicon == null) return null;
            Collection<DynamicReplacer> replacer = replacers.get(favicon.getSource());
            if (replacer.size() > 0) return favicon.withSource(ReplacementManager.replaceDynamic(this,
                    favicon.getSource(), replacer));
            return favicon;
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
