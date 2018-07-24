package net.minecrell.serverlistplus.status.profile;

import net.minecrell.serverlistplus.status.PlayerProfile;
import net.minecrell.serverlistplus.status.StatusRequest;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public interface StatusProfile extends Predicate<StatusRequest> {

    StatusProfile[] EMPTY_ARRAY = new StatusProfile[0];

    Comparator<StatusProfile> COMPARATOR = Comparator.comparingInt(StatusProfile::getPriority);

    String getId();

    default int getPriority() {
        return 0;
    }

    @Override
    default boolean test(StatusRequest request) {
        return true;
    }

    @Nullable
    default String getDescription(StatusRequest request) {
        return null;
    }

    @Nullable
    default Boolean getHidePlayers(StatusRequest request) {
        return null;
    }

    @Nullable
    default Integer getOnlinePlayers(StatusRequest request) {
        return null;
    }

    @Nullable
    default Integer getMaxPlayers(StatusRequest request) {
        return null;
    }

    @Nullable
    default List<PlayerProfile> getPlayerProfiles(StatusRequest request) {
        return null;
    }

    @Nullable
    default String getVersion(StatusRequest request) {
        return null;
    }

    @Nullable
    default Integer getProtocolVersion(StatusRequest request) {
        return null;
    }

    @Nullable
    default FaviconSource getFavicon(StatusRequest request) {
        return null;
    }

}
