/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - Customize your complete server status ping!
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

package net.minecrell.serverlistplus.core;

import java.net.URL;
import java.util.Properties;

import com.google.common.base.Preconditions;

public class CoreDescription {
    private final String name, version;
    private final String description;
    private final String author;
    private final URL website;

    public CoreDescription(String name, String version, String description, String author, URL website) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.version = Preconditions.checkNotNull(version, "version");
        this.description = description;
        this.author = author;
        this.website = website;
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

    private static final String ABOUT_FILE = "ABOUT";

    public static CoreDescription load(ServerListPlusCore core) {
        try {
            Properties about = new Properties();
            about.load(core.getClass().getClassLoader().getResourceAsStream(ABOUT_FILE));
            return new CoreDescription(about.getProperty("Name"), about.getProperty("Version"),
                    about.getProperty("Description"), about.getProperty("Author"),
                    new URL(about.getProperty("Website")));
        } catch (Exception e) {
            core.getLogger().severe(e, "Unable to load core info!");
            return new CoreDescription("ServerListPlus", "Unknown", null, null, null);
        }
    }
}
