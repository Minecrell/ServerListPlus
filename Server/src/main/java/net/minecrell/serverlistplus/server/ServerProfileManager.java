package net.minecrell.serverlistplus.server;

import net.minecrell.serverlistplus.core.ServerListPlusException;
import net.minecrell.serverlistplus.core.profile.ProfileManager;

public final class ServerProfileManager implements ProfileManager {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean setEnabled(boolean state) throws ServerListPlusException {
        return false;
    }

    @Override
    public void reload() throws ServerListPlusException {
    }

    @Override
    public void save() throws ServerListPlusException {
    }

}
