package net.minecrell.serverlistplus.core.player.ban;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;

public class NoBanDetector implements BanDetector {

    public static final NoBanDetector instance = new NoBanDetector();

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return false;
    }
}
