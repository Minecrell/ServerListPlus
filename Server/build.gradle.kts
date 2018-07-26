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

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compile("io.netty:netty-all:4.1.27.Final")

    compile("com.google.guava:guava:25.1-jre")
    compile("org.yaml:snakeyaml:1.21")
    compile("com.google.code.gson:gson:2.8.5")
}

tasks {
    getByName<Jar>("jar") {
        manifest.attributes(mapOf("Main-Class" to "net.minecrell.serverlistplus.server.Main"))
    }

    getByName<ShadowJar>("shadowJar") {
        dependencies {
            include(dependency("io.netty:netty-all"))

            include(dependency("com.google.guava:guava"))
            include(dependency("org.yaml:snakeyaml"))
            include(dependency("com.google.code.gson:gson"))
        }
    }
}
