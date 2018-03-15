package net.minecrell.serverlistplus.bungee.integration;

import me.leoko.advancedban.manager.PunishmentManager;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanDetector;

public class AdvancedBanBanDetector implements BanDetector {

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        final String uuid = playerIdentity.getUuid().toString().replace("-", "");
        
        return PunishmentManager.get().isBanned(uuid);
    }

}
