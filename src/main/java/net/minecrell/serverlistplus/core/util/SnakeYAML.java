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

package net.minecrell.serverlistplus.core.util;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class SnakeYAML {

    private static final String MAVEN_CENTRAL = "https://repo.maven.apache.org/maven2/";

    private static final String YAML_VERSION = "1.27";
    private static final String SNAKE_YAML_JAR = "snakeyaml-" + YAML_VERSION + ".jar";
    private static final String SNAKE_YAML =
            MAVEN_CENTRAL + "org/yaml/snakeyaml/" + YAML_VERSION + '/' + SNAKE_YAML_JAR;

    private static final String EXPECTED_HASH = "359d62567480b07a679dc643f82fc926b100eed5"; // SHA-1

    public static Path load(Path pluginFolder) throws IOException {
        Path libFolder = pluginFolder.resolve("lib");
        Path path = libFolder.resolve(SNAKE_YAML_JAR);

        if (Files.notExists(path)) {
            Files.createDirectories(libFolder);

            URL url = new URL(SNAKE_YAML);

            String hash;

            try (HashingInputStream his = new HashingInputStream(Hashing.sha1(), url.openStream());
                 ReadableByteChannel source = Channels.newChannel(his);
                 FileChannel out = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                out.transferFrom(source, 0, Long.MAX_VALUE);
                hash = his.hash().toString();
            }

            if (!hash.equals(EXPECTED_HASH)) {
                Files.delete(path);
                throw new IOException("Hash mismatch in " + SNAKE_YAML_JAR + ": expected " + EXPECTED_HASH);
            }
        }

        return path;
    }

}
