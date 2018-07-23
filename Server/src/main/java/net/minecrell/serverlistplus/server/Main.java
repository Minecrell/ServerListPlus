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
