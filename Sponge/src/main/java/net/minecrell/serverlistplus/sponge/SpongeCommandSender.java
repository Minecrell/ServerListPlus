package net.minecrell.serverlistplus.sponge;

import net.minecrell.serverlistplus.core.plugin.ServerCommandSender;
import net.minecrell.serverlistplus.core.util.Wrapper;

import org.spongepowered.api.util.command.CommandSource;

public class SpongeCommandSender extends Wrapper<CommandSource> implements ServerCommandSender {
    public SpongeCommandSender(CommandSource handle) {
        super(handle);
    }

    @Override
    public String getName() {
        return "SPONGE"; // fail
    }

    @Override
    public void sendMessage(String message) {
        sendMessages(message);
    }

    @Override
    public void sendMessages(String... messages) {
        getHandle().sendMessage(messages);
    }

    @Override
    public boolean hasPermission(String permission) {
        return handle.hasPermission(permission);
    }
}
