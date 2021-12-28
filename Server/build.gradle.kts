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
    implementation("io.netty:netty-all:4.1.71.Final")

    implementation("net.minecrell:terminalconsoleappender:1.3.0")
    // CVE-2021-44228, CVE-2021-45046, CVE-2021-45105, CVE-2021-44832
    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
    runtimeOnly("org.jline:jline-terminal-jansi:3.20.0")
    runtimeOnly("com.lmax:disruptor:3.4.4") // async loggers

    // Note: Before upgrading these dependencies, make sure the core would also compile against them!
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.yaml:snakeyaml:1.29")
    implementation("com.google.code.gson:gson:2.8.9")

    implementation(platform("net.kyori:adventure-bom:4.9.3"))
    implementation("net.kyori:adventure-text-serializer-plain")
    implementation("net.kyori:adventure-text-serializer-legacy")
    implementation("net.kyori:adventure-text-serializer-gson")
}

tasks {
    named<Jar>("jar") {
        manifest.attributes(mapOf("Main-Class" to "net.minecrell.serverlistplus.server.Main"))
    }
    named<ShadowJar>("shadowJar") {
        exclude("module-info.class")
        exclude("META-INF/versions/*/module-info.class")
        manifest.attributes(mapOf("Multi-Release" to true))

        dependencies {
            // The Guava annotations are not needed at runtime https://github.com/google/guava/issues/2824
            // but other Guava dependencies (e.g. com.google.guava:failureaccess) are!
            exclude(dependency("com.google.code.findbugs:jsr305"))
            exclude(dependency("org.checkerframework:checker-qual"))
            exclude(dependency("com.google.errorprone:error_prone_annotations"))
            exclude(dependency("com.google.j2objc:j2objc-annotations"))
        }

        transform(Log4j2PluginsCacheFileTransformer())
    }
}
