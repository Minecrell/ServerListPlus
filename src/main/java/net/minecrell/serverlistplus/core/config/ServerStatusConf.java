/*
 *        _____                     __    _     _   _____ _
 *       |   __|___ ___ _ _ ___ ___|  |  |_|___| |_|  _  | |_ _ ___
 *       |__   | -_|  _| | | -_|  _|  |__| |_ -|  _|   __| | | |_ -|
 *       |_____|___|_|  \_/|___|_| |_____|_|___|_| |__|  |_|___|___|
 *
 *  ServerListPlus - http://git.io/slp
 *  Copyright (c) 2014, Minecrell <https://github.com/Minecrell>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.config;

import net.minecrell.serverlistplus.core.config.help.Description;

import java.util.Map;

@Description({
        "This is the section where you can customize your server status ping!",
        " - Default is the section for the status used when the player name is unknown.",
        "   Personalized is used if the client has already joined the server once.",
        "   Use %player% as placeholder for the player's name.",
        "",
        "Features:",
        " - Description (MotD): A short status message for your server, max. 2 lines.",
        " - Players:",
        "   - Hover: The player hover message that is displayed if you hover the player count.",
        "   - Slots: Custom player slot formatting, see http://git.io/slp-slots",
        " - Favicon:",
        // TODO: http://git.io/slp-favicons?
        "   - Use multiple server icons or the player's head as favicon, see http://git.io/oMhJlg",
        "   - Possible favicon sources: Files, Folders, URLs, Heads, Helms, Encoded",
        "",
        " More features are explained in the Wiki: http://git.io/slp-config",
        "",
        "Usage:",
        " - Add multiple entries for random messages.",
        " - Save the file with valid UTF-8 encoding for special characters.",
        " - Available placeholders:",
        "   - Player name: %player%",
        "   - Player count: %online%, %max%, %online@server% %online@world%",
        "   - Random online player name: %random_player%",
        "   - Ban reason: %ban_reason%",
        "   - Ban operator: %ban_operator%",
        "   - Ban experiation date: %ban_expiration_date%, %ban_expiration_datetime%"
})
public class ServerStatusConf extends PersonalizedStatusConf {
    public Map<String, PersonalizedStatusConf> Hosts;
}
