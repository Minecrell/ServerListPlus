/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
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

package net.minecrell.serverlistplus.core.player;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.config.CoreConf;
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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import static net.minecrell.serverlistplus.core.logging.Logger.DEBUG;
import static net.minecrell.serverlistplus.core.logging.Logger.ERROR;
import static net.minecrell.serverlistplus.core.logging.Logger.INFO;
import static net.minecrell.serverlistplus.core.util.Helper.JSON;

public class JSONIdentificationStorage extends AbstractIdentificationStorage {
    public static class Conf extends PluginConf.PlayerTrackingConf.StorageConf {
        public TimeUnitValue SaveDelay = new TimeUnitValue(TimeUnit.MINUTES, 5);
    }

    public static final String STORAGE_FILE = "PlayerCache.json";
    public static final Type STORAGE_TYPE = new TypeToken<Map<InetAddress, PlayerIdentity>>(){}.getType();

    private Cache<InetAddress, PlayerIdentity> cache;
    private String cacheConf;

    private ScheduledTask saveTask;

    public JSONIdentificationStorage(ServerListPlusCore core) {
        super(core);
    }

    @Override
    public Cache<InetAddress, PlayerIdentity> getCache() {
        return cache;
    }

    public Path getStoragePath() {
        return core.getPlugin().getPluginFolder().resolve(STORAGE_FILE);
    }

    @Override
    public void reload() throws ServerListPlusException {
        reload(true);
    }

    private synchronized void reload(boolean copy) throws ServerListPlusException {
        if (copy && !isEnabled()) return; // Don't copy when not enabled

        CoreConf conf = core.getConf(CoreConf.class);
        // Chech if the cache configuration has been changed
        if (cacheConf == null || cache == null || !cacheConf.equals(conf.Caches.JSONStorage)) {
            getLogger().log(DEBUG, "Creating new player tracking cache...");

            Cache<InetAddress, PlayerIdentity> cache;
            String cacheConf;
            try {
                cacheConf = conf.Caches.JSONStorage;
                cache = CacheBuilder.from(cacheConf).build();
            } catch (IllegalArgumentException e) {
                getLogger().log(ERROR, e, "Unable to create player tracking cache using configuration settings");
                cacheConf = core.getDefaultConf(CoreConf.class).Caches.JSONStorage;
                cache = CacheBuilder.from(cacheConf).build();
            }

            if (this.cache != null) {
                getLogger().log(DEBUG, "Deleting old player tracking cache due to configuration changes.");

                if (copy) {
                    getLogger().log(DEBUG, "Copying old entries to the new player tracking cache.");
                    cache.putAll(this.cache.asMap());
                    this.cache.invalidateAll();
                    this.cache.cleanUp();
                }
            }

            this.cache = cache;
            this.cacheConf = cacheConf;
        }
    }

    @Override
    public synchronized void enable() throws ServerListPlusException {
        if (isEnabled()) return;
        this.reload(false);

        getLogger().log(INFO, "Reloading saved player identities...");
        Path storagePath = getStoragePath();
        getLogger().log(DEBUG, "Storage location: " + storagePath);

        try {
            if (Files.exists(storagePath)) {
                Map<InetAddress, PlayerIdentity> identities;

                try (Reader reader = IOHelper.newBufferedReader(storagePath)) {
                    identities = JSON.fromJson(reader, STORAGE_TYPE);
                }

                if (identities != null) cache.putAll(identities);
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

        TimeUnitValue delay = ((Conf) core.getConf(PluginConf.class).PlayerTracking.Storage).SaveDelay;
        this.saveTask = core.getPlugin().scheduleAsync(new SaveTask(), delay.getValue(), delay.getUnit());
    }

    @Override
    public boolean isEnabled() {
        return cache != null;
    }

    public synchronized void save() throws ServerListPlusException {
        if (!isEnabled()) return;
        getLogger().log(INFO, "Saving player identities...");
        Path storagePath = getStoragePath();
        getLogger().log(DEBUG, "Storage location: " + storagePath);

        try {
            if (Files.notExists(storagePath)) {
                // Actually this should have been already created by the configuration manager...
                Files.createDirectories(storagePath.getParent());
            }

            try (BufferedWriter writer = IOHelper.newBufferedWriter(storagePath)) {
                JSON.toJson(cache.asMap(), STORAGE_TYPE, writer);
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
            this.saveTask = null;
        }

        save();
        this.cache = null;
    }

    private class SaveTask implements Runnable {

        @Override
        public void run() {
            save();
        }
    }
}
