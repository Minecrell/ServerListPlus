repositories {
    maven {
        name = "sonatype-snapshots"
        setUrl("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.12-SNAPSHOT")
}
