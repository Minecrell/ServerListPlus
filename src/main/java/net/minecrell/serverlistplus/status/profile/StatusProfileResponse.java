package net.minecrell.serverlistplus.status.profile;

import net.minecrell.serverlistplus.status.PlayerProfile;
import net.minecrell.serverlistplus.status.StatusPing;
import net.minecrell.serverlistplus.status.StatusRequest;

import java.util.List;

import javax.annotation.Nullable;

public interface StatusProfileResponse {

    StatusRequest getRequest();

    @Nullable
    default String getDescription() {
        return null;
    }

    @Nullable
    default Boolean getHidePlayers() {
        return null;
    }

    @Nullable
    default Integer getOnlinePlayers() {
        return null;
    }

    @Nullable
    default Integer getMaxPlayers() {
        return null;
    }

    @Nullable
    default List<PlayerProfile> getPlayerProfiles() {
        return null;
    }

    @Nullable
    default String getVersion() {
        return null;
    }

    @Nullable
    default Integer getProtocolVersion() {
        return null;
    }

    @Nullable
    default FaviconSource getFavicon() {
        return null;
    }

    default void apply(StatusPing ping) {
        String description = getDescription();
        if (description != null) {
            ping.setDescription(description);
        }

        Boolean hidePlayers = getHidePlayers();
        if (hidePlayers != null) {
            ping.setHidePlayers(hidePlayers);
        }

        if (!ping.isHidePlayers()) {
            Integer players = getOnlinePlayers();
            if (players != null) {
                ping.setOnlinePlayers(players);
            }

            players = getMaxPlayers();
            if (players != null) {
                ping.setMaxPlayers(players);
            }

            List<PlayerProfile> profiles = getPlayerProfiles();
            if (profiles != null) {
                ping.setPlayerProfiles(profiles);
            }
        }

        String version = getVersion();
        if (version != null) {
            ping.setVersion(version);
        }

        Integer protocolVersion = getProtocolVersion();
        if (protocolVersion != null) {
            ping.setProtocolVersion(protocolVersion);
        }

        // TODO: Favicon
    }

}
