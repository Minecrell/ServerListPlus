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
1.  Download [ServerListPlus](https://github.com/Minecrell/ServerListPlus) from one of the locations listed above.
2.  CraftBukkit/Spigot only: Download [ProtocolLib](http://dev.bukkit.org/bukkit-plugins/protocollib/) if you haven't installed it on your server already.
3.  Copy the plugin JAR to your CraftBukkit/Spigot or BungeeCord server.
4.  Restart your server to load the plugin.
5.  Go into the plugin folder, open the configuration file `ServerListPlus.yml` and change the status configuration to your likings. You can find more information about the configuration on the [GitHub wiki](https://github.com/Minecrell/ServerListPlus/wiki).
6.  Enable the plugin by typing `/serverlistplus enable` on your console. Now ServerListPlus will modify your status ping with your configured values.
7.  You can find a list of all commands, permissions and more information about the configuration on the [GitHub wiki](https://github.com/Minecrell/ServerListPlus/wiki)

Source
---------------------
The plugin is completely open source and released under the terms and conditions of the [GNU General Public License](http://www.gnu.org/licenses/gpl-3.0). You are free to redistribute or modify it to your likings, but please add a link to the [GitHub page](https://github.com/Minecrell/ServerListPlus) of the plugin, thanks!
