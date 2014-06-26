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

package net.minecrell.serverlistplus.bungee.metrics;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMetrics {
    private final static Gson JSON = new Gson();

    private final static int REVISION = 7; // PluginMetrics revision
    private static final String BASE_URL = "http://report.mcstats.org";
    private static final String REPORT_URL = "/plugin/%s";

    private final static int PING_INTERVAL = 15;

    private final Plugin plugin;
    private final String guid;

    private TimerTask task;
    private final Timer timer;

    public BungeeMetrics(Plugin plugin) {
        this.plugin = Preconditions.checkNotNull(plugin, "plugin");
        // Get UUID from BungeeCord configuration
        this.guid = plugin.getProxy().getConfigurationAdapter().getString("stats", UUID.randomUUID().toString());
        this.timer = new Timer(plugin.getDescription().getName() + " Metrics Thread");
    }

    public void start() {
        // Check if UUID is not null --> Plugin statistics disabled
        if (task != null || guid == null || guid.equalsIgnoreCase("null")) return;

        this.task = new TimerTask() {
            private boolean firstPost = true;
            private boolean errorReported = false;

            @Override
            public void run() {
                try {
                    postPlugin(!firstPost);
                    firstPost = false; // Just ping now for the next times
                } catch (Throwable e) {
                    if (!errorReported) {
                        plugin.getLogger().fine("Failed to submit plugin statistics: " + e.getMessage());
                        errorReported = true;
                    }
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, TimeUnit.MINUTES.toMillis(PING_INTERVAL));
    }

    public void stop() {
        if (task != null) {
            timer.cancel();
            task = null;
        }
    }

    private void postPlugin(final boolean isPing) throws IOException {
        // Create data object
        JsonObject jsonData = new JsonObject();

        // Plugin and server information
        jsonData.addProperty("guid", guid);
        jsonData.addProperty("plugin_version", plugin.getDescription().getVersion());
        jsonData.addProperty("server_version", plugin.getProxy().getVersion());

        jsonData.addProperty("auth_mode", plugin.getProxy().getConfigurationAdapter().getBoolean("online_mode",
                true) ? 1 : 0);
        jsonData.addProperty("players_online", plugin.getProxy().getOnlineCount());

        // New data as of R6, system information
        jsonData.addProperty("osname", System.getProperty("os.name"));
        String osArch = System.getProperty("os.arch");
        jsonData.addProperty("osarch", osArch.equals("amd64") ? "x86_64" : osArch);
        jsonData.addProperty("osversion", System.getProperty("os.version"));
        jsonData.addProperty("cores", Runtime.getRuntime().availableProcessors());
        jsonData.addProperty("java_version", System.getProperty("java.version"));

        if (isPing) jsonData.addProperty("ping", 1);

        // Get json output from GSON
        String json = JSON.toJson(jsonData);

        // Open URL connection
        URL url = new URL(BASE_URL + String.format(REPORT_URL, URLEncoder.encode(plugin.getDescription().getName(),
                "UTF-8")));
        URLConnection con = url.openConnection();

        byte[] data = json.getBytes(Charsets.UTF_8);
        byte[] gzip = null;

        try { // Compress using GZIP
            gzip = gzip(data);
        } catch (Exception ignored) {}

        // Add request headers
        con.addRequestProperty("User-Agent", "MCStats/" + REVISION);
        con.addRequestProperty("Content-Type", "application/json");
        if (gzip != null) {
            con.addRequestProperty("Content-Encoding", "gzip");
            data = gzip;
        }

        con.addRequestProperty("Content-Length", Integer.toString(data.length));
        con.addRequestProperty("Accept", "application/json");
        con.addRequestProperty("Connection", "close");

        con.setDoOutput(true);

        // Write json data to the opened stream
        try (OutputStream out = con.getOutputStream()) {
            out.write(data);
            out.flush();
        }

        String response; // Read the response
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            response = reader.readLine();
        }

        // Check for error
        if (response == null || response.startsWith("ERR") || response.startsWith("7")) {
            if (response == null) response = "null";
            else if (response.startsWith("7")) response = response.substring(response.startsWith("7,") ? 2 : 1);
            throw new IOException(response);
        }
    }

    private static byte[] gzip(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(data);
        } return out.toByteArray();
    }
}
