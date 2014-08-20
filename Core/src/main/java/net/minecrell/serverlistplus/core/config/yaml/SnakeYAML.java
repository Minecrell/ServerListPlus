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

package net.minecrell.serverlistplus.core.config.yaml;

import com.google.common.base.Preconditions;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public class SnakeYAML {
    private final Yaml yaml;
    private final boolean outdated;

    private final DumperOptions dumperOptions;
    private final Constructor constructor;
    private final Representer representer;

    public SnakeYAML(DumperOptions dumperOptions, Constructor constructor, Representer representer) {
        this(dumperOptions, constructor, representer, false);
    }

    public SnakeYAML(DumperOptions dumperOptions, Constructor constructor, Representer representer,
                     boolean outdated) {
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