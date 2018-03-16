package net.minecrell.serverlistplus.core.player.ban;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;

import java.sql.Timestamp;

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
    public Timestamp getBanExpiration(PlayerIdentity playerIdentity) {
        return null;
    }
}
