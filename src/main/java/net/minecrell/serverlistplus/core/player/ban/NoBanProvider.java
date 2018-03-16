package net.minecrell.serverlistplus.core.player.ban;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;

import java.util.Date;

public class NoBanProvider implements BanProvider {

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        return false;
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        return null;
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        return null;
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        return null;
    }
}
