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

import com.google.common.base.Splitter;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.util.ArrayList;

public final class ServerListPlusConsole extends SimpleTerminalConsole {

    private static final Splitter COMMAND_SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings();
    private final ServerListPlusServer server;

    public ServerListPlusConsole(ServerListPlusServer server) {
        this.server = server;
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        return super.buildReader(builder
            .appName("ServerListPlusServer")
            .completer(new ServerListPlusCompleter(this.server)));
    }

    @Override
    protected boolean isRunning() {
        return this.server.isRunning();
    }

    @Override
    protected void runCommand(String command) {
        if (command.charAt(0) == '/') {
            command = command.substring(1);
        }

        this.server.processCommand(new ArrayList<String>(COMMAND_SPLITTER.splitToList(command)));
    }

    @Override
    protected void shutdown() {
        this.server.stop();
    }

}
