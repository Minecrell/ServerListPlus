package net.minecrell.serverlistplus.core.player.ban;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;

import java.util.Date;

public interface BanProvider {

    /**
     * Checks if a player is banned.
     * 
     * @param playerIdentity Player to check
     * @return <tt>true</tt> if the player is banned, <tt>false</tt> if not.
     */
    boolean isBanned(PlayerIdentity playerIdentity);

    /**
     * Gets the ban reason of a banned player.
     * 
     * @param playerIdentity Player to get the ban reason for
     * @return The ban reason, or <tt>null</tt> if the reason is unknown or the
     *         player is not banned.
     */
    String getBanReason(PlayerIdentity playerIdentity);

    /**
     * Gets the name of the operator that banned the player.
     * 
     * @param playerIdentity Player to get the ban operator for
     * @return The ban operator, or <tt>null</tt> if the operator is unknown or
     *         the player is not banned.
     */
    String getBanOperator(PlayerIdentity playerIdentity);

    /**
     * Gets the expiration date of the banned player.<br>
     * <i>Note: {@link Date} contains time!</i>
     * 
     * @param playerIdentity Player to get the ban expiration date for
     * @return The ban expiration date, or <tt>null</tt> if the ban is permanent
     *         or the player is not banned.
     */
    Date getBanExpiration(PlayerIdentity playerIdentity);

}
