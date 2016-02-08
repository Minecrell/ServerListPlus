package net.minecrell.serverlistplus.server;

import com.google.common.base.Splitter;
import net.minecrell.serverlistplus.server.logger.ConsoleFormatter;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main {

    private static final String USAGE = "Usage: ServerListPlusServer [IP]<:Port>";
    private static final Splitter HOST_SPLITTER = Splitter.on(':').limit(2).trimResults();

    private Main() {
    }

    public static int create(String[] args) {
        if (args.length == 0 || args.length > 1)
            return printError(USAGE);

        List<String> address = HOST_SPLITTER.splitToList(args[0]);
        if (address.size() != 2)
            return printError(USAGE);

        String host = address.get(0);
        if (host.isEmpty() || host.equals("*")) host = null;
        int port = Integer.parseInt(address.get(1));

        InetSocketAddress socket = host != null ? new InetSocketAddress(host, port) : new InetSocketAddress(port);
        if (socket.getAddress() == null)
            printError("Unknown host: " + host);

        Logger logger = Logger.getLogger(ServerListPlusServer.class.getName());
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new ConsoleFormatter());
        logger.addHandler(handler);

        try {
            new ServerListPlusServer(socket, logger).start();
            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to start server!", e);
            return -1;
        }
    }

    private static int printError(String message) {
        System.err.println(message);
        return -1;
    }

    public static void main(String[] args) {
        System.exit(create(args));
    }

}
