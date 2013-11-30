package net.minecrell.serverlistplus.api.configuration;

import net.minecrell.serverlistplus.api.ServerListPlusAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineProcessor {
    private final ServerListPlusAPI api;

    public LineProcessor(ServerListPlusAPI api) {
        this.api = api;
    }

    private List<String> lines;
    private List<String> unknownPlayer;

    private Map<String, List<String>> forcedHosts = new HashMap<>();

    public List<String> getLines() {
        if (lines != null) return lines;
        return this.lines = processLines(api, api.getConfiguration().getLines());
    }

    public List<String> getUnknownPlayerLines() {
        if (unknownPlayer != null) return unknownPlayer;
        if (api.getConfiguration().getAdvanced() == null) return null;
        return this.unknownPlayer = processLines(api, api.getConfiguration().getAdvanced().getPlayerTracking().getUnknownPlayer().getCustomLines().getLines());
    }

    public List<String> getForcedHost(String forcedHost) {
        if (forcedHosts.containsKey(forcedHost)) return forcedHosts.get(forcedHost);
        if (api.getConfiguration().getAdvanced() == null) return null;
        if (api.getConfiguration().getAdvanced().getForcedHosts().containsKey(forcedHost)) {
            List<String> lines = processLines(api, api.getConfiguration().getAdvanced().getForcedHosts().get(forcedHost));
            forcedHosts.put(forcedHost, lines); return lines;
        } else return null;
    }

    public static List<String> processLines(ServerListPlusAPI api, List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines)
            result.add(api.getServer().colorizeString((line.trim().length() > 0) ? line : "&r"));
        return result;
    }
}
