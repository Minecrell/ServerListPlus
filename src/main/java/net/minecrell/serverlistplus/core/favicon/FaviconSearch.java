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

package net.minecrell.serverlistplus.core.favicon;

import static net.minecrell.serverlistplus.core.logging.Logger.Level.WARN;

import com.google.common.collect.ImmutableSet;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.util.Helper;
import net.minecrell.serverlistplus.core.util.PathMatcherFilter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FaviconSearch {
    private FaviconSearch() {}

    public static final String IMAGE_PATTERN = "glob:*.{jpg,jpeg,png,bmp,wbmp,gif}";

    public static Set<String> findInFolder(ServerListPlusCore core, List<String> folders) {
        if (Helper.isNullOrEmpty(folders)) return ImmutableSet.of();
        final Path pluginFolder = core.getPlugin().getPluginFolder();

        final PathMatcher image = pluginFolder.getFileSystem().getPathMatcher(IMAGE_PATTERN);
        DirectoryStream.Filter<Path> filter = new PathMatcherFilter(image);

        final Set<String> favicons = new HashSet<>();
        boolean recursive = core.getConf(PluginConf.class).Favicon.RecursiveFolderSearch;

        for (String folderPath : folders) {
            Path folder = pluginFolder.resolve(folderPath);
            if (!Files.isDirectory(folder)) {
                core.getLogger().log(WARN, "Invalid favicon folder in configuration: " + folder);
                continue;
            }

            if (recursive) { // Also check sub folders
                try { // Walk down the file tree
                    Files.walkFileTree(folder, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                            new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                        throws IOException {
                                    if (image.matches(file.getFileName()))
                                        favicons.add(pluginFolder.relativize(file).toString());
                                    return FileVisitResult.CONTINUE;
                                }
                            });
                } catch (IOException e) {
                    core.getLogger().log(WARN, "Unable to walk through file tree: {} -> {}", folder,
                            Helper.causedException(e));
                }
            } else { // Only this one folder
                try (DirectoryStream<Path> dir = Files.newDirectoryStream(folder, filter)) {
                    for (Path file : dir)
                        favicons.add(pluginFolder.relativize(file).toString());
                } catch (IOException e) {
                    core.getLogger().log(WARN, "Unable to get directory listing: {} -> {}", folder,
                            Helper.causedException(e));
                }
            }
        }

        return favicons;
    }
}
