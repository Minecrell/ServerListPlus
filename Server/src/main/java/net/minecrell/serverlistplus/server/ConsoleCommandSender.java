package net.minecrell.serverlistplus.server;

import net.minecrell.serverlistplus.core.plugin.ServerCommandSender;
import net.minecrell.serverlistplus.server.util.FormattingCodes;

public final class ConsoleCommandSender implements ServerCommandSender {

    public static final ConsoleCommandSender INSTANCE = new ConsoleCommandSender();

    private ConsoleCommandSender() {
    }

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(FormattingCodes.strip(message));
    }

    @Override
    public void sendMessages(String... messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

}
