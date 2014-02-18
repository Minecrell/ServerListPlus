/*
 *       __                           __ _     _     ___ _
 *      / _\ ___ _ ____   _____ _ __ / /(_)___| |_  / _ \ |_   _ ___
 *      \ \ / _ \ '__\ \ / / _ \ '__/ / | / __| __|/ /_)/ | | | / __|
 *      _\ \  __/ |   \ V /  __/ | / /__| \__ \ |_/ ___/| | |_| \__ \
 *      \__/\___|_|    \_/ \___|_| \____/_|___/\__\/    |_|\__,_|___/
 *                       Customize your server ping!
 *
 *  Copyright (C) 2014, Minecrell <https://github.com/Minecrell>
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.configuration.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.minecrell.serverlistplus.api.ServerListPlusCore;
import net.minecrell.serverlistplus.api.ServerListPlusException;
import net.minecrell.serverlistplus.core.util.Helper;

public class IOUtil {
    private IOUtil() {}

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static final String HEADER_FILENAME = "HEADER";
    private static String[] header;

    public static void loadHeader(ClassLoader loader) throws IOException {
        try (InputStream in = loader.getResourceAsStream(HEADER_FILENAME)) {
            header = (in != null) ? Helper.nullWhenEmpty(readLineArray(in)) : null;
        }
    }
    public static void loadHeader(ServerListPlusCore core) throws ServerListPlusException {
        try {
            loadHeader(core.getClass().getClassLoader());
        } catch (Throwable e) {
            throw core.processException(Level.WARNING, "Unable to read file header!", e);
        }
    }
    public static void writeHeader(BufferedWriter writer, String prefix) throws IOException {
        if (header != null) {
            writePrefixed(writer, prefix, header);
            // Add an empty line after the header
            writer.newLine();
        }
    }

    public static void writePrefixed(BufferedWriter writer, String prefix, String... lines) throws IOException {
        if (lines == null) return;
        for (String line : lines) {
            writer.write(prefix); writer.write(line);
            writer.newLine();
        }
    }

    public static List<String> readLines(InputStream in) throws IOException {
        BufferedReader reader = newBufferedReader(in);
        String line; List<String> result = new ArrayList<>();
        while ((line = reader.readLine()) != null)
            result.add(line);
        return result;
    }

    public static String[] readLineArray(InputStream in) throws IOException {
        return Helper.toStringArray(readLines(in));
    }

    public static BufferedReader newBufferedReader(InputStream in) {
        return new BufferedReader(newReader(in));
    }

    public static BufferedReader newBufferedReader(Path path) throws IOException {
        return Files.newBufferedReader(path, CHARSET);
    }

    public static InputStreamReader newReader(Path path) throws IOException {
        return newReader(Files.newInputStream(path));
    }

    public static InputStreamReader newReader(InputStream in) {
        return new InputStreamReader(in, CHARSET);
    }

    public static BufferedWriter newBufferedWriter(Path path) throws IOException {
        return Files.newBufferedWriter(path, CHARSET);
    }

    public static void rewriteLines(BufferedReader reader, BufferedWriter writer) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line); writer.newLine();
        }
    }
}
