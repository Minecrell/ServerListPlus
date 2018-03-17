package net.minecrell.serverlistplus.core.player.ban.integration;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import net.minecrell.serverlistplus.core.util.UUIDs;

import java.util.Date;

public class AdvancedBanBanProvider implements BanProvider {

    private static String getUUID(PlayerIdentity playerIdentity) {
        return UUIDs.NO_DASHES.toString(playerIdentity.getUuid());
    }

    private static Punishment getBan(PlayerIdentity playerIdentity) {
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
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final Punishment punishment = getBan(playerIdentity);

        if (punishment == null)
            return null;

        final long expiration = punishment.getEnd();

        return (expiration < 0L) ? null : new Date(expiration);
    }

}
