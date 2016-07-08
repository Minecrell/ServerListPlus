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
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

class ConfigurationRepresenter extends Representer {

    private final Represent handle;

    ConfigurationRepresenter() {
        this.handle = Objects.requireNonNull(this.representers.get(null), "handle");
        this.multiRepresenters.put(MappedElement.class, data -> {
            MappedElement conf = (MappedElement) data;
            MappingNode node = (MappingNode) this.handle.representData(conf);
            if (node.getValue().isEmpty() && conf.getValue() == null) {
                return representScalar(Tag.STR, conf.getKey());
            }

            node.getValue().add(0, new NodeTuple(representScalar(Tag.STR, conf.getKey()), representScalar(Tag.STR, conf.getValue())));
            return node;
        });
    }

    @Override
    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        MappingNode node = super.representJavaBean(properties, javaBean);
        node.setTag(Tag.MAP);
        return node;
    }

    @Override
    @Nullable
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, @Nullable Object propertyValue, Tag customTag) {
        // Skip null values
        return propertyValue != null ? super.representJavaBeanProperty(javaBean, property, propertyValue, customTag) : null;
    }
}
