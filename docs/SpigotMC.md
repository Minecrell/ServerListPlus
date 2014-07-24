# ServerListPlus
[ServerListPlus](http://git.io/slp) is a plugin for Bukkit and BungeeCord servers that allows you to customize your complete server status ping! While has most of the features have already been added, the plugin aims to become an universal solution for everything possible for the server status ping! It is compatible with Bukkit/Spigot servers and BungeeCord servers with the same configuration and plugin file therefore switching between them is really easy!

![](http://i.imgur.com/fZUIq2U.png)


## Features
- Custom status descriptions (MotD)!
- Messages when a player hovers the player amount in the server list!
- Change the displayed version when an outdated or newer client pings your server!
- Change the displayed maximal player count in the status ping!
- Load favicons from URLs or display a random icon from the configuration!
- Multiple randomly chosen descriptions, favicons, maximal player counts, ...!
- Personalize your status ping by adding the player's name!
- Use the heads of your players as favicon!
- Use symbols or special characters directly by in your configuration without any escaping!
- Supports both CraftBukkit/Spigot and BungeeCord!


## Installation
1.  Download the plugin and copy the plugin JAR to your CraftBukkit/Spigot or BungeeCord server.
2.  CraftBukkit/Spigot only: Download [ProtocolLib](http://dev.bukkit.org/bukkit-plugins/protocollib/) if you haven't installed it on your server already.
3.  Restart your server to load the plugin.
4.  Go into the plugin folder, open the configuration file `ServerListPlus.yml` and change the status configuration to your likings. You can find more information about the configuration on the [GitHub Wiki](http://git.io/slp-wiki).
5.  Enable the plugin by typing `/serverlistplus enable` on your console. Now ServerListPlus will modify your status ping with your configured values.

### Development Builds
You can download unsupported development builds on the [Jenkins server](http://ci.minecrell.net/job/ServerListPlus/). Use them at your own risk.

## Configuration
All configuration of the plugin is done inside the `ServerListPlus.yml` file. There will be also a `Profiles.json` file in your plugin folder, but that's only used by the plugin, you do not need to edit something there. Most parts of the configuration are explained inside the configuration, but if you need some more help you can take a look at our [GitHub Wiki](http://git.io/slp-wiki) where it is explained more detailed.

## Commands & Permissions
- `/slp` - Display an information page about the plugin and list all available commands.
- `/slp reload` or `/slp rl` - Reload the plugin configuration.
- `/slp save` - Save the plugin configuration.
- `/slp enable` - Enable the plugin and start modifying the status ping.
- `/slp disable` - Disable the plugin and stop modifying the status ping.
- `/slp clean <favicons/players>` - Delete all entries from the specified cache.

If `/slp` doesn't work you can also use `/serverlistplus` instead. The permission for all commands is `serverlistplus.admin`.

## Source
The plugin is completely open source and released under the terms and conditions of the [GNU General Public License](http://www.gnu.org/licenses/gpl-3.0). You are free to redistribute and/or modify it to your likings, but please add a link to the [GitHub Page](http://git.io/slp) of the plugin and redistribute it under a compatible license.

