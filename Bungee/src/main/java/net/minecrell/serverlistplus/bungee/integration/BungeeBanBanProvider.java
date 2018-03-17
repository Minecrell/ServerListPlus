package net.minecrell.serverlistplus.bungee.integration;

import de.lucavinci.bungeeban.BungeeBan;
import de.lucavinci.bungeeban.BungeeBanApi;
import de.lucavinci.bungeeban.util.Ban;
import de.lucavinci.bungeeban.util.BungeeBanPlayer;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;

import java.util.Date;

public class BungeeBanBanProvider implements BanProvider {

    private static BungeeBanApi getAPI() {
        return BungeeBan.getApi();
    }

    private static BungeeBanPlayer getPlayer(PlayerIdentity playerIdentity) {
        return getAPI().getPlayer(playerIdentity.getUuid());
    }

    private static Ban getBan(PlayerIdentity playerIdentity) {
        return getPlayer(playerIdentity).getActiveBan();
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return getPlayer(playerIdentity).isBanned();
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getBanReason();
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getBannedBy();
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        final long expires = ban.getBanEnd();

        return (expires < 0L) ? null : new Date(expires);
    }

}
