package net.minecrell.serverlistplus.status.profile;

import net.minecrell.serverlistplus.module.Component;
import net.minecrell.serverlistplus.status.StatusPing;
import net.minecrell.serverlistplus.status.StatusRequest;
import net.minecrell.serverlistplus.status.handler.StatusHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public final class StatusProfileManager implements Component, StatusHandler {

    private final Map<String, StatusProfile> profiles = new HashMap<>();

    // In reverse order (highest priority comes first)
    private StatusProfile[] enabledProfiles = StatusProfile.EMPTY_ARRAY;

    public Collection<StatusProfile> getProfiles() {
        return Collections.unmodifiableCollection(profiles.values());
    }

    public void registerProfile(StatusProfile profile) {
        String id = profile.getId();
        if (profiles.containsKey(id)) {
            throw new IllegalArgumentException("Profile '" + id + "' is already registered");
        }

        profiles.put(id, profile);
    }

    public boolean unregisterProfile(StatusProfile profile) {
        return profiles.remove(profile.getId(), profile);
    }

    public void updateProfiles() {
        // TODO: Allow disabling profiles
        StatusProfile[] newProfiles = profiles.values().toArray(StatusProfile.EMPTY_ARRAY);
        Arrays.sort(newProfiles, StatusProfile.COMPARATOR);
        this.enabledProfiles = newProfiles;
    }

    @Nullable
    public StatusProfileResponse getResponse(StatusRequest request) {
        StatusProfile[] enabledProfiles = this.enabledProfiles;
        if (enabledProfiles.length == 0) {
            return null;
        }

        List<StatusProfile> activeProfiles = new ArrayList<>(enabledProfiles.length);
        for (StatusProfile profile : enabledProfiles) {
            if (profile.test(request)) {
                activeProfiles.add(profile);
            }
        }

        if (activeProfiles.isEmpty()) {
            return null;
        }

        return new CompositeStatusProfileResponse(request, activeProfiles.toArray(StatusProfile.EMPTY_ARRAY));
    }

    @Override
    public void handle(StatusPing ping) {
        StatusProfileResponse response = getResponse(ping);
        if (response != null) {
            response.apply(ping);
        }
    }

    @Override
    public void enable() {
        updateProfiles();
    }

}