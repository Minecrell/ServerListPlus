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

package net.minecrell.serverlistplus.core.config.yaml;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import lombok.Getter;
import net.minecrell.serverlistplus.core.config.help.Descriptions;
import net.minecrell.serverlistplus.core.util.Helper;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.io.Writer;

public class YAMLWriter {
    public static final String COMMENT_PREFIX = "# ";
    public static final String DOCUMENT_START = "--- ";

    protected final SnakeYAML snakeYAML;
    protected final String newLine;

    protected final @Getter String[] header;

    public YAMLWriter(SnakeYAML snakeYAML) {
        this(snakeYAML, null);
    }

    public YAMLWriter(SnakeYAML snakeYAML, String[] header) {
        this.snakeYAML = Preconditions.checkNotNull(snakeYAML, "snakeYAML");
        this.newLine = snakeYAML.getDumperOptions().getLineBreak().getString();
        this.header = header;
    }

    public SnakeYAML snakeYAML() {
        return snakeYAML;
    }

    public void registerAlias(Class<?> clazz, String alias) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(alias), "empty alias");
        Tag tag = new Tag('!' + alias);
        // Add tag to representer and constructor to "notify" them about the alias
        snakeYAML.getRepresenter().addClassTag(clazz, tag);
        snakeYAML.getConstructor().addTypeDescription(new TypeDescription(clazz, tag));
    }

    public void newLine(Appendable writer) throws IOException {
        writer.append(newLine);
    }

    public void writeHeader(Appendable appendable) throws IOException {
        writeComments(appendable, header);
    }

    public void writeDocumented(Writer writer, Object conf) throws IOException {
        // Write configuration description
        writeComments(writer, Descriptions.of(conf));
        writer.append(DOCUMENT_START);
        // Write only one configuration, but don't create a new list for that as SnakeYAML is doing that
        snakeYAML.getYaml().dumpAll(Iterators.singletonIterator(conf), writer);
        writer.append(newLine);
    }

    public void writeComments(Appendable appendable, String... comments) throws IOException {
        if (!Helper.isNullOrEmpty(comments))
            for (String line : comments) { // Write all comment lines
                appendable.append(COMMENT_PREFIX).append(line);
                newLine(appendable);
            }
    }
}
