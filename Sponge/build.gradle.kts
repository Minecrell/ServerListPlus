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

import org.spongepowered.gradle.plugin.config.PluginLoaders

plugins {
    id("org.spongepowered.gradle.plugin") version "2.1.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

sourceSets {
    register("stub") {
        configurations[compileOnlyConfigurationName].extendsFrom(configurations["spongeApi"])
    }
}

dependencies {
    compileOnly(sourceSets["stub"].output)
}

sponge {
    apiVersion("8.1.0")
    license("GPL-3.0-or-later")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("serverlistplus") {
        displayName(rootProject.name)
        entrypoint("net.minecrell.serverlistplus.sponge.SpongePlugin")

        links {
            val url: String by extra
            homepage(url)
            source(url)
            issues("$url-issues")
        }
        val author: String by extra
        contributor(author) {
            description("Developer")
        }
    }
}

license {
    exclude("org/spongepowered/api/")
}