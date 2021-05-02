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

package net.minecrell.serverlistplus.core.config.yaml;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

@Getter
public class SnakeYAML {
    private final Yaml yaml;

    private final DumperOptions dumperOptions;
    private final Constructor constructor;
    private final Representer representer;

    public SnakeYAML(DumperOptions dumperOptions, Constructor constructor, Representer representer) {
        this.dumperOptions = Preconditions.checkNotNull(dumperOptions, "dumperOptions");
        this.constructor = Preconditions.checkNotNull(constructor, "constructor");
        this.representer = Preconditions.checkNotNull(representer, "representer");
        this.yaml = new Yaml(constructor, representer, dumperOptions);
    }
}