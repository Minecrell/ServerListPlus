package net.minecrell.serverlistplus.server.status;

import lombok.Value;

import java.util.UUID;

@Value
public class UserProfile {

    private final String name;
    private final UUID uuid;

}
