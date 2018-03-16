package net.minecrell.serverlistplus.bukkit;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanDetector;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;

import java.util.Date;
import java.sql.Timestamp;

public class BukkitBanDetector implements BanDetector {

    private BanEntry getBanEntry(PlayerIdentity playerIdentity) {
        return Bukkit.getServer().getBanList(BanList.Type.NAME).getBanEntry(playerIdentity.getName());
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return Bukkit.getServer().getBanList(BanList.Type.NAME).isBanned(playerIdentity.getName());
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final BanEntry banEntry = getBanEntry(playerIdentity);

        if (banEntry == null)
            return null;

        return banEntry.getReason();
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final BanEntry banEntry = getBanEntry(playerIdentity);

        if (banEntry == null)
            return null;

        return banEntry.getSource();
    }

    @Override
    public Timestamp getBanExpiration(PlayerIdentity playerIdentity) {
        final BanEntry banEntry = getBanEntry(playerIdentity);

        if (banEntry == null)
            return null;

        final Date expiration = banEntry.getExpiration();

        return (expiration == null) ? null : new Timestamp(expiration.getTime());
    }

}
