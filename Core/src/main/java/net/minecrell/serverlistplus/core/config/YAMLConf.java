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

package net.minecrell.serverlistplus.core.config;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import net.minecrell.serverlistplus.core.util.Helper;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class YAMLConf {
    public static final String COMMENT_PREFIX = "# ";
    public static final String DOCUMENT_START = "--- ";

    protected final SnakeYAML snakeYAML;
    protected final String newLine;
    protected final Joiner commentWriter;

    public YAMLConf(SnakeYAML snakeYAML) {
        this.snakeYAML = Preconditions.checkNotNull(snakeYAML, "snakeYAML");
        this.newLine = snakeYAML.getDumperOptions().getLineBreak().getString();
        this.commentWriter = Joiner.on(newLine + COMMENT_PREFIX);
    }

    public SnakeYAML snakeYAML() {
        return snakeYAML;
    }

    public String dump(Object conf) {
        return dump(null, conf);
    }

    public <T extends Writer> T write(T writer, Object conf) {
        return write(writer, null, conf);
    }

    public String dump(String[] header, Object conf) {
        return write(new StringWriter(), header, conf).toString();
    }

    @SuppressWarnings("deprecation")
    public <T extends Writer> T write(T writer, String[] header, Object conf) {
        try {
            writeComments(writer, header);
            // There is no dumpAsMap for writers
            Tag root = snakeYAML.getDumperOptions().getExplicitRoot();
            snakeYAML.getDumperOptions().setExplicitRoot(Tag.MAP);
            dumpConf(conf, writer);
            snakeYAML.getDumperOptions().setExplicitRoot(root);
            return writer;
        } catch (IOException e) {
            throw new YAMLException(e);
        }
    }

    public String dumpAll(Object... confs) {
        return dumpAll(null, confs);
    }

    public <T extends Writer> T writeAll(T writer, Object... confs) {
        return writeAll(writer, null, confs);
    }

    public String dumpAll(String[] header, Object... confs) {
        return writeAll(new StringWriter(), header, confs).toString();
    }

    public <T extends Writer> T writeAll(T writer, String[] header, Object... confs) {
        try {
            // Print header to the file if it is not empty
           writeComments(writer, header);

            String[] description;
            for (Object conf : confs) {
                writer.append(newLine);
                writeComments(writer, ConfHelper.getDescription(conf)); // Print section description
                // Start a new section
                writer.append(DOCUMENT_START);
                dumpConf(conf, writer);
            }

            return writer;
        } catch (IOException e) {
            throw new YAMLException(e);
        }
    }

    private void dumpConf(Object conf, Writer writer) {
        // Use singleton iterator to prevent creation of a new ArrayList with only one entry
        snakeYAML.getYaml().dumpAll(Iterators.singletonIterator(conf), writer);
    }

    private void writeComments(Appendable appendable, String... comments) throws IOException {
        if (!Helper.nullOrEmpty(comments))
            commentWriter.appendTo(appendable, Iterators.forArray(comments)).append(newLine);
    }

    public static class SnakeYAML {
        private final Yaml yaml;
        private final boolean outdated;

        private final DumperOptions dumperOptions;
        private final Constructor constructor;
        private final Representer representer;

        public SnakeYAML(DumperOptions dumperOptions, Constructor constructor, Representer representer) {
            this(dumperOptions, constructor, representer, false);
        }

        public SnakeYAML(DumperOptions dumperOptions, Constructor constructor, Representer representer, boolean outdated) {
            this.dumperOptions = Preconditions.checkNotNull(dumperOptions, "dumperOptions");
            this.constructor = Preconditions.checkNotNull(constructor, "constructor");
            this.representer = Preconditions.checkNotNull(representer, "representer");
            this.outdated = outdated;
            this.yaml = new Yaml(constructor, representer, dumperOptions);
        }

        public Yaml getYaml() {
            return yaml;
        }

        public boolean isOutdated() {
            return outdated;
        }

        public DumperOptions getDumperOptions() {
            return dumperOptions;
        }

        public Constructor getConstructor() {
            return constructor;
        }

        public Representer getRepresenter() {
            return representer;
        }
    }
}
