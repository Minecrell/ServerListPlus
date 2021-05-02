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

package net.minecrell.serverlistplus.core.player;

import static net.minecrell.serverlistplus.core.logging.Logger.Level.DEBUG;
import static net.minecrell.serverlistplus.core.logging.Logger.Level.INFO;
import static net.minecrell.serverlistplus.core.util.Helper.JSON;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.config.io.IOHelper;
import net.minecrell.serverlistplus.core.plugin.ScheduledTask;
import net.minecrell.serverlistplus.core.util.TimeUnitValue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class JSONIdentificationStorage extends AbstractIdentificationStorage {
    public static class Conf extends PluginConf.PlayerTrackingConf.StorageConf {
        public TimeUnitValue SaveDelay = new TimeUnitValue(TimeUnit.MINUTES, 5);
    }

    public static final String STORAGE_FILE = "PlayerCache.json";
    public static final Type STORAGE_TYPE = new TypeToken<Map<InetAddress, PlayerIdentity>>(){}.getType();

    private Map<InetAddress, PlayerIdentity> storage = new MapMaker().makeMap();

    private final AtomicBoolean changed = new AtomicBoolean();
    private ScheduledTask saveTask;

    public JSONIdentificationStorage(ServerListPlusCore core) {
        super(core);
    }

    @Override
    public boolean has(InetAddress client) {
        return resolve(client) != null;
    }

    @Override
    public PlayerIdentity resolve(InetAddress client) {
        return storage.get(client);
    }

    @Override
    public void update(InetAddress client, PlayerIdentity identity) {
        storage.put(client, identity);
        changed.set(true);
    }

    public Path getStoragePath() {
        return core.getPlugin().getPluginFolder().resolve(STORAGE_FILE);
    }

    @Override
    public void reload() throws ServerListPlusException {
        // Nothing to do here
    }

    @Override
    public synchronized void enable() throws ServerListPlusException {
        if (isEnabled()) return;
        if (!core.getConf(PluginConf.class).PlayerTracking.Storage.Enabled) return;

        getLogger().log(INFO, "Reloading saved player identities...");
        Path storagePath = getStoragePath();
        getLogger().log(DEBUG, "Storage location: " + storagePath);

        try {
            if (Files.exists(storagePath)) {
                Map<InetAddress, PlayerIdentity> identities;

                try (Reader reader = IOHelper.newBufferedReader(storagePath)) {
                    identities = JSON.fromJson(reader, STORAGE_TYPE);
                }

                if (identities != null) storage.putAll(identities);
            }

            getLogger().log(DEBUG, "Player identities successfully reloaded from file.");
        } catch (JsonSyntaxException e) {
            throw getLogger().process(e, "Unable to parse player storage, have you changed it?");
        } catch (IOException | JsonIOException e) {
            throw getLogger().process(e, "Unable to read player storage, make sure it is accessible by the " +
                    "server");
        } catch (Exception e) {
            throw getLogger().process(e, "Failed to load player storage.");
        }

        changed.set(false);

        TimeUnitValue delay = ((Conf) core.getConf(PluginConf.class).PlayerTracking.Storage).SaveDelay;
        this.saveTask = core.getPlugin().scheduleAsync(new SaveTask(), delay.getValue(), delay.getUnit());
    }

    @Override
    public boolean isEnabled() {
        return saveTask != null;
    }

    public synchronized void save() throws ServerListPlusException {
        if (!isEnabled() || !changed.compareAndSet(true, false)) return;
        getLogger().log(DEBUG, "Saving player identities...");
        Path storagePath = getStoragePath();
        getLogger().log(DEBUG, "Storage location: " + storagePath);

        try {
            if (Files.notExists(storagePath)) {
                // Actually this should have been already created by the configuration manager...
                Files.createDirectories(storagePath.toAbsolutePath().getParent());
            }

            try (BufferedWriter writer = IOHelper.newBufferedWriter(storagePath)) {
                JSON.toJson(storage, STORAGE_TYPE, writer);
            }

            getLogger().log(DEBUG, "Successfully saved profiles to the storage!");
        } catch (IOException | JsonIOException e) {
            throw getLogger().process(e, "Unable to access profile configuration.");
        } catch (Exception e) {
            throw getLogger().process(e, "An internal error occurred while saving the profiles!");
        }
    }

    @Override
    public void disable() throws ServerListPlusException {
        if (saveTask != null) {
            saveTask.cancel();
        }

        save();
        this.saveTask = null;
    }

    // TODO: Java 8
    private class SaveTask implements Runnable {

        @Override
        public void run() {
            save();
        }
    }
}
