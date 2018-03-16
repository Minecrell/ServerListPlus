package net.minecrell.serverlistplus.canary;

import net.canarymod.Canary;
import net.canarymod.bansystem.Ban;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;

import java.util.Date;

public class CanaryBanProvider implements BanProvider {

    private String getUUID(PlayerIdentity playerIdentity) {
        return playerIdentity.getUuid().toString();
    }

    private Ban getBan(PlayerIdentity playerIdentity) {
        return Canary.bans().getBan(getUUID(playerIdentity));
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return Canary.bans().isBanned(getUUID(playerIdentity));
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getReason();
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getBanningPlayer();
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        final long expiration = ban.getExpiration();

        return (expiration < 0L) ? null : new Date(expiration);
    }

}
