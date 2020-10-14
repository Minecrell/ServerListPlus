/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
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

package net.minecrell.serverlistplus.core.status;

import static net.minecrell.serverlistplus.core.logging.Logger.WARN;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import net.minecrell.serverlistplus.core.AbstractManager;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.PersonalizedStatusConf;
import net.minecrell.serverlistplus.core.config.ServerStatusConf;
import net.minecrell.serverlistplus.core.favicon.DefaultFaviconLoader;
import net.minecrell.serverlistplus.core.favicon.FaviconLoader;
import net.minecrell.serverlistplus.core.favicon.FaviconSearch;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.replacement.DynamicReplacer;
import net.minecrell.serverlistplus.core.replacement.ReplacementManager;
import net.minecrell.serverlistplus.core.status.hosts.VirtualHost;
import net.minecrell.serverlistplus.core.status.hosts.VirtualHosts;
import net.minecrell.serverlistplus.core.util.BooleanOrList;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.IntegerRange;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StatusManager extends AbstractManager {
    private @Getter PersonalizedStatusPatch patch;
    private @Getter Map<VirtualHost, PersonalizedStatusPatch> hosts;
    private Multimap<String, DynamicReplacer> replacers; // For the used placeholders of the messages.

    private boolean favicons;

    public StatusManager(ServerListPlusCore core) {
        super(core);
    }

    public boolean isEnabled() {
        return core.getProfiles().isEnabled();
    }

    public boolean hasChanges() {
        return isEnabled() && ((patch != null && patch.hasChanges()) || (hosts != null && !hosts.isEmpty()));
    }

    public boolean hasFavicon() {
        return isEnabled() && favicons;
    }

    public void reload() {
        ServerStatusConf conf = core.getConf(ServerStatusConf.class);
        if (conf != null &&
                (conf.Default != null || conf.Personalized != null || !Helper.isNullOrEmpty(conf.Hosts))) {
            Preparation preparation = new Preparation();

            this.patch = preparation.preparePersonalizedPatch(conf);
            this.hosts = preparation.prepareHosts(conf.Hosts);
            this.replacers = preparation.createReplacers();

            favicons = patch.getDefault().getFavicons() != null || patch.getPersonalized().getFavicons() != null;
            if (!favicons)
                for (PersonalizedStatusPatch patch : hosts.values())
                    if (patch.getDefault().getFavicons() != null ||
                            patch.getPersonalized().getFavicons() != null) {
                        favicons = true; break;
                    }
        } else { // Configuration is empty
            this.patch = new PersonalizedStatusPatch();
            this.hosts = ImmutableMap.of();
            this.replacers = ImmutableSetMultimap.of();
            this.favicons = false;
        }

        boolean changes = this.hasChanges();
        if (isEnabled() && !changes) {
            getLogger().log(WARN, "There was no configuration found to apply to the status ping. The plugin will be disabled until you add "
                    + "something to your configuration.");
        }

        core.getPlugin().statusChanged(this, changes);
    }

    protected class Preparation implements Function<String, String> {
        private final Multimap<String, DynamicReplacer> replacers;

        private Preparation() {
            this.replacers = HashMultimap.create();
        }

        public Multimap<String, DynamicReplacer> createReplacers() {
            return ImmutableSetMultimap.copyOf(replacers);
        }

        public PersonalizedStatusPatch preparePersonalizedPatch(PersonalizedStatusConf conf) {
            return new PersonalizedStatusPatch(preparePatch(conf.Default), preparePatch(conf.Personalized),
                    preparePatch(conf.Banned));
        }

        public StatusPatch preparePatch(PersonalizedStatusConf.StatusConf conf) {
            if (conf == null) return StatusPatch.empty();

            // Temporary patch storage
            List<String> descriptions, playerHovers = null;

            List<IntegerRange> online = null, max = null;
            Boolean hidePlayers = null;
            List<String> slots = null;

            List<String> versions = null;
            Integer protocol = null;

            List<FaviconSource> favicons = null;


            descriptions = prepareMessages(conf.Description);

            if (conf.Players != null) {
                hidePlayers = conf.Players.Hidden;
                if (hidePlayers == null || !hidePlayers) {
                    online = prepareRanges(conf.Players.Online);
                    max = prepareRanges(conf.Players.Max);
                    playerHovers = prepareMessages(conf.Players.Hover);
                    slots = prepareMessages(conf.Players.Slots);
                } else if (conf.Players.Online != null || conf.Players.Max != null || conf.Players.Hover != null) {
                    getLogger().log(WARN, "You have hidden the player count in your configuration but still " +
                            "have the maximum online count / hover message configured. They will not work if the" +
                            " player count is hidden.");
                }
            }

            if (conf.Version != null) {
                versions = prepareMessages(conf.Version.Name);
                protocol = conf.Version.Protocol;
            }

            if (conf.Favicon != null) {
                // Improve this somehow
                ImmutableList.Builder<FaviconSource> builder = ImmutableList.builder();

                builder.addAll(prepareFavicons(conf.Favicon.Files, DefaultFaviconLoader.FILE));
                builder.addAll(prepareFaviconSources(FaviconSearch.findInFolder(core, conf.Favicon.Folders),
                        DefaultFaviconLoader.FILE));

                builder.addAll(prepareFavicons(conf.Favicon.URLs, DefaultFaviconLoader.URL));

                builder.addAll(prepareFavicons(conf.Favicon.Heads, DefaultFaviconLoader.SKIN_HEAD));
                builder.addAll(prepareFavicons(conf.Favicon.Helms, DefaultFaviconLoader.SKIN_HELM));

                builder.addAll(prepareFaviconSources(conf.Favicon.Encoded, DefaultFaviconLoader.BASE64));

                // Done
                favicons = builder.build();
                if (favicons.size() == 0) favicons = null;
            }

            return new StatusPatch(descriptions, playerHovers, online, max, hidePlayers, slots, versions, protocol,
                    favicons);
        }

        public Map<VirtualHost, PersonalizedStatusPatch> prepareHosts(Map<String, PersonalizedStatusConf> conf) {
            if (conf == null) return ImmutableMap.of();
            ImmutableMap.Builder<VirtualHost, PersonalizedStatusPatch> builder = ImmutableMap.builder();
            for (Map.Entry<String, PersonalizedStatusConf> entry : conf.entrySet()) {
                PersonalizedStatusPatch patch = preparePersonalizedPatch(entry.getValue());
                if (patch.hasChanges()) {
                    builder.put(VirtualHosts.parse(entry.getKey()), patch);
                }
            }
            return builder.build();
        }

        @Override
        public String apply(String result) {
            result = ReplacementManager.replaceStatic(core, result);
            replacers.putAll(result, ReplacementManager.findDynamic(result));
            return result;
        }

        protected Collection<String> prepareStrings(List<String> strings) {
            return !Helper.isNullOrEmpty(strings) ? Collections2.transform(strings, this) : null;
        }

        protected List<String> prepareMessages(BooleanOrList<String> messages) {
            if (messages == null) return null;
            if (messages.getBoolean() == Boolean.FALSE) return ImmutableList.of("");
            return prepareMessages(messages.getList());
        }

        protected List<String> prepareMessages(List<String> messages) {
            return Helper.makeImmutableList(prepareStrings(messages));
        }

        protected List<IntegerRange> prepareRanges(List<IntegerRange> ranges) {
            return Helper.makeImmutableList(ranges);
        }

        protected Collection<FaviconSource> prepareFavicons(List<String> favicons, final FaviconLoader loader) {
            return prepareFaviconSources(prepareStrings(favicons), loader);
        }

        protected Collection<FaviconSource> prepareFaviconSources(Collection<String> favicons,
                                                                  final FaviconLoader loader) {
            return favicons == null ? ImmutableList.<FaviconSource>of() :
                    Collections2.transform(favicons, new Function<String, FaviconSource>() {
                        @Override
                        public FaviconSource apply(String input) {
                            return new FaviconSource(input, loader);
                        }
                    });
        }
    }

    String prepare(StatusResponse response, String s) {
        return s != null ? ReplacementManager.replaceDynamic(response, s, replacers.get(s)) : null;
    }

    FaviconSource prepare(StatusResponse response, FaviconSource favicon) {
        if (favicon == null) {
            return null;
        }

        Collection<DynamicReplacer> replacer = replacers.get(favicon.getSource());
        if (replacer.size() > 0) {
            return favicon.withSource(ReplacementManager.replaceDynamic(response, favicon.getSource(), replacer));
        } else {
            return favicon;
        }
    }

}
