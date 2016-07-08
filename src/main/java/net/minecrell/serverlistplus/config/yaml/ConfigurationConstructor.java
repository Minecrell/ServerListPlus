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
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

class ConfigurationConstructor extends CustomClassLoaderConstructor {

    private static final class ConstructException extends ConstructorException {

        private static final long serialVersionUID = -1169713899789462095L;

        private ConstructException(String context, Mark contextMark, String problem, Mark problemMark) {
            super(context, contextMark, problem, problemMark);
        }

        private ConstructException(String context, Mark contextMark, String problem, Mark problemMark, Throwable cause) {
            super(context, contextMark, problem, problemMark, cause);
        }

    }

    @Nullable private Class<?> keyType;
    @Nullable private Class<?> valueType;

    private final Map<Class<? extends MappedElement>, Map<String, Class<? extends MappedElement>>> mappings = new HashMap<>();

    ConfigurationConstructor() {
        super(ConfigurationConstructor.class.getClassLoader());

        yamlClassConstructors.put(NodeId.scalar, new ConstructScalar(yamlClassConstructors.get(NodeId.scalar)));
        yamlClassConstructors.put(NodeId.mapping, new ConstructMapping(yamlClassConstructors.get(NodeId.mapping)));
    }

    void registerMappedElement(Class<? extends MappedElement> type, String key, Class<? extends MappedElement> element) {
        Map<String, Class<? extends MappedElement>> typeMappings = getMappings(type);
        if (typeMappings == null) {
            typeMappings = new HashMap<>();
            this.mappings.put(type, typeMappings);
        }

        typeMappings.put(key, element);
    }

    void setListType(Class<?> valueType) {
        this.valueType = Objects.requireNonNull(valueType, "valueType");
    }

    void setMapType(Class<?> keyType, Class<?> valueType) {
        this.keyType = Objects.requireNonNull(keyType, "keyType");
        setListType(valueType);
    }

    @Override
    protected Object constructObject(Node node) {
        if (this.valueType != null) {
            if (this.keyType != null) {
                if (node.getNodeId() != NodeId.mapping) {
                    throw new ConstructException("Cannot construct map", node.getStartMark(),
                            "Expected mapping, got " + node.getNodeId(), node.getStartMark());
                }

                ((MappingNode) node).setTypes(keyType, valueType);
            } else {
                if (node.getNodeId() != NodeId.sequence) {
                    throw new ConstructException("Cannot construct list", node.getStartMark(),
                            "Expected sequence, got " + node.getNodeId(), node.getStartMark());
                }

                ((SequenceNode) node).setListType(valueType);
            }

            this.keyType = null;
            this.valueType = null;
        }

        return super.constructObject(node);
    }

    @Nullable
    Map<String, Class<? extends MappedElement>> getMappings(Class<?> type) {
        return mappings.get(type);
    }

    MappedElement constructConf(Map<String, Class<? extends MappedElement>> mappings, Node node, ScalarNode keyNode, @Nullable String value) {
        String key = keyNode.getValue();

        Class<? extends MappedElement> type = mappings.get(key);
        if (type == null) {
            throw new ConstructException("Cannot construct object", node.getStartMark(), "Invalid element type",
                    keyNode.getStartMark());
        }

        // Try String constructor
        MappedElement conf;
        try {
            try {
                conf = type.getConstructor(String.class).newInstance(key);
            } catch (NoSuchMethodException e) {
                conf = type.newInstance();
            }
        } catch (java.lang.Exception e) {
            throw new ConstructException("Cannot construct object", node.getStartMark(), "Failed to invoke constructor: " + e.getMessage(),
                    keyNode.getStartMark());
        }

        conf.setValue(value);
        return conf;
    }

    class ConstructScalar extends ForwardingConstruct {

        ConstructScalar(Construct handle) {
            super(handle);
        }

        @Override
        public Object construct(Node node) {
            Map<String, Class<? extends MappedElement>> mappings = getMappings(node.getType());
            if (mappings == null) {
                return super.construct(node);
            }

            MappedElement conf = constructConf(mappings, node, (ScalarNode) node, null);
            if (!node.isTwoStepsConstruction()) {
                construct2ndStep(node, conf);
            }

            return conf;
        }
    }

    class ConstructMapping extends ForwardingConstruct {

        ConstructMapping(Construct handle) {
            super(handle);
        }

        @Override
        public Object construct(Node node) {
            Map<String, Class<? extends MappedElement>> mappings = getMappings(node.getType());
            if (mappings == null) {
                return super.construct(node);
            }

            MappingNode mnode = (MappingNode) node;
            List<NodeTuple> values = mnode.getValue();
            if (values.isEmpty()) {
                throw new ConstructException("Cannot construct object", node.getStartMark(), "Invalid element without type", node.getStartMark());
            }

            NodeTuple root = values.get(0);
            MappedElement conf = constructConf(mappings, node, (ScalarNode) root.getKeyNode(), ((ScalarNode) root.getValueNode()).getValue());
            if (!node.isTwoStepsConstruction()) {
                construct2ndStep(node, conf);
            }

            return conf;
        }

    }

}
