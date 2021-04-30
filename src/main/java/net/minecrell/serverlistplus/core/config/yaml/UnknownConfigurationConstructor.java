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

import static net.minecrell.serverlistplus.core.logging.Logger.Level.WARN;

import com.google.common.base.Throwables;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.UnknownConf;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

public class UnknownConfigurationConstructor extends CustomClassLoaderConstructor {
    private final ServerListPlusCore core;

    public UnknownConfigurationConstructor(ServerListPlusCore core) {
        super(core.getClass().getClassLoader());
        this.core = core;
    }

    @Override
    protected Class<?> getClassForNode(Node node) {
        try {
            return super.getClassForNode(node);
        } catch (YAMLException e) {
            core.getLogger().log(WARN, "Unknown configuration: {} -> {}", node.getTag().getValue(),
                    Throwables.getRootCause(e).getMessage());
            return UnknownConf.class;
        }
    }
}
