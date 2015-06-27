/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.core.status;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import net.minecrell.serverlistplus.core.favicon.FaviconSource;
import net.minecrell.serverlistplus.core.replacement.DynamicReplacer;
import net.minecrell.serverlistplus.core.replacement.ReplacementManager;
import net.minecrell.serverlistplus.core.util.IntegerRange;

import java.util.Collection;
import java.util.List;

import static net.minecrell.serverlistplus.core.util.Randoms.nextEntry;
import static net.minecrell.serverlistplus.core.util.Randoms.nextNumber;

@EqualsAndHashCode @ToString
public class PersonalizedStatusPatch {
    private final StatusPatch def;
    private final @Getter StatusPatch personalized;

    public PersonalizedStatusPatch() {
        this(null, null);
    }

    public PersonalizedStatusPatch(StatusPatch def, StatusPatch personalized) {
        this.def = def != null ? def : StatusPatch.empty();
        this.personalized = personalized != null ? personalized : StatusPatch.empty();
    }

    public StatusPatch getDefault() {
        return def;
    }

    public boolean hasDefault() {
        return def.hasChanges();
    }

    public boolean hasPersonalized() {
        return personalized.hasChanges();
    }

    public boolean hasChanges() {
        return hasDefault() || hasPersonalized();
    }

    // Getters
    public Boolean hidePlayers(StatusResponse response) {
        Boolean result;
        return response.getRequest().isIdentified() && (result = personalized.getHidePlayers()) != null ?
                result : def.getHidePlayers();
    }

    public Integer getOnlinePlayers(StatusResponse response) {
        List<IntegerRange> result;
        return nextNumber(nextEntry(
                response.getRequest().isIdentified() && (result = personalized.getOnline()) != null ?
                        result : def.getOnline()));
    }

    public Integer getMaxPlayers(StatusResponse response) {
        List<IntegerRange> result;
        return nextNumber(nextEntry(
                response.getRequest().isIdentified() && (result = personalized.getMax()) != null ?
                        result : def.getMax()));
    }

    public String getDescription(StatusResponse response) {
        List<String> result;
        return prepareRandomEntry(response,
                response.getRequest().isIdentified() && (result = personalized.getDescriptions()) != null ?
                        result : def.getDescriptions());
    }

    public String getPlayerHover(StatusResponse response) {
        List<String> result;
        return prepareRandomEntry(response,
                response.getRequest().isIdentified() && (result = personalized.getPlayerHovers()) != null ?
                        result : def.getPlayerHovers());
    }

    public String getPlayerSlots(StatusResponse response) {
        List<String> result;
        return prepareRandomEntry(response,
                response.getRequest().isIdentified() && (result = personalized.getSlots()) != null ?
                        result : def.getSlots());
    }

    public String getVersion(StatusResponse response) {
        List<String> result;
        return prepareRandomEntry(response,
                response.getRequest().isIdentified() && (result = personalized.getVersions()) != null ?
                        result : def.getVersions());
    }

    public Integer getProtocolVersion(StatusResponse response) {
        Integer result;
        return response.getRequest().isIdentified() && (result = personalized.getProtocolVersion()) != null ?
                result : def.getProtocolVersion();
    }

    public FaviconSource getFavicon(StatusResponse response) {
        List<FaviconSource> result;
        FaviconSource favicon = nextEntry(
                response.getRequest().isIdentified() && (result = personalized.getFavicons()) != null ?
                        result : def.getFavicons());
        if (favicon == null) return null;
        Collection<DynamicReplacer> replacer = response.getStatus().getReplacers(favicon.getSource());
        if (replacer.size() > 0) return favicon.withSource(ReplacementManager.replaceDynamic(response,
                favicon.getSource(), replacer));
        return favicon;
    }

    private static String prepareRandomEntry(StatusResponse response, List<String> list) {
        return response.getStatus().prepare(response, nextEntry(list));
    }
}
