/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.minecrell.serverlistplus.core.ServerListPlusCore;

import java.util.Date;
import java.util.UUID;

@Data @AllArgsConstructor
public class PlayerIdentity {
    private final UUID uuid;
    private final @NonNull String name;
    private final Date time;

    public boolean isBanned(ServerListPlusCore core) {
        return core.getBanProvider().isBanned(this);
    }

    public static PlayerIdentity create(UUID uuid, String name) {
        return new PlayerIdentity(uuid, name, new Date());
    }

}
