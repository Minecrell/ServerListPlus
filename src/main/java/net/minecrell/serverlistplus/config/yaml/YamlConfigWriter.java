/*
 * ServerListPlus
 * Copyright (C) 2016, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.config.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

public final class YamlConfigWriter implements Closeable {

    private static final String COMMENT_PREFIX = "# ";
    private static final char NEW_LINE = '\n';

    private final Yaml yaml;
    private final BufferedWriter writer;

    YamlConfigWriter(Yaml yaml, BufferedWriter writer) {
        this.yaml = Objects.requireNonNull(yaml, "yaml");
        this.writer = Objects.requireNonNull(writer, "writer");
    }

    public void writeNewLine() throws IOException {
        this.writer.write(NEW_LINE);
    }

    public void writeComment(String comment) throws IOException {
        this.writer.write(COMMENT_PREFIX);
        this.writer.write(comment);
        writeNewLine();

    }

    public void writeConfig(Object config) throws IOException {
        this.yaml.dump(config, this.writer);
        //writeNewLine();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }

}
