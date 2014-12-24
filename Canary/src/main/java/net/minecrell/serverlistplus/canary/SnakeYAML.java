/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *    > The most customizable server status ping plugin for Minecraft!
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.canary;

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

import lombok.SneakyThrows;
import net.canarymod.plugin.Plugin;
import org.apache.commons.codec.binary.Hex;

public final class SnakeYAML {

    private static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2/";

    private static final String YAML_VERSION = "1.14";
    private static final String SNAKE_YAML_JAR = "snakeyaml-" + YAML_VERSION + ".jar";
    private static final String SNAKE_YAML =
            MAVEN_CENTRAL + "org/yaml/snakeyaml/" + YAML_VERSION + "/" + SNAKE_YAML_JAR;

    private static final String EXPECTED_HASH = "c2df91929ed06a25001939929bff5120e0ea3fd4"; // SHA-1

    @SneakyThrows
    public static void load(Plugin plugin) {
        try { // Check if it is already loaded
            Class.forName("org.yaml.snakeyaml.Yaml");
            return;
        } catch (ClassNotFoundException ignored) {}

        Path path = Paths.get("lib", SNAKE_YAML_JAR);

        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());

            plugin.getLogman().info("Downloading SnakeYAML...");

            URL url = new URL(SNAKE_YAML);
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

            try (ReadableByteChannel source = Channels.newChannel(new DigestInputStream(url.openStream(), sha1));
                 FileChannel out = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                out.transferFrom(source, 0, Long.MAX_VALUE);
            }

            if (!new String(Hex.encodeHex(sha1.digest())).equals(EXPECTED_HASH)) {
                Files.delete(path);
                throw new IllegalStateException("Downloaded SnakeYAML, but checksum check failed. Please try again later.");
            }

            plugin.getLogman().info("Successfully downloaded!");
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
