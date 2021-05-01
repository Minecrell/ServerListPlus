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

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;

public final class ServerListPlusCompleter implements Completer {

    private final ServerListPlusServer server;

    public ServerListPlusCompleter(ServerListPlusServer server) {
        this.server = server;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        List<String> words = line.words();
        List<String> args = new ArrayList<>();
        boolean prefix = false;

        if (!words.isEmpty()) {
            String root = words.get(0);
            if (!root.isEmpty() && root.charAt(0) == '/') {
                root = root.substring(1);
                prefix = line.wordIndex() == 0;
            }
            args.add(root);

            for (int i = 1; i <= line.wordIndex(); ++i) {
                if (i < words.size()) {
                    args.add(words.get(i));
                } else {
                    args.add("");
                }
            }
        }

        for (String comp : server.tabComplete(args)) {
            candidates.add(new Candidate(prefix ? '/' + comp : comp));
        }
    }

}
