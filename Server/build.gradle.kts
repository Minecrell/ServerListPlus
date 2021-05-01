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
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("io.netty:netty-all:4.1.63.Final")

    implementation("net.minecrell:terminalconsoleappender:1.2.0")
    runtimeOnly("org.jline:jline-terminal-jansi:3.12.1")

    implementation("com.google.guava:guava:25.1-jre") { isTransitive = false }
    implementation("org.yaml:snakeyaml:1.27")
    implementation("com.google.code.gson:gson:2.8.6")

    implementation(platform("net.kyori:adventure-bom:4.7.0"))
    implementation("net.kyori:adventure-text-serializer-plain")
    implementation("net.kyori:adventure-text-serializer-legacy")
    implementation("net.kyori:adventure-text-serializer-gson")
}

tasks {
    named<Jar>("jar") {
        manifest.attributes(mapOf("Main-Class" to "net.minecrell.serverlistplus.server.Main"))
    }
    named<ShadowJar>("shadowJar") {
        transform(Log4j2PluginsCacheFileTransformer())
    }
}
