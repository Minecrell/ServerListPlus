/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
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

package net.minecrell.serverlistplus.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Randoms {
    private Randoms() {}

    public static Random get() {
        return random();
    }

    private static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    public static <T> Collection<T> shuffle(Collection<? extends T> collection) {
        List<T> result = new ArrayList<>(collection);
        Collections.shuffle(result, random());
        return result;
    }

    public static <T> T nextEntry(T[] array) {
        if (Helper.isNullOrEmpty(array)) return null;
        return array.length > 1 ? array[random().nextInt(array.length)] : array[0];
    }

    public static <T> T nextEntry(List<T> list) {
        if (Helper.isNullOrEmpty(list)) return null;
        return list.size() > 1 ? list.get(random().nextInt(list.size())) : list.get(0);
    }

    public static Integer nextNumber(IntegerRange range) {
        if (range == null) return null;
        return range.isSingle() ? range.from() : random().nextInt(range.from(), range.to());
    }
}
