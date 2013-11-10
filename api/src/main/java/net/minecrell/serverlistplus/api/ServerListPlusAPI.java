package net.minecrell.serverlistplus.api;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    public ServerPing handleServerPing(ServerPing ping) {
        return handleServerPing(ping, lines);
    }

    public void reload() throws IOException {
        if (Files.notExists(configPath)) {
            Files.createFile(configPath);
            lines = new ArrayList<>();
        } else
            this.lines = colorize(Files.readAllLines(configPath, StandardCharsets.UTF_8));
    }

    public List<String> getLines() {
        return lines;
    }

    public static ServerPing handleServerPing(ServerPing ping, List<String> lines) {
        if (lines.size() == 0) {
            ping.getPlayers().setSample(null); return ping;
        }

        PlayerInfo[] players = new PlayerInfo[lines.size()];
        for (int i = 0; i < players.length; i++)
            players[i] = new PlayerInfo(lines.get(i), "");

        ping.getPlayers().setSample(players); return ping;
    }


    private static String colorize(String message){
        return message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
    }

    private static List<String> colorize(List<String> messages){
        List<String> newList = new ArrayList<>(messages.size());
        for (String msg : messages)
            newList.add(colorize(msg));
        return newList;
    }
}
