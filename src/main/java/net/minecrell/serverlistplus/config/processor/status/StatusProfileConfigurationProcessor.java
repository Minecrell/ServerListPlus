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

package net.minecrell.serverlistplus.config.processor.status;

import net.minecrell.serverlistplus.config.ConfigurationException;
import net.minecrell.serverlistplus.config.loader.ConfigurationLoader;
import net.minecrell.serverlistplus.config.processor.ConfigurationProcessor;
import net.minecrell.serverlistplus.config.status.StatusProfileConfiguration;
import net.minecrell.serverlistplus.status.profile.StatusProfileManager;
import net.minecrell.serverlistplus.util.PlusCollections;

import java.util.Map;

public class StatusProfileConfigurationProcessor implements ConfigurationProcessor {

    private static final String ID = "profiles";

    private final StatusProfileManager manager;

    public StatusProfileConfigurationProcessor(StatusProfileManager manager) {
        this.manager = manager;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void reload(ConfigurationLoader loader) throws ConfigurationException {
        Map<String, StatusProfileConfiguration> profiles = loader.loadMapConfig(ID, String.class, StatusProfileConfiguration.class);

        int priority = 0;
        for (Map.Entry<String, StatusProfileConfiguration> entry : profiles.entrySet()) {
            String id = entry.getKey();
            StatusProfileConfiguration config = entry.getValue();

            ConfigurationStatusProfile.Builder builder = ConfigurationStatusProfile.builder(id, priority);
            builder.setDescriptions(PlusCollections.toStringArray(config.description));

            if (config.players != null) {
                builder.setOnlinePlayers(PlusCollections.toIntegerArray(config.players.online));
                builder.setMaxPlayers(PlusCollections.toIntegerArray(config.players.max));
                builder.setHidePlayers(config.players.hidden);
                builder.setHover(PlusCollections.toStringArray(config.players.hover));
            }

            if (config.version != null) {
                builder.setVersions(PlusCollections.toStringArray(config.version.name));
                builder.setProtocolVersions(PlusCollections.toIntegerArray(config.version.protocol));
            }

            // TODO: Favicons
            manager.registerProfile(builder.build());

            priority += 10;
        }
    }

}
