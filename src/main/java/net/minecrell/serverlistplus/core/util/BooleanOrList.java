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

package net.minecrell.serverlistplus.core.util;

import com.google.common.collect.ForwardingList;
import net.minecrell.serverlistplus.core.config.yaml.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Just some nice SnakeYAML hacks here to make it possible
// to have a boolean or a list of something
public class BooleanOrList<T> extends ForwardingList<T> implements ConfigurationSerializable {

    private List<T> list;
    private Boolean b;

    public BooleanOrList() {
        this.list = new ArrayList<>();
    }

    public BooleanOrList(boolean b) {
        this.b = b;
    }

    @Override
    protected List<T> delegate() {
        return this.list;
    }

    public List<T> getList() {
        return this.list;
    }

    /**
     * Get the boolean stored inside this list, or {@code null} if not set.
     * (In this case this should be a proper list...)
     *
     * @return The boolean or null
     */
    public Boolean getBoolean() {
        return b;
    }

    public void setBoolean(Boolean b) {
        this.b = b;
        if (b == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = null;
        }
    }

    @Override
    public Object serialize() {
        if (this.b != null) {
            return this.b;
        } else {
            return this.list;
        }
    }

    @SafeVarargs
    public static <T> BooleanOrList<T> of(T... elements) {
        BooleanOrList<T> list = new BooleanOrList<>();
        Collections.addAll(list.list, elements);
        return list;
    }

}
