package net.minecrell.serverlistplus.api;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ServerListPlusAPI {
    private final Path configPath;
    private List<String> lines = null;

    public ServerListPlusAPI(File configFile) throws IOException {
        this(configFile.toPath());
    }

    public ServerListPlusAPI(Path configPath) throws IOException {
        this.configPath = configPath;
        this.reload();
    }

    public ServerPing handleServerPing(InetAddress address, ServerPing ping) {
        return handleServerPing(address, ping, lines);
    }

    public void reload() throws IOException {
        if (Files.notExists(configPath)) {
            Files.createDirectories(configPath.getParent());
            Files.createFile(configPath);
            lines = new ArrayList<>();
        } else
            // Colorize and replace empty lines using an RESET color
            this.lines = Lists.transform(Files.readAllLines(configPath, StandardCharsets.UTF_8), new Function<String, String>() {
                @Override
                public String apply(String s) {
                    return ChatColor.translateAlternateColorCodes('&', (s.length() > 0) ? s : "&r");
                }
            });
    }

    public List<String> getLines() {
        return lines;
    }

    private static Map<String, String> playerIPs = new HashMap<>();

    public static ServerPing handleServerPing(InetAddress address, ServerPing ping, List<String> lines) {
        if (lines.size() == 0) {
            ping.getPlayers().setSample(null); return ping;
        }

        final String playerIP = address.getHostAddress();

        PlayerInfo[] players = new PlayerInfo[lines.size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = new PlayerInfo(lines.get(i).replace("%player%", (playerIPs.containsKey(playerIP) ? playerIPs.get(playerIP) : "player")), "");
        }

        ping.getPlayers().setSample(players); return ping;
    }

    public static void handlePlayerLogin(String playerName, InetAddress address) {
        playerIPs.put(address.getHostAddress(), playerName);
    }
}
