/*
 *       _____                     _____ _         _____ _
 *      |   __|___ ___ _ _ ___ ___|  _  |_|___ ___|  _  | |_ _ ___
 *      |__   | -_|  _| | | -_|  _|   __| |   | . |   __| | | |_ -|
 *      |_____|___|_|  \_/|___|_| |__|  |_|_|_|_  |__|  |_|___|___|
 *                                            |___|
 *  ServerPingPlus - Customize your server ping!
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

package net.minecrell.serverpingplus.core.config.yamlconf;

import net.minecrell.serverpingplus.core.config.yamlconf.yaml.FieldOrderPropertyUtils;
import net.minecrell.serverpingplus.core.config.yamlconf.yaml.NullSkippingRepresenter;

import com.google.common.collect.Iterators;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

public class YAMLConf {
    public static final String COMMENT_PREFIX = "# ";
    public static final char NEWLINE = '\n';

    private final Yaml yaml;
    private final DumperOptions yamlDumper;
    private final Constructor yamlConstructor;
    private final Representer yamlRepresenter;

    public YAMLConf() {
        this.yamlDumper = new DumperOptions();
        yamlDumper.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlDumper.setLineBreak(DumperOptions.LineBreak.UNIX);

        this.yamlConstructor = new Constructor();
        (this.yamlRepresenter = new NullSkippingRepresenter()).setPropertyUtils(new FieldOrderPropertyUtils());

        this.yaml = new Yaml(yamlConstructor, yamlRepresenter, yamlDumper);
    }

    public String dump(String[] header, Object... confs) {
        StringBuilder output = new StringBuilder();
        for (String line : header) {
            output.append(COMMENT_PREFIX).append(line).append(NEWLINE);
        }
        return output.append(yaml.dumpAll(Iterators.forArray(confs))).toString();
    }

}
