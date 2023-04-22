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

plugins {
    id("net.kyori.blossom") version "1.3.1"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-releases/")
    maven("https://repo.papermc.io/repository/maven-snapshots/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

blossom {
    replaceTokenIn("src/main/java/net/minecrell/serverlistplus/velocity/VelocityPlugin.java")
    replaceToken("%version%", version)
    replaceToken("%description%", description)
    replaceToken("%url%", extra["url"])
    replaceToken("%author%", extra["author"])
}
