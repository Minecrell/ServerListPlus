package net.minecrell.serverlistplus.server;

import net.minecrell.serverlistplus.server.logger.ConsoleFormatter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main {

    private Main() {
    }

    public static int create(String[] args) {
        final ConsoleFormatter formatter = new ConsoleFormatter();
        for (Handler handler : Logger.getLogger("").getHandlers()) {
            handler.setFormatter(formatter);
        }

        Logger logger = Logger.getLogger(ServerListPlusServer.class.getName());
        try {
            ServerListPlusServer server = new ServerListPlusServer(logger);
            if (!server.start()) {
                return -1;
            }

            readCommands(server);
            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to start server!", e);
            return -1;
        }
    }

    private static void readCommands(ServerListPlusServer server) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    if (server.processCommand(line)) {
                        return;
                    }
                }
            }
        }

        server.join();
    }

    public static void main(String[] args) {
        System.exit(create(args));
    }

}
