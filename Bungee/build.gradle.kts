plugins {
    id("net.minecrell.plugin-yml.bungee") version "0.2.0"
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.12-SNAPSHOT")
}

bungee {
    name = rootProject.name
    main = "net.minecrell.serverlistplus.bungee.BungeePlugin"
}
