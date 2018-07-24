package net.minecrell.serverlistplus.util;

import java.util.List;

import javax.annotation.Nullable;

public final class PlusCollections {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final Integer[] EMPTY_INTEGER_ARRAY = new Integer[0];

    private PlusCollections() {
    }

    public static String[] toStringArray(@Nullable List<String> list) {
        return list != null ? list.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY;
    }

    public static Integer[] toIntegerArray(@Nullable List<Integer> list) {
        return list != null ? list.toArray(EMPTY_INTEGER_ARRAY) : EMPTY_INTEGER_ARRAY;
    }

}
