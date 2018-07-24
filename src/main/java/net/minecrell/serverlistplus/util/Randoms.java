package net.minecrell.serverlistplus.util;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

public final class Randoms {

    private Randoms() {
    }

    public static ThreadLocalRandom get() {
        return ThreadLocalRandom.current();
    }

    @Nullable
    public static <T> T next(T[] array) {
        int len = array.length;
        if (len == 0) {
            return null;
        }

        return array[get().nextInt(len)];
    }

}
