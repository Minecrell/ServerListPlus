package net.minecrell.serverlistplus.bukkit.integration;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import org.maxgamer.maxbans.MaxBans;
import org.maxgamer.maxbans.banmanager.Ban;
import org.maxgamer.maxbans.banmanager.TempBan;

import java.util.Date;

public class MaxBansBanProvider implements BanProvider {

    private static Ban getBan(PlayerIdentity playerIdentity) {
        return MaxBans.instance.getBanManager().getBan(playerIdentity.getName());
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return getBan(playerIdentity) != null;
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

        return ban.getBanner();
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final Ban ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        if (!(ban instanceof TempBan))
            return null;

        final long expires = ((TempBan) ban).getExpires();

        return (expires < 0L) ? null : new Date(expires);
    }

}
