import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "2.0.1" apply false
    id("net.minecrell.licenser") version "0.3"
}

// Common settings for all projects (including core)
allprojects {
    plugins.apply("java")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        // Optional at runtime
        compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.isDeprecation = true
    }

    val jar: Jar by tasks
    jar.manifest {
        attributes["Specification-Title"] = rootProject.name
        attributes["Specification-Version"] = rootProject.version
    }

    plugins.apply("net.minecrell.licenser")

    license {
        header = rootProject.file("HEADER")
        include("**/*.java")
    }
}

// Common settings for all implementations
subprojects {
    base.archivesBaseName = "${rootProject.name}${base.archivesBaseName}"

    dependencies {
        compile(rootProject)
    }

    val jar: Jar by tasks
    jar.manifest {
        attributes["Implementation-Title"] = base.archivesBaseName
        attributes["Implementation-Version"] = version
    }

    plugins.apply("com.github.johnrengelman.shadow")

    val shadowJar: ShadowJar by tasks
    shadowJar {
        baseName = rootProject.name
        classifier = project.name

        dependencies {
            include(project(rootProject.path))
        }
    }

    artifacts {
        add("archives", shadowJar)
    }
}

// Settings for core project
val processResources: AbstractCopyTask by tasks

dependencies {
    // Required at runtime
    compile("org.slf4j:slf4j-api:1.7.25")

    // Provided
    compileOnly("com.google.guava:guava:21.0")
    compileOnly("com.google.code.gson:gson:2.8.0")
    compileOnly("org.yaml:snakeyaml:1.19")
}

processResources {
    from(file("LICENSE"))
    expand(mapOf(
            "name" to project.name,
            "version" to project.version
    ))
}

val sourceJar = task<Jar>("sourceJar") {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)

    subprojects {
        from(java.sourceSets["main"].allSource)
    }
}

artifacts {
    add("archives", sourceJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            artifact(sourceJar)

            subprojects {
                artifact(tasks["shadowJar"])
            }

            repositories {
                maven {
                    setUrl(System.getenv("REPO_" + (if (version.endsWith("-SNAPSHOT")) "SNAPSHOTS" else "RELEASES")) ?: "$buildDir/repo")
                }
            }
        }
    }
}

inline operator fun <T : Task> T.invoke(a: T.() -> Unit): T = apply(a)
