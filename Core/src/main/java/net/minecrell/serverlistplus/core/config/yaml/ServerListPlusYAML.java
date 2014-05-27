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

import java.io.IOException;
import java.util.logging.Level;

import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.io.IOUtil;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.representer.Representer;

public final class ServerListPlusYAML {
    private ServerListPlusYAML() {}

    private static final String HEADER_FILENAME = "HEADER";

    public static YAMLWriter createWriter(ServerListPlusCore core) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Constructor constructor = new CustomClassLoaderConstructor(core.getClass().getClassLoader());
        Representer representer = new NullSkippingRepresenter();
        representer.setPropertyUtils(new FieldOrderPropertyUtils());

        boolean outdatedYaml = false;

        try {
            representer.getPropertyUtils().setSkipMissingProperties(true);
        } catch (Throwable e) {
            outdatedYaml = true; // Meh, CraftBukkit is using an outdated SnakeYAML version
            core.getLogger().warning("Your server is running an outdated YAML version. The configuration loading might be " +
                    "working incorrectly.");
        }

        Iterable<String> header = null;
        try {
            header = IOUtil.readLines(core.getClass().getResourceAsStream(HEADER_FILENAME));
        } catch (IOException e) {
            core.getLogger().log(Level.WARNING, "Unable to read configuration header!", e);
        }

        return new YAMLWriter(new SnakeYAML(dumperOptions, constructor, representer, outdatedYaml), header);
    }
}
