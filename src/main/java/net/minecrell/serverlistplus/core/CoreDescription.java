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

package net.minecrell.serverlistplus.core;

import com.google.common.base.Preconditions;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class CoreDescription {
    private final String name, version;
    private final String description;
    private final String author;
    private final URL website, wiki;

    public CoreDescription(String name, String version, String description, String author, URL website, URL wiki) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.version = Preconditions.checkNotNull(version, "version");
        this.description = description;
        this.author = author;
        this.website = website;
        this.wiki = wiki;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public URL getWebsite() {
        return website;
    }

    public URL getWiki() {
        return wiki;
    }

    public static CoreDescription load(ServerListPlusCore core) {
        // Load the description from gradle.properties
        try (InputStream in = core.getClass().getResourceAsStream("gradle.properties")) {
            Properties properties = new Properties();
            properties.load(in);
            return new CoreDescription(properties.getProperty("name"), properties.getProperty("version"),
                    properties.getProperty("description"), properties.getProperty("author"),
                    new URL(properties.getProperty("url")), new URL(properties.getProperty("wiki")));
        } catch (Exception e) {
            core.getLogger().log(e, "Unable to load plugin version information from JAR.");
            return new CoreDescription("ServerListPlus", "Unknown", null, null, null, null);
        }
    }
}
