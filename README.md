ServerListPlus
==============

[ServerListPlus](https://github.com/Minecrell/ServerListPlus) is a new Minecraft plugin that allows you to customize your complete server status ping. It is not yet complete, but new features will be added soon.

Features
---------------------
- Add custom status descriptions (MotD).
- Add messages when a player hovers the player amount in the server list.
- Choose a random entry by adding multiple ones.
- Personalize your status by adding the player's name.
- Use symbols or special characters directly by saving your configuration using `UTF-8`.
- Currently supports CraftBukkit/Spigot and BungeeCord.

Upcoming Features
---------------------
[ServerListPlus](https://github.com/Minecrell/ServerListPlus) is not yet complete. You can find a lot of upcoming and/or planned features on the GitHub issue tracker.

Download
---------------------
There are multiple locations where you can download [ServerListPlus](https://github.com/Minecrell/ServerListPlus):
- [GitHub Releases](https://github.com/Minecrell/ServerListPlus/releases)
- [SpigotMC](www.spigotmc.org/resources/serverlistplus.241/)
- [BukkitDev](http://dev.bukkit.org/bukkit-plugins/serverlistplus/)
- [Development Builds](http://ci.minecrell.net/job/ServerListPlus/)

Compilation
---------------------
You can also compile it yourself by cloning the repository from [GitHub](https://github.com/Minecrell/ServerListPlus) and executing the following command on the command line: `gradlew clean build shadowJar`

Installation
---------------------
Installation of [ServerListPlus](https://github.com/Minecrell/ServerListPlus) is actually quite easy:
1. Download [ServerListPlus](https://github.com/Minecrell/ServerListPlus) from one of the locations listed above.
2. Copy the plugin JAR to your CraftBukkit/Spigot or BungeeCord server.
3. Restart your server to load the plugin.
4. Go into the plugin folder, open the configuration file `ServerListPlus.yml` and change the status configuration to your likings. You can find more information about the configuration on the GitHub wiki.
5. Enable the plugin by typing `/serverlistplus enable` on your console. Now ServerListPlus will modify your status ping with your configured values.
