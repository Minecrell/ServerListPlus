package net.minecrell.serverlistplus.canary;

import net.canarymod.Canary;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanDetector;

public class CanaryBanDetector implements BanDetector {

    public static final CanaryBanDetector instance = new CanaryBanDetector();

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return Canary.bans().isBanned(playerIdentity.getUuid().toString());
    }

}
