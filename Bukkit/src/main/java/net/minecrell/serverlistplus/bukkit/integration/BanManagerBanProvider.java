package net.minecrell.serverlistplus.bukkit.integration;

import me.confuser.banmanager.BmAPI;
import me.confuser.banmanager.data.PlayerBanData;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;

import java.util.Date;

public class BanManagerBanProvider implements BanProvider {

    private static PlayerBanData getBan(PlayerIdentity playerIdentity) {
        return BmAPI.getCurrentBan(playerIdentity.getUuid());
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return BmAPI.isBanned(playerIdentity.getUuid());
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final PlayerBanData ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getReason();
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final PlayerBanData ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getActor().getName();
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final PlayerBanData ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        final long expires = ban.getExpires();

        return (expires < 0L) ? null : new Date(expires);
    }

}
