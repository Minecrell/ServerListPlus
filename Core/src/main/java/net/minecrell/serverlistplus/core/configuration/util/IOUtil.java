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

import net.minecrell.serverlistplus.core.util.Helper;

public class IOUtil {
    private IOUtil() {}

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static void newLine(BufferedWriter writer) throws IOException {
        writer.write('\n');
    }

    public static void writePrefixed(BufferedWriter writer, String prefix, String... lines) throws IOException {
        if (Helper.nullOrEmpty(lines)) return;
        for (String line : lines) {
            writer.write(prefix); writer.write(line);
            newLine(writer);
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
}
