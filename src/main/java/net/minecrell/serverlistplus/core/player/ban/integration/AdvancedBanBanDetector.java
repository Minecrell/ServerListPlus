package net.minecrell.serverlistplus.core.player.ban.integration;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanDetector;

import java.sql.Timestamp;

public class AdvancedBanBanDetector implements BanDetector {

    private String getUUID(PlayerIdentity playerIdentity) {
        return playerIdentity.getUuid().toString().replace("-", "");
    }

    private Punishment getBan(PlayerIdentity playerIdentity) {
        return PunishmentManager.get().getBan(getUUID(playerIdentity));
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return PunishmentManager.get().isBanned(getUUID(playerIdentity));
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final Punishment punishment = getBan(playerIdentity);

        if (punishment == null)
            return null;

        return punishment.getReason();
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final Punishment punishment = getBan(playerIdentity);

        if (punishment == null)
            return null;

        return punishment.getOperator();
    }

    @Override
    public Timestamp getBanExpiration(PlayerIdentity playerIdentity) {
        final Punishment punishment = getBan(playerIdentity);

        if (punishment == null)
            return null;

        final long expiration = punishment.getEnd();

        return (expiration < 0L) ? null : new Timestamp(expiration);
    }

}
