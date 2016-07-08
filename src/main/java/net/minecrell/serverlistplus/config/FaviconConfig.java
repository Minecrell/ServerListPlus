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

package net.minecrell.serverlistplus.config;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import net.minecrell.serverlistplus.util.NonnullByDefault;

import java.util.Locale;

import javax.annotation.Nullable;

@NonnullByDefault
public abstract class FaviconConfig extends MappedElement {

    public static class Simple extends FaviconConfig {

        public enum Type {
            FILE("File"), URL("URL"), ENCODED("Encoded");

            private final String displayName;

            Type(String displayName) {
                this.displayName = displayName;
            }
        }

        private Type type;

        public Simple(String key) {
            this.type = Type.valueOf(key.toUpperCase(Locale.ENGLISH));
        }

        @Override
        public String getKey() {
            return this.type.displayName;
        }

        public Simple.Type getType() {
            return this.type;
        }

        public void setType(Simple.Type type) {
            this.type = type;
        }

    }

    public static class Folder extends FaviconConfig {

        @Option(name = "Recursive")
        @Nullable
        private Boolean recursive;

        @Override
        public String getKey() {
            return "Folder";
        }

        public boolean isRecursive() {
            return recursive != null && recursive;
        }

        public void setRecursive(@Nullable Boolean recursive) {
            this.recursive = recursive;
        }

        @Override
        protected Objects.ToStringHelper toStringHelper() {
            return super.toStringHelper()
                    .add("recursive", recursive);
        }
    }

    public static class Head extends FaviconConfig {

        @Option(name = "Helm")
        @Nullable
        private Boolean helm;

        @Override
        public String getKey() {
            return "Head";
        }

        public boolean hasHelm() {
            return helm != null && helm;
        }

        public void setHelm(@Nullable Boolean helm) {
            this.helm = helm;
        }

        @Override
        protected Objects.ToStringHelper toStringHelper() {
            return super.toStringHelper()
                    .add("helm", helm);
        }
    }

    public static ImmutableMap<String, Class<? extends MappedElement>> getFaviconTypes() {
        ImmutableMap.Builder<String, Class<? extends MappedElement>> builder = ImmutableMap.builder();

        for (Simple.Type type : Simple.Type.values()) {
            builder.put(type.displayName, Simple.class);
        }

        builder.put("Folder", Folder.class);
        builder.put("Head", Head.class);

        return builder.build();
    }


}
