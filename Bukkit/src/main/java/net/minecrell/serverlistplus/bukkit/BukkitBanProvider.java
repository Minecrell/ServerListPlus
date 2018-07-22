package net.minecrell.serverlistplus.bukkit;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;

import java.util.Date;

public class BukkitBanProvider implements BanProvider {
    
    private static BanList getBanList() {
        return Bukkit.getServer().getBanList(BanList.Type.NAME);
    }

    private static BanEntry getBanEntry(PlayerIdentity playerIdentity) {
        return getBanList().getBanEntry(playerIdentity.getName());
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return getBanList().isBanned(playerIdentity.getName());
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
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final BanEntry banEntry = getBanEntry(playerIdentity);

        if (banEntry == null)
            return null;

        return banEntry.getExpiration();
    }

}
