package net.minecrell.serverlistplus.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.serializer.ComponentSerializers;
import net.minecrell.serverlistplus.core.plugin.ServerCommandSender;

class VelocityCommandSender implements ServerCommandSender {
    private final ProxyServer proxy;
    private final CommandSource source;

    VelocityCommandSender(ProxyServer proxy, CommandSource source) {
        this.proxy = proxy;
        this.source = source;
    }

    @Override
    public String getName() {
        if (source instanceof Player) {
            return ((Player) source).getUsername();
        } else if (source == this.proxy.getConsoleCommandSource()) {
            return "Console";
        } else {
            return "Unknown";
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendMessage(String message) {
        source.sendMessage(ComponentSerializers.LEGACY.deserialize(message));
    }

    @Override
    public void sendMessages(String... messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

}
