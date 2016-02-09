package net.minecrell.serverlistplus.server.status;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public final class StatusPingResponse {

    @Data
    @AllArgsConstructor
    public static final class Version {

        private String name;
        private int protocol;

    }

    @Data
    @AllArgsConstructor
    public static final class Players {

        private int online;
        private int max;

        private UserProfile[] sample;

    }

    private String description = "";
    private final Version version = new Version("ServerListPlus", 0);
    private Players players = null;
    private String favicon;

}
