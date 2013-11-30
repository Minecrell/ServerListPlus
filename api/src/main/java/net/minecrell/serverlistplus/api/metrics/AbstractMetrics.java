/*
 * ServerListPlus - Customize your server's ping information!
 * Copyright (C) 2013, Minecrell
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
 *
 * Based on Metrics:
 * Copyright 2011-2013 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package net.minecrell.serverlistplus.api.metrics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecrell.serverlistplus.api.metrics.configuration.DefaultMetricsConfigurationProvider;
import net.minecrell.serverlistplus.api.metrics.configuration.MetricsConfiguration;
import net.minecrell.serverlistplus.api.metrics.configuration.MetricsConfigurationProvider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public abstract class AbstractMetrics<T> implements Runnable {
    private final static int REVISION = 7; // Based on Metrics R7
    private final static String VERSION = REVISION + "-CUSTOM";

    private static final String BASE_URL = "http://report.mcstats.org";
    private static final String REPORT_URL = "/plugin/%s";

    private static final int PING_INTERVAL = 15; // In minutes

    private final T plugin;

    public final T getPlugin() {
        return plugin;
    }

    private final MetricsConfigurationProvider configProvider;

    private boolean isOptOut;
    private String guid;
    private boolean debug;

    private TimerTask task;
    private final Timer timer = new Timer();

    protected abstract Logger getLogger();

    protected abstract MetricsPlugin getMetricsPlugin();
    protected abstract MetricsServer getMetricsServer();

    private final Gson gson = new Gson();

    /**
     * All of the custom graphs to submit to metrics
     */
    private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet<Graph>());

    protected AbstractMetrics() throws Exception {
        this(new DefaultMetricsConfigurationProvider());
    }

    protected AbstractMetrics(MetricsConfigurationProvider configProvider) throws Exception {
        this(null, configProvider);
    }

    protected AbstractMetrics(T plugin, MetricsConfigurationProvider configProvider) throws Exception {
        this.plugin = plugin;
        if (configProvider == null) throw new IllegalArgumentException("Configuration provider cannot be null!");

        this.configProvider = configProvider;
        this.reload();
    }

    public final MetricsConfigurationProvider getConfigurationProvider() {
        return configProvider;
    }

    public final void reload() throws Exception {
        MetricsConfiguration config = configProvider.loadConfiguration();
        this.isOptOut = config.isOptOut();
        this.guid = config.getUUID();
        this.debug = config.debugEnabled();
    }

    public final void start() {
        if (task != null) return;

        task = new TimerTask() {
            private boolean firstPost = true;

            @Override
            public void run() {
                try {
                    postPlugin(!firstPost);
                    firstPost = false;
                } catch (Throwable e) {
                    if (debug) getLogger().log(Level.WARNING, "Failed to submit stats to Plugin-Metrics!", e);
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, TimeUnit.MINUTES.toMillis(PING_INTERVAL));
    }

    public final void stop() {
        if (task != null) {
            timer.cancel();
            task = null;
        }
    }

    @Override
    public final void run() {
        if (task != null) {
            task.run();
        }
    }

    public Graph createGraph(final String name) {
        if (name == null) throw new IllegalArgumentException("Graph name cannot be null");
        final Graph graph = new Graph(name);
        graphs.add(graph);
        return graph;
    }

    private void postPlugin(final boolean isPing) throws IOException {
        MetricsPlugin plugin = this.getMetricsPlugin();
        MetricsServer server = this.getMetricsServer();

        JsonObject json = new JsonObject();

        json.addProperty("guid", guid);
        json.addProperty("plugin_version", plugin.getVersion());
        json.addProperty("server_version", server.getVersion());

        json.addProperty("players_online", server.getOnlinePlayers());

        // New data as of R6
        json.addProperty("osname", System.getProperty("os.name"));
        String osArch = System.getProperty("os.arch");
        json.addProperty("osarch", (!osArch.equals("amd64")) ? osArch : "x86_64");
        json.addProperty("osversion", System.getProperty("os.version"));
        json.addProperty("cores", Runtime.getRuntime().availableProcessors());
        json.addProperty("auth_mode", (server.getOnlineMode()) ? 1 : 0);
        json.addProperty("java_version", System.getProperty("java.version"));

        if (isPing) json.addProperty("ping", 1);

        if (graphs.size() > 0) {
            synchronized (graphs) {
                for (Graph graph : graphs) {
                    JsonObject graphJson = new JsonObject();
                    for (Plotter plotter : graph.getPlotters()) {
                        json.addProperty(plotter.getColumnName(), plotter.getValue());
                    }
                    json.add(graph.getName(), graphJson);
                }
            }
        }

        String jsonString = gson.toJson(json);

        URL url = new URL(BASE_URL + String.format(REPORT_URL, URLEncoder.encode(plugin.getName(), "UTF-8")));
        URLConnection con = url.openConnection();

        byte[] data = jsonString.getBytes(StandardCharsets.UTF_8);
        byte[] gzipData = null;

        try {
            gzipData = gzip(jsonString);
        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, "Unable to gzip the statistic data!", e);
        }

        con.addRequestProperty("User-Agent", "MCStats/" + VERSION);
        con.addRequestProperty("Content-Type", "application/json");
        if (gzipData != null) {
            con.addRequestProperty("Content-Encoding", "gzip");
            data = gzipData;
        }

        con.addRequestProperty("Content-Length", Integer.toString(data.length));
        con.addRequestProperty("Accept", "application/json");
        con.addRequestProperty("Connection", "close");

        con.setDoOutput(true);

        if (debug) this.getLogger().info("Request prepared for " + plugin.getName() + "!");

        try (OutputStream out = con.getOutputStream()) {
            out.write(data);
            out.flush();
        }

        String response = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            response = reader.readLine();
        }

        if (response == null || response.startsWith("ERR") || response.startsWith("7")) {
            if (response == null) response = "null";
            else if (response.startsWith("7")) response = response.substring(response.startsWith("7,") ? 2 : 1);

            throw new IOException(response);
        } else {
            // Is this the first update this hour?
            if (response.equals("1") || response.contains("This is your first update this hour")) {
                synchronized (graphs) {
                    for (Graph graph : graphs) {
                        for (Plotter plotter : graph.getPlotters()) {
                            plotter.reset();
                        }
                    }
                }
            }
        }
    }

    private static byte[] gzip(String s) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (GZIPOutputStream gzipOut = new GZIPOutputStream(out)) {
            gzipOut.write(s.getBytes(StandardCharsets.UTF_8));
        }

        return out.toByteArray();
    }
}
