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
[ServerListPlus](https://github.com/Minecrell/ServerListPlus) is not yet complete. You can find a lot of upcoming and/or planned features on the [GitHub issue tracker](https://github.com/Minecrell/ServerListPlus/issues).


Installation
---------------------
1.  Download the plugin and copy the plugin JAR to your CraftBukkit/Spigot or BungeeCord server.
2.  CraftBukkit/Spigot only: Download [ProtocolLib](http://dev.bukkit.org/bukkit-plugins/protocollib/) if you haven't installed it on your server already.
3.  Restart your server to load the plugin.
4.  Go into the plugin folder, open the configuration file `ServerListPlus.yml` and change the status configuration to your likings. You can find more information about the configuration on the [GitHub Wiki](https://github.com/Minecrell/ServerListPlus/wiki).
5.  Enable the plugin by typing `/serverlistplus enable` on your console. Now ServerListPlus will modify your status ping with your configured values.

Development Builds
---------------------
You can download unsupported development builds on the [Jenkins server](http://ci.minecrell.net/job/ServerListPlus/). Use them at your own risk.

Configuration
---------------------
All configuration of the plugin is done inside the `ServerListPlus.yml` file. There will be also a `Profiles.json` file in your plugin folder, but that's only used by the plugin, you do not need to edit something there. Most parts of the configuration are explained inside the configuration, but if you need some more help you can take a look at our [GitHub Wiki](https://github.com/Minecrell/ServerListPlus/wiki) where it is explained more detailed.

Commands & Permissions
---------------------
- `/serverlistplus` - Display an information page about the plugin and list all available commands.
- `/serverlistplus reload` - Reload the plugin configuration.
- `/serverlistplus save` - Save the plugin configuration.
- `/serverlistplus enable` - Enable the plugin and start modifying the status ping.
- `/serverlistplus disable` - Disable the plugin and stop modifying the status ping.

The permission for all commands is `serverlistplus.admin`.

Source
---------------------
The plugin is completely open source and released under the terms and conditions of the [GNU General Public License](http://www.gnu.org/licenses/gpl-3.0). You are free to redistribute or modify it to your likings, but please add a link to the [GitHub Page](https://github.com/Minecrell/ServerListPlus) of the plugin and redistribute it under a compatible license.
