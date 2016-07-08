/*
 * ServerListPlus
 * Copyright (C) 2016, Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.config.yaml;

import net.minecrell.serverlistplus.config.MappedElement;
import net.minecrell.serverlistplus.logger.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public final class ServerListPlusYaml {

    final Yaml yaml;
    final ConfigurationConstructor constructor;

    public ServerListPlusYaml(Logger logger) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setWidth(Integer.MAX_VALUE);

        this.constructor = new ConfigurationConstructor();
        Representer representer = new ConfigurationRepresenter();
        representer.setPropertyUtils(new ConfigurationPropertyUtils(logger));

        this.yaml = new Yaml(constructor, representer, dumperOptions);
    }

    public void registerMappedElement(Class<? extends MappedElement> type, String key, Class<? extends MappedElement> element) {
        this.constructor.registerMappedElement(type, key, element);
    }

    public YamlConfigReader createReader(BufferedReader reader) {
        return new YamlConfigReader(this, reader);
    }

    public YamlConfigWriter createWriter(BufferedWriter writer) {
        return new YamlConfigWriter(this.yaml, writer);
    }

}
