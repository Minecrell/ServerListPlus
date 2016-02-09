package net.minecrell.serverlistplus.server.util;

import java.util.regex.Pattern;

public final class FormattingCodes {

    private FormattingCodes() {
    }

    private static final Pattern FORMATTING_CODES = Pattern.compile("§[0-9A-FK-OR]", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONFIG_CODES = Pattern.compile("&([0-9A-FK-OR])", Pattern.CASE_INSENSITIVE);

    public static String strip(String s) {
        return FORMATTING_CODES.matcher(s).replaceAll("");
    }

    public static String colorize(String s) {
        return CONFIG_CODES.matcher(s).replaceAll("§$1");
    }

}
