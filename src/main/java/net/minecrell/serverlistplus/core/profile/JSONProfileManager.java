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

package net.minecrell.serverlistplus.core.profile;

import static net.minecrell.serverlistplus.core.logging.Logger.Level.DEBUG;
import static net.minecrell.serverlistplus.core.logging.Logger.Level.REPORT;
import static net.minecrell.serverlistplus.core.util.Helper.JSON;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecrell.serverlistplus.core.AbstractManager;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.config.io.IOHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JSONProfileManager extends AbstractManager implements ProfileManager {
    private static final String DEFAULT_PROFILE = "ServerListPlus";

    private static final String PROFILE_FILENAME = "Profiles.json";

    // TODO: Implement profiles
    private @Getter boolean enabled;

    public JSONProfileManager(ServerListPlusCore core) {
        super(core);
    }

    public Path getProfilePath() {
        return core.getPlugin().getPluginFolder().resolve(PROFILE_FILENAME);
    }

    @Override
    public void reload() throws ServerListPlusException {
        Path profilePath = this.getProfilePath();
        getLogger().log(DEBUG, "Reloading profiles from: " + profilePath);

        try {
            if (Files.exists(profilePath)) {
                // TODO: Deserialize objects instead
                try (BufferedReader reader = IOHelper.newBufferedReader(profilePath)) {
                    JsonObject obj = JSON.fromJson(reader, JsonObject.class);
                    obj = obj.getAsJsonObject(DEFAULT_PROFILE);
                    this.enabled = obj.get("Enabled").getAsBoolean();
                }

                if (enabled)
                    getLogger().log(REPORT, "ServerListPlus profile is enabled!");
                else
                    getLogger().log(REPORT, "ServerListPlus profile is disabled!");
            } else
                getLogger().log(REPORT, "Profile configuration not found, assuming the profile is disabled!");
        } catch (JsonSyntaxException e) {
            throw getLogger().process(e, "Unable to parse profile configuration, have you changed it?");
        } catch (IOException | JsonIOException e) {
            throw getLogger().process(e, "Unable to access profile configuration.");
        } catch (Exception e) {
            throw getLogger().process(e, "An internal error occurred while reloading the profiles!");
        }
    }

    @Override
    public void save() throws ServerListPlusException {
        Path profilePath = this.getProfilePath();
        getLogger().log(DEBUG, "Saving profiles to: " + profilePath);

        try {
            if (Files.notExists(profilePath)) {
                // Actually this should have been already created by the configuration manager...
                Files.createDirectories(profilePath.getParent());
            }

            // TODO: Serialize objects instead
            try (BufferedWriter writer = IOHelper.newBufferedWriter(profilePath)) {
                JsonObject profile = new JsonObject();
                profile.addProperty("Enabled", this.enabled);
                JsonObject obj = new JsonObject();
                obj.add(DEFAULT_PROFILE, profile);
                JSON.toJson(obj, writer);
            }

            getLogger().log(DEBUG, "Successfully saved profiles to the storage!");
        } catch (IOException | JsonIOException e) {
            throw getLogger().process(e, "Unable to access profile configuration.");
        } catch (Exception e) {
            throw getLogger().process(e, "An internal error occurred while saving the profiles!");
        }
    }

    @Override
    public boolean setEnabled(boolean state) throws ServerListPlusException {
        if (this.enabled != state) {
            this.enabled = state;
            this.save();
            if (enabled) core.reload();
            else core.getPlugin().statusChanged(core.getStatus(), core.getStatus().hasChanges());
            return true;
        }

        return false;
    }
}
