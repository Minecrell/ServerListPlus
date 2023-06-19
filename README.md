# ServerListPlus

> ## Important Notice
> ServerListPlus is no longer receiving new features and is only updated to fix bugs or compatibility issues.
> Contributions are welcome! If you are happy with ServerListPlus, you can continue using it.

[ServerListPlus](http://git.io/slp) is an extremely customizable server status ping plugin for Minecraft. It provides an easy-to-use configuration for almost everything possible using the server status ping. The plugin aims to become the universal solution for server status ping customization, available for:
- Bukkit/Spigot/Paper/Folia
- CanaryMod
- Sponge
- BungeeCord
- Velocity

ServerListPlus is also available as [standalone server](https://www.spigotmc.org/resources/serverlistplusserver.20301/) that can be used independent from other server implementations e.g. during maintenance.

## Features
[ServerListPlus](https://git.io/slp) provides options in the configuration for:

- [**Description/Motd**](https://github.com/Minecrell/ServerListPlus/wiki/Status-Configuration#descriptions) with [RGB colors and gradients](https://github.com/Minecrell/ServerListPlus/wiki/Status-Configuration#rgb-colors)
- [**Favicons**](https://github.com/Minecrell/ServerListPlus/wiki/Favicons) (Server icons): Load from files, URLs, or use the player's head for example
- [**Maximum player count**](https://github.com/Minecrell/ServerListPlus/wiki/Status-Configuration#player-count)
- [**Custom player slot format**](https://github.com/Minecrell/ServerListPlus/wiki/Player-Slots)
- [**Player Hover Message**](https://github.com/Minecrell/ServerListPlus/wiki/Status-Configuration#player-hover-messages): Displayed when a player hovers the player count in the server list
- [**Virtual/Forced Hosts**](https://github.com/Minecrell/ServerListPlus/wiki/Virtual-Hosts): Use a custom configuration if players ping the server by using a special IP/hostname to connect with
- Multiple entries to choose a random one from the list
- Personalize the status ping by adding the player's name to the messages

The [features page in the wiki](https://github.com/Minecrell/ServerListPlus/wiki/Features) describes all features in detail.

# Download
You can download the plugin from these official download sites:
- [GitHub Releases](http://git.io/slp-releases) (recommended)
- [SpigotMC](http://www.spigotmc.org/resources/serverlistplus.241/)
- [Sponge](https://ore.spongepowered.org/Minecrell/ServerListPlus)
- [Development Builds](https://ci.codemc.org/job/Minecrell/job/ServerListPlus/) (experimental but usually stable, use this to get the latest updates)
- ~~[BukkitDev](http://dev.bukkit.org/bukkit-plugins/serverlistplus/)~~ (**OUTDATED**)

## Compilation
To compile the plugin, simply clone the repository and build the plugin using Gradle:
```
./gradlew
```

## Source
The plugin is completely open source and released under the terms and conditions of the [GNU General Public License](http://www.gnu.org/licenses/gpl-3.0). You are free to redistribute and/or modify it to your likings, but please add a link to the [GitHub Page](http://git.io/slp) of the plugin and redistribute it under a compatible license.
