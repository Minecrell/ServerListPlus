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

package net.minecrell.serverlistplus.config;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import net.minecrell.serverlistplus.util.NullableByDefault;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

@NullableByDefault
@Description("Changes the appearance of your server in the server list.")
public class StatusProfileConfig {

    @Option(name = "Enabled")
    private Boolean enabled;

    @Option(name = "Description")
    private List<String> description;

    @Option(name = "Players")
    private Players players;

    @Option(name = "Version")
    private Version version;

    @Option(name = "FaviconConfig")
    private List<FaviconConfig> favicon;

    public boolean isEnabled() {
        return enabled == null || enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public Players getPlayers() {
        return players;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public List<FaviconConfig> getFavicon() {
        return favicon;
    }

    public void setFavicon(List<FaviconConfig> favicon) {
        this.favicon = favicon;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .omitNullValues()
                .add("enabled", enabled)
                .add("description", description)
                .add("players", players)
                .add("version", version)
                .add("favicon", favicon)
                .toString();
    }

    public static class Players {

        @Option(name = "Online")
        private List<Object> online;

        @Option(name = "Max")
        private List<Object> max;

        @Option(name = "Hidden")
        private Boolean hidden;

        @Option(name = "Slots")
        private List<String> slots;

        @Option(name = "Hover")
        private List<String> hover;

        public List<Object> getOnline() {
            return online;
        }

        public void setOnline(List<Object> online) {
            this.online = online;
        }

        public List<Object> getMax() {
            return max;
        }

        public void setMax(List<Object> max) {
            this.max = max;
        }

        public Boolean getHidden() {
            return hidden;
        }

        public void setHidden(Boolean hidden) {
            this.hidden = hidden;
        }

        public List<String> getSlots() {
            return slots;
        }

        public void setSlots(List<String> slots) {
            this.slots = slots;
        }

        public List<String> getHover() {
            return hover;
        }

        public void setHover(List<String> hover) {
            this.hover = hover;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .omitNullValues()
                    .add("online", online)
                    .add("max", max)
                    .add("hidden", hidden)
                    .add("slots", slots)
                    .add("hover", hover)
                    .toString();
        }
    }

    public static class Version {

        @Option(name = "Name")
        private List<String> name;

        @Option(name = "Protocol")
        private Integer protocol;

        public List<String> getName() {
            return name;
        }

        public void setName(List<String> name) {
            this.name = name;
        }

        public Integer getProtocol() {
            return protocol;
        }

        public void setProtocol(Integer protocol) {
            this.protocol = protocol;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .omitNullValues()
                    .add("name", name)
                    .add("protocol", protocol)
                    .toString();
        }

    }

    @Nonnull
    public static Map<String, StatusProfileConfig> getDefaults() {
        StatusProfileConfig config = new StatusProfileConfig();

        config.setDescription(Arrays.asList("A Minecraft Server\nNow with 100% more Sponge!", "A Minecraft Server\nI can't think of anything to put"
                + " here!"));

        Players players = new Players();
        config.setPlayers(players);

        players.setOnline(Arrays.asList(100, 200));
        players.setMax(Arrays.asList(300, 700));

        return ImmutableMap.of("Default", config);
    }

}
