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
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.minecrell.net/snapshots/")
    maven("https://destroystokyo.com/repo/repository/maven-snapshots/")
    maven("https://repo.dmulloy2.net/nexus/repository/releases/")
    maven("https://repo.dmulloy2.net/nexus/repository/snapshots/")
    maven("https://ci.frostcast.net/plugin/repository/everything/")
    maven("https://jitpack.io/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/");
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib-API:4.4.0") { isTransitive = false }

    compileOnly("me.confuser.banmanager:BanManagerCommon:7.5.0") { isTransitive = false }
    compileOnly("com.github.seancfoley:ipaddress:5.3.3") /* For BanManager */
    compileOnly("com.github.netherfoam:MaxBans:156239e1f1") { isTransitive = false }

    compileOnly("me.clip:placeholderapi:2.10.10") { isTransitive = false }
}

java {
    disableAutoTargetJvm()
}

bukkit {
    apiVersion = "1.13"
    main = "net.minecrell.serverlistplus.bukkit.BukkitPlugin"

    name = rootProject.name
    softDepend = listOf("ProtocolLib", "AdvancedBan", "BanManager", "MaxBans", "PlaceholderAPI")

    commands {
        register("serverlistplus") {
            description = "Configure ServerListPlus"
            aliases = listOf("slp")
            permission = "serverlistplus.command"
        }
    }

    permissions {
        register("serverlistplus.command") {
            description = "Allows read-only access to ServerListPlus commands"
        }
        register("serverlistplus.admin") {
            description = "Allows to access the ServerListPlus administration commands"
            children = listOf("serverlistplus.command")
        }
    }
}
