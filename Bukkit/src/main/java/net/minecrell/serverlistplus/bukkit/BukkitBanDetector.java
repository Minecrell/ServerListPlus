package net.minecrell.serverlistplus.bukkit;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanDetector;
import org.bukkit.BanList;
import org.bukkit.Bukkit;

public class BukkitBanDetector implements BanDetector {

    public static final BukkitBanDetector instance = new BukkitBanDetector();

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return Bukkit.getServer().getBanList(BanList.Type.NAME).isBanned(playerIdentity.getName());
    }

}
