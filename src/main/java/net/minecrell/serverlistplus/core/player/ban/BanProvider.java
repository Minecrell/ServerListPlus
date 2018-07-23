/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
