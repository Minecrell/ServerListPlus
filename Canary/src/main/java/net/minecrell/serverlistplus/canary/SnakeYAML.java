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

package net.minecrell.serverlistplus.canary;

import com.google.common.io.BaseEncoding;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Set;

public final class SnakeYAML {

    private static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2/";

    private static final String YAML_VERSION = "1.16";
    private static final String SNAKE_YAML_JAR = "snakeyaml-" + YAML_VERSION + ".jar";
    private static final String SNAKE_YAML =
            MAVEN_CENTRAL + "org/yaml/snakeyaml/" + YAML_VERSION + '/' + SNAKE_YAML_JAR;

    private static final String EXPECTED_HASH = "D64FB662C9E42789149F5078A62A22EDDA786C6A"; // SHA-1

    private static boolean isLoaded(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ignored) {
            try {
                // Remove the class from LaunchClassLoader cache if present
                ClassLoader classLoader = SnakeYAML.class.getClassLoader();
                Field invalidClasses = classLoader.getClass().getDeclaredField("invalidClasses");
                invalidClasses.setAccessible(true);

                @SuppressWarnings("unchecked")
                Set<String> classes  = (Set<String>) invalidClasses.get(classLoader);
                if (classes != null) {
                    classes.remove(className);
                }
            } catch (Exception ignore) {
            }
        }

        return false;
    }

    @SneakyThrows
    public static void load() {
        //if (isLoaded("org.yaml.snakeyaml.Yaml")) return;
        Path path = Paths.get("lib", SNAKE_YAML_JAR);

        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());

            URL url = new URL(SNAKE_YAML);
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

            try (ReadableByteChannel source = Channels.newChannel(new DigestInputStream(url.openStream(), sha1));
                 FileChannel out = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                out.transferFrom(source, 0, Long.MAX_VALUE);
            }

            if (!BaseEncoding.base16().encode(sha1.digest()).equals(EXPECTED_HASH)) {
                Files.delete(path);
                throw new IllegalStateException("Downloaded SnakeYAML, but checksum check failed. Please try again later.");
            }
        }

        loadJAR(path);
    }

    @SneakyThrows
    private static void loadJAR(Path path) {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(SnakeYAML.class.getClassLoader(), path.toUri().toURL());
    }
}
