package net.minecrell.serverlistplus.config.processor.status;

import net.minecrell.serverlistplus.status.PlayerProfile;
import net.minecrell.serverlistplus.status.StatusRequest;
import net.minecrell.serverlistplus.status.profile.StatusProfile;
import net.minecrell.serverlistplus.util.PlusCollections;
import net.minecrell.serverlistplus.util.Randoms;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

class ConfigurationStatusProfile implements StatusProfile {

    private final String id;
    private final int priority;

    private final String[] descriptions;
    private final Integer[] onlinePlayers;
    private final Integer[] maxPlayers;
    @Nullable private final Boolean hidePlayers;
    private final String[] hover;
    private final String[] versions;
    private final Integer[] protocolVersions;

    private ConfigurationStatusProfile(String id, int priority, String[] descriptions,
            Integer[] onlinePlayers, Integer[] maxPlayers, @Nullable Boolean hidePlayers,
            String[] hover, String[] versions, Integer[] protocolVersions) {
        this.id = id;
        this.priority = priority;
        this.descriptions = descriptions;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.hidePlayers = hidePlayers;
        this.hover = hover;
        this.versions = versions;
        this.protocolVersions = protocolVersions;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Nullable
    @Override
    public String getDescription(StatusRequest request) {
        return Randoms.next(this.descriptions);
    }

    @Nullable
    @Override
    public Boolean getHidePlayers(StatusRequest request) {
        return this.hidePlayers;
    }

    @Nullable
    @Override
    public Integer getOnlinePlayers(StatusRequest request) {
        return Randoms.next(this.onlinePlayers);
    }

    @Nullable
    @Override
    public Integer getMaxPlayers(StatusRequest request) {
        return Randoms.next(this.maxPlayers);
    }

    @Nullable
    @Override
    public List<PlayerProfile> getPlayerProfiles(StatusRequest request) {
        String hover = Randoms.next(this.hover);
        if (hover != null) {
            // TODO: Support multiple
            return Collections.singletonList(new PlayerProfile(hover));
        } else {
            return null;
        }

    }

    @Nullable
    @Override
    public String getVersion(StatusRequest request) {
        return Randoms.next(this.versions);
    }

    @Nullable
    @Override
    public Integer getProtocolVersion(StatusRequest request) {
        return Randoms.next(this.protocolVersions);
    }

    static Builder builder(String id, int priority) {
        return new Builder(id, priority);
    }

    static class Builder {

        private final String id;
        private final int priority;

        private String[] descriptions = PlusCollections.EMPTY_STRING_ARRAY;
        private Integer[] onlinePlayers = PlusCollections.EMPTY_INTEGER_ARRAY;
        private Integer[] maxPlayers = PlusCollections.EMPTY_INTEGER_ARRAY;
        @Nullable private Boolean hidePlayers;
        private String[] hover = PlusCollections.EMPTY_STRING_ARRAY;
        private String[] versions = PlusCollections.EMPTY_STRING_ARRAY;
        private Integer[] protocolVersions = PlusCollections.EMPTY_INTEGER_ARRAY;

        Builder(String id, int priority) {
            this.id = id;
            this.priority = priority;
        }

        void setDescriptions(String[] descriptions) {
            this.descriptions = descriptions;
        }

        void setOnlinePlayers(Integer[] onlinePlayers) {
            this.onlinePlayers = onlinePlayers;
        }

        void setMaxPlayers(Integer[] maxPlayers) {
            this.maxPlayers = maxPlayers;
        }

        void setHidePlayers(@Nullable Boolean hidePlayers) {
            this.hidePlayers = hidePlayers;
        }

        void setHover(String[] hover) {
            this.hover = hover;
        }

        void setVersions(String[] versions) {
            this.versions = versions;
        }

        void setProtocolVersions(Integer[] protocolVersions) {
            this.protocolVersions = protocolVersions;
        }

        StatusProfile build() {
            return new ConfigurationStatusProfile(id, priority, descriptions,
                    onlinePlayers, maxPlayers, hidePlayers, hover,
                    versions, protocolVersions);
        }

    }

}
