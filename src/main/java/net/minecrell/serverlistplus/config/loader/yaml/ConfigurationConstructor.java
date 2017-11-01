/*
 * ServerListPlus
 * Copyright (C) 2017 Minecrell <https://github.com/Minecrell>
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

package net.minecrell.serverlistplus.config.loader.yaml;

import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

class ConfigurationConstructor extends CustomClassLoaderConstructor {

    private final Map<Class<?>, Map<Tag, Class<?>>> tags = new HashMap<>();

    @Nullable private Class<?> keyType;
    @Nullable private Class<?> valueType;

    ConfigurationConstructor() {
        super(ConfigurationConstructor.class.getClassLoader());
    }

    <T> void registerType(Class<T> baseType, Tag tag, Class<? extends T> type) {
        Map<Tag, Class<?>> typeTags = tags.computeIfAbsent(baseType, k -> new HashMap<>());
        typeTags.put(tag, type);
    }

    @Override
    protected Class<?> getClassForNode(Node node) {
        Map<Tag, Class<?>> typeTags = tags.get(node.getType());
        if (typeTags != null) {
            Class<?> implClass = typeTags.get(node.getTag());
            if (implClass != null) {
                return implClass;
            }
        }

        return super.getClassForNode(node);
    }

    void reset() {
        this.keyType = null;
        this.valueType =  null;
    }

    void setListType(Class<?> valueType) {
        this.keyType = null;
        this.valueType = valueType;
    }

    void setMapType(Class<?> keyType, Class<?> valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    protected Object constructObject(Node node) {
        if (this.valueType != null) {
            if (this.keyType == null) {
                if (node.getNodeId() != NodeId.sequence) {
                    throw new YamlConstructorException("Cannot construct list", node.getStartMark(),
                            "Expected sequence, got " + node.getNodeId(), node.getStartMark());
                }

                ((SequenceNode) node).setListType(this.valueType);
            } else {
                if (node.getNodeId() != NodeId.mapping) {
                    throw new YamlConstructorException("Cannot construct map", node.getStartMark(),
                            "Expected mapping, got " + node.getNodeId(), node.getStartMark());
                }

                ((MappingNode) node).setTypes(this.keyType, this.valueType);
                this.keyType = null;
            }

            this.valueType = null;
        }

        return super.constructObject(node);
    }

}
