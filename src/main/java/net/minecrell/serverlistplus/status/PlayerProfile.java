/*
 * ServerListPlus
 * Copyright (C) 2017 Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.status;

import com.google.common.base.MoreObjects;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

public final class PlayerProfile {

    private final UUID uniqueId;
    private final String name;

    public PlayerProfile(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlayerProfile that = (PlayerProfile) o;
        return uniqueId.equals(that.uniqueId) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId, name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uniqueId", uniqueId)
                .add("name", name)
                .toString();
    }

}
