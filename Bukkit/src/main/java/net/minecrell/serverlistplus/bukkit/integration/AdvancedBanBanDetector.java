package net.minecrell.serverlistplus.bukkit.integration;

import me.leoko.advancedban.manager.PunishmentManager;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanDetector;

public class AdvancedBanBanDetector implements BanDetector {

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return PunishmentManager.get().isBanned(playerIdentity.getUuid().toString());
    }

}
