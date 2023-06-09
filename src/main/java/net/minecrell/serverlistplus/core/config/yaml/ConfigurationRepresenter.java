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

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigurationRepresenter extends Representer {

    public ConfigurationRepresenter(DumperOptions dumperOptions) {
        super(dumperOptions);
        // Remove existing representers so we can add something in the beginning
        Map<Class<?>, Represent> backup = new LinkedHashMap<>(multiRepresenters);
        multiRepresenters.clear();

        // Insert ConfigurationSerializable first
        multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());

        // Add back all other representers
        multiRepresenters.putAll(backup);
    }

    @Override // Skip null values for configuration generating
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object value, Tag customTag) {
        if (value != null) {
            NodeTuple tuple = super.representJavaBeanProperty(javaBean, property, value, customTag);
            Node valueNode = tuple.getValueNode();

            // Avoid using tags for enums
            if (customTag == null && valueNode.getNodeId() == NodeId.scalar && value instanceof Enum<?>) {
                valueNode.setTag(Tag.STR);
            }

            return tuple;
        } else {
            return null;
        }
    }

    public class RepresentConfigurationSerializable implements Represent {

        @Override
        public Node representData(Object data) {
            return represent(((ConfigurationSerializable) data).serialize());
        }
    }
}
