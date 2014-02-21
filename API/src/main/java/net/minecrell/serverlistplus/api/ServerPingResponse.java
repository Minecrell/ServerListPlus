/*
 *       __                           __ _     _     ___ _
 *      / _\ ___ _ ____   _____ _ __ / /(_)___| |_  / _ \ |_   _ ___
 *      \ \ / _ \ '__\ \ / / _ \ '__/ / | / __| __|/ /_)/ | | | / __|
 *      _\ \  __/ |   \ V /  __/ | / /__| \__ \ |_/ ___/| | |_| \__ \
 *      \__/\___|_|    \_/ \___|_| \____/_|___/\__\/    |_|\__,_|___/
 *                       Customize your server ping!
 *
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.api;

import java.util.EnumSet;

/**
 * Represents an completely prepared response on a server ping request to a client.
 * It is used to wrap the different APIs to have one class for the modifications.
 * Exact protocol description is available <a href="http://wiki.vg/Server_List_Ping">here</a>.
 *
 * @see <a href="http://wiki.vg/Server_List_Ping">Protocol description</a>
 */
public interface ServerPingResponse {
    public enum Modify {
        ALL,
        DESCRIPTION,
        PLAYERS, PLAYER_HOVER;

        private EnumSet<Modify> children;

        public EnumSet<Modify> getChildren() {
            if (children == null) {
                switch (this) {
                    case ALL: return this.children = EnumSet.allOf(this.getDeclaringClass());
                    case PLAYERS: return this.children = EnumSet.of(PLAYERS, PLAYER_HOVER);
                    default: return this.children = EnumSet.of(this);
                }
            } else return children;
        }
    }


    /**
     * Sets the server list description (MotD) for this response.
     * @param description The server list description.
     */
    void setDescription(String description);

    /**
     * Sets the player hover messages for this response.
     * @param playerHover The player hover messages.
     */
    void setPlayerHover(String[] playerHover);
}
