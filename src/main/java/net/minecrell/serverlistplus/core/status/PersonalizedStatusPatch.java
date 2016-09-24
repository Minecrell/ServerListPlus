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

import static net.minecrell.serverlistplus.core.util.Randoms.nextEntry;
import static net.minecrell.serverlistplus.core.util.Randoms.nextNumber;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecrell.serverlistplus.core.favicon.FaviconSource;

import java.util.List;

@EqualsAndHashCode @ToString
public class PersonalizedStatusPatch {
    private final StatusPatch def;
    private final @Getter StatusPatch personalized;
    private final @Getter StatusPatch banned;

    public PersonalizedStatusPatch() {
        this(null, null, null);
    }

    public PersonalizedStatusPatch(StatusPatch def, StatusPatch personalized, StatusPatch banned) {
        this.def = def != null ? def : StatusPatch.empty();
        this.personalized = personalized != null ? personalized : StatusPatch.empty();
        this.banned = banned != null ? banned : StatusPatch.empty();
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
        if (response.getRequest().isIdentified()) {
            if (banned.getHidePlayers() != null && response.getRequest().getIdentity().isBanned(response.getCore())) {
                return banned.getHidePlayers();
            }

            if (personalized.getHidePlayers() != null) {
                return personalized.getHidePlayers();
            }
        }

        return def.getHidePlayers();
    }

    public Integer getOnlinePlayers(StatusResponse response) {
        if (response.getRequest().isIdentified()) {
            if (banned.getOnline() != null && response.getRequest().getIdentity().isBanned(response.getCore())) {
                return nextNumber(nextEntry(banned.getOnline()));
            }

            if (personalized.getOnline() != null) {
                return nextNumber(nextEntry(personalized.getOnline()));
            }
        }

        return nextNumber(nextEntry(def.getOnline()));
    }

    public Integer getMaxPlayers(StatusResponse response) {
        if (response.getRequest().isIdentified()) {
            if (banned.getMax() != null && response.getRequest().getIdentity().isBanned(response.getCore())) {
                return nextNumber(nextEntry(banned.getMax()));
            }

            if (personalized.getMax() != null) {
                return nextNumber(nextEntry(personalized.getMax()));
            }
        }

        return nextNumber(nextEntry(def.getMax()));
    }

    public String getDescription(StatusResponse response) {
        if (response.getRequest().isIdentified()) {
            if (banned.getDescriptions() != null && response.getRequest().getIdentity().isBanned(response.getCore())) {
                return prepareRandomEntry(response, banned.getDescriptions());
            }

            if (personalized.getDescriptions() != null) {
                return prepareRandomEntry(response, personalized.getDescriptions());
            }
        }

        return prepareRandomEntry(response, def.getDescriptions());
    }

    public String getPlayerHover(StatusResponse response) {
        if (response.getRequest().isIdentified()) {
            if (banned.getPlayerHovers() != null && response.getRequest().getIdentity().isBanned(response.getCore())) {
                return prepareRandomEntry(response, banned.getPlayerHovers());
            }

            if (personalized.getPlayerHovers() != null) {
                return prepareRandomEntry(response, personalized.getPlayerHovers());
            }
        }

        return prepareRandomEntry(response, def.getPlayerHovers());
    }

    public String getPlayerSlots(StatusResponse response) {
        if (response.getRequest().isIdentified()) {
            if (banned.getSlots() != null && response.getRequest().getIdentity().isBanned(response.getCore())) {
                return prepareRandomEntry(response, banned.getSlots());
            }

            if (personalized.getSlots() != null) {
                return prepareRandomEntry(response, personalized.getSlots());
            }
        }

        return prepareRandomEntry(response, def.getSlots());
    }

    public String getVersion(StatusResponse response) {
        if (response.getRequest().isIdentified()) {
            if (banned.getVersions() != null && response.getRequest().getIdentity().isBanned(response.getCore())) {
                return prepareRandomEntry(response, banned.getVersions());
            }

            if (personalized.getVersions() != null) {
                return prepareRandomEntry(response, personalized.getVersions());
            }
        }

        return prepareRandomEntry(response, def.getVersions());
    }

    public Integer getProtocolVersion(StatusResponse response) {
        if (response.getRequest().isIdentified()) {
            if (banned.getProtocolVersion() != null && response.getRequest().getIdentity().isBanned(response.getCore())) {
                return banned.getProtocolVersion();
            }

            if (personalized.getProtocolVersion() != null) {
                return personalized.getProtocolVersion();
            }
        }

        return def.getProtocolVersion();
    }

    public FaviconSource getFavicon(StatusResponse response) {
        if (response.getRequest().isIdentified()) {
            if (banned.getFavicons() != null && response.getRequest().getIdentity().isBanned(response.getCore())) {
                return prepareFavicon(response, banned.getFavicons());
            }

            if (personalized.getFavicons() != null) {
                return prepareFavicon(response, personalized.getFavicons());
            }
        }

        return prepareFavicon(response, def.getFavicons());
    }

    private static String prepareRandomEntry(StatusResponse response, List<String> list) {
        return response.getStatus().prepare(response, nextEntry(list));
    }

    private static FaviconSource prepareFavicon(StatusResponse response, List<FaviconSource> favicons) {
        return response.getStatus().prepare(response, nextEntry(favicons));
    }

}
