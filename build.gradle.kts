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

plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.2" apply false
    id("org.cadixdev.licenser") version "0.6.1"
}

defaultTasks("clean", "build")

allprojects {
    plugins.apply("java")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_7
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.34")
        annotationProcessor("org.projectlombok:lombok:1.18.34")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.isDeprecation = true
    }

    plugins.apply("org.cadixdev.licenser")

    license {
        header(rootProject.file("src/main/resources/LICENSE"))
        include("**/*.java")
        include("**/*.kts")

        tasks {
            register("gradle") {
                files(project.files("build.gradle.kts", "settings.gradle.kts"))
            }
        }
    }
}

subprojects {
    base {
        // Using archivesName.map { "${rootProject.name}$it" } seems to cause StackOverflowError...
        archivesName.set("${rootProject.name}${archivesName.get()}")
    }

    dependencies {
        implementation(rootProject)
    }

    plugins.apply("com.gradleup.shadow")

    tasks.withType<ShadowJar> {
        artifacts.add("archives", this)

        archiveBaseName.set(rootProject.name)
        archiveClassifier.set(project.name)
        duplicatesStrategy = DuplicatesStrategy.FAIL

        manifest.attributes(mapOf("Multi-Release" to true))
        exclude("module-info.class", "META-INF/versions/*/module-info.class")

        if (project.name != "Server") {
            exclude("META-INF/maven/", "META-INF/proguard/", "META-INF/services/")

            dependencies {
                include(project(rootProject.path))
                include(dependency("org.ocpsoft.prettytime:prettytime"))
                include(dependency("org.yaml:snakeyaml"))
            }

            relocate("org.ocpsoft.prettytime", "net.minecrell.serverlistplus.core.lib.prettytime")
            relocate("org.yaml.snakeyaml", "net.minecrell.serverlistplus.core.lib.snakeyaml")
        }
    }
}

repositories {
    maven("https://jitpack.io/")
}

dependencies {
    // Provided by platform at runtime
    implementation("com.google.guava:guava:21.0")
    implementation("com.google.code.gson:gson:2.8.0")
    // Included in ServerListPlus JAR (with relocation)
    implementation("org.yaml:snakeyaml:2.3")
    implementation("org.ocpsoft.prettytime:prettytime:4.0.6.Final")

    compileOnly("org.slf4j:slf4j-api:1.7.25")
    compileOnly("org.apache.logging.log4j:log4j-api:2.8.1")
    compileOnly("com.github.DevLeoko.AdvancedBan:AdvancedBan-Core:v2.3.0") { isTransitive = false }

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.11.0")
}

java {
    withSourcesJar()
}

tasks {
    // Copy project properties, loaded at runtime for version information
    named<AbstractCopyTask>("processResources") {
        expand(project.properties) // Replace variables in HEADER file

        from("gradle.properties") {
            into("net/minecrell/serverlistplus/core")
        }
    }

    // Universal JAR that works on multiple platforms
    val universal = register<Jar>("universal") {
        artifacts.add("archives", this)

        archiveClassifier.set("Universal")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        manifest.attributes(mapOf("Multi-Release" to true))

        for (p in arrayOf("Bukkit", "Bungee", "Canary", "Sponge", "Velocity")) {
            val task = project(p).tasks.named("shadowJar")
            dependsOn(task)
            from(zipTree(task.map { it.outputs.files.singleFile }))
        }
    }
    artifacts.add("archives", universal)

    // Bundle all sources together into one source JAR
    named<AbstractCopyTask>("sourcesJar") {
        subprojects {
            from(sourceSets["main"].allSource)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.named("universal"))

            subprojects {
                tasks.withType<ShadowJar> {
                    artifact(this)
                }
            }
        }
    }

    repositories {
        val mavenUrl: String? by project
        val mavenSnapshotUrl: String? by project

        (if (version.toString().endsWith("-SNAPSHOT")) mavenSnapshotUrl else mavenUrl)?.let { url ->
            maven(url) {
                val mavenUsername: String? by project
                val mavenPassword: String? by project
                if (mavenUsername != null && mavenPassword != null) {
                    credentials {
                        username = mavenUsername
                        password = mavenPassword
                    }
                }
            }
        }
    }
}
