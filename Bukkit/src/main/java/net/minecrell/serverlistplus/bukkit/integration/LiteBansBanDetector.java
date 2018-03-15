package net.minecrell.serverlistplus.bukkit.integration;

import litebans.api.Database;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanDetector;

public class LiteBansBanDetector implements BanDetector {

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return Database.get().isPlayerBanned(playerIdentity.getUuid(), null);
    }

}
