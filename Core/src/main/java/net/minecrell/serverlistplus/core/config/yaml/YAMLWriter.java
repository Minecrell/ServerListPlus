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

import net.minecrell.serverlistplus.core.config.help.ConfHelper;
import net.minecrell.serverlistplus.core.util.Helper;

import java.io.IOException;
import java.io.Writer;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Tag;

public class YAMLWriter {
    public static final String COMMENT_PREFIX = "# ";
    public static final String DOCUMENT_START = "--- ";

    protected final SnakeYAML snakeYAML;
    protected final String newLine;

    protected final String[] header;

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

    public String[] getHeader() {
        return header;
    }

    public void registerAlias(Class<?> clazz, String alias) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(alias), "empty alias");
        Tag tag = new Tag("!" + alias);
        snakeYAML.getRepresenter().addClassTag(clazz, tag);
        snakeYAML.getConstructor().addTypeDescription(new TypeDescription(clazz, tag));
    }

    public <A extends Appendable> A newLine(A writer) throws IOException {
        writer.append(newLine); return writer;
    }

    public void writeHeader(Appendable appendable) throws IOException {
        writeComments(appendable, header);
    }

    public void writeDocumented(Writer writer, Object conf) throws IOException {
        // Write configuration description
        writeComments(writer, ConfHelper.getDescription(conf));
        writer.append(DOCUMENT_START);
        snakeYAML.getYaml().dumpAll(Iterators.singletonIterator(conf), writer);
        writer.append(newLine);
    }

    private void writeComments(Appendable appendable, String... comments) throws IOException {
        if (!Helper.nullOrEmpty(comments))
            for (String line : comments)
                appendable.append(COMMENT_PREFIX).append(line).append(newLine);
    }

    public void applyStupidFix() {
        snakeYAML.getRepresenter().setDefaultScalarStyle(DumperOptions.ScalarStyle.LITERAL);
    }

    public void revertStupidFix() {
        snakeYAML.getRepresenter().setDefaultScalarStyle(snakeYAML.getDumperOptions().getDefaultScalarStyle());
    }
}
