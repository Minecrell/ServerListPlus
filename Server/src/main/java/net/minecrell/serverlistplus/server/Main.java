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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {

    private static final Logger logger = LogManager.getLogger();

    private Main() {
    }

    public static int create(String[] args) {
        try {
            ServerListPlusServer server = new ServerListPlusServer();
            if (!server.start()) {
                return -1;
            }

            new ServerListPlusConsole(server).start();
            server.join();
            return 0;
        } catch (Exception e) {
            logger.error("Unable to start server!", e);
            return -1;
        }
    }

    public static void main(String[] args) {
        System.exit(create(args));
    }

}
