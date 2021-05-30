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

package net.minecrell.serverlistplus.bukkit.config;

import net.minecrell.serverlistplus.core.config.help.Description;

@Description({
        "This section is only for the Bukkit plugin of ServerListPlus.",
        "ProtocolLib: (DISABLE|AUTO|ENABLE) Decides if ProtocolLib should be used by ServerListPlus.",
        "  Generally, AUTO (the default) is a good choice: ProtocolLib is used on Spigot for",
        "  full functionality, but disabled on Paper because it is not needed there.",
        "  Note that this might cause other plugins that make use of ProtocolLib to overwrite",
        "  the changes made by ServerListPlus. In that case, try setting ENABLE instead."
})
public class BukkitConf {
    public ProtocolLibUsage ProtocolLib = ProtocolLibUsage.AUTO;
}
