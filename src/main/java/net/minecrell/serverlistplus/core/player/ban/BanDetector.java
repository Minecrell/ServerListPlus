package net.minecrell.serverlistplus.core.player.ban;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;

public interface BanDetector {

    boolean isBanned(PlayerIdentity playerIdentity);
}
