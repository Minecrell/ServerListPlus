package net.minecrell.serverlistplus.core.profile;

import net.minecrell.serverlistplus.core.ServerListPlusException;

public interface ProfileManager {

    boolean isEnabled();
    void setEnabled(boolean state) throws ServerListPlusException;

    void reload() throws ServerListPlusException;
    void save() throws ServerListPlusException;

}
