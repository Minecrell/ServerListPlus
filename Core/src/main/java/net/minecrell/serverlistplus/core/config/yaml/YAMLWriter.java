/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your server list ping!
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.config.yaml;

import java.io.IOException;
import net.minecrell.serverlistplus.core.util.Helper;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

public class YAMLWriter {
    public static final String COMMENT_PREFIX = "# ";
    public static final String DOCUMENT_START = "--- ";

    protected final SnakeYAML snakeYAML;
    protected final String newLine;
    protected final Joiner commentWriter;

    public YAMLWriter(SnakeYAML snakeYAML) {
        this.snakeYAML = Preconditions.checkNotNull(snakeYAML, "snakeYAML");
        this.newLine = snakeYAML.getDumperOptions().getLineBreak().getString();
        this.commentWriter = Joiner.on(newLine + COMMENT_PREFIX);
    }

    public SnakeYAML snakeYAML() {
        return snakeYAML;
    }



    private void writeComments(Appendable appendable, String... comments) throws IOException {
        if (!Helper.nullOrEmpty(comments))
            commentWriter.appendTo(appendable, Iterators.forArray(comments)).append(newLine);
    }
}
