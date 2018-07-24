package net.minecrell.serverlistplus.status.profile;

import net.minecrell.serverlistplus.status.PlayerProfile;
import net.minecrell.serverlistplus.status.StatusRequest;

import java.util.List;

import javax.annotation.Nullable;

class CompositeStatusProfileResponse implements StatusProfileResponse {

    private final StatusRequest request;
    private final StatusProfile[] profiles;

    CompositeStatusProfileResponse(StatusRequest request, StatusProfile[] profiles) {
        this.request = request;
        this.profiles = profiles;
    }

    @Override
    public StatusRequest getRequest() {
        return request;
    }

    @Nullable
    @Override
    public String getDescription() {
        for (StatusProfile profile : profiles) {
            String description = profile.getDescription(request);
            if (description != null) {
                return description;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Boolean getHidePlayers() {
        for (StatusProfile profile : profiles) {
            Boolean hidePlayers = profile.getHidePlayers(request);
            if (hidePlayers != null) {
                return hidePlayers;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Integer getOnlinePlayers() {
        for (StatusProfile profile : profiles) {
            Integer onlinePlayers = profile.getOnlinePlayers(request);
            if (onlinePlayers != null) {
                return onlinePlayers;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Integer getMaxPlayers() {
        for (StatusProfile profile : profiles) {
            Integer maxPlayers = profile.getMaxPlayers(request);
            if (maxPlayers != null) {
                return maxPlayers;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public List<PlayerProfile> getPlayerProfiles() {
        for (StatusProfile profile : profiles) {
            List<PlayerProfile> playerProfiles = profile.getPlayerProfiles(request);
            if (playerProfiles != null) {
                return playerProfiles;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String getVersion() {
        for (StatusProfile profile : profiles) {
            String version = profile.getVersion(request);
            if (version != null) {
                return version;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Integer getProtocolVersion() {
        for (StatusProfile profile : profiles) {
            Integer protocolVersion = profile.getProtocolVersion(request);
            if (protocolVersion != null) {
                return protocolVersion;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public FaviconSource getFavicon() {
        for (StatusProfile profile : profiles) {
            FaviconSource favicon = profile.getFavicon(request);
            if (favicon != null) {
                return favicon;
            }
        }

        return null;
    }

}
