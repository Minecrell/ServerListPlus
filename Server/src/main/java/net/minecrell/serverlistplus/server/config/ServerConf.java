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

package net.minecrell.serverlistplus.server.config;

import net.minecrell.serverlistplus.core.config.help.Description;
import net.minecrell.serverlistplus.core.util.Helper;

import java.util.Collections;
import java.util.List;

@Description({
        "This section is only for the standalone server implementation of ServerListPlus.",
        "Address: The address to bind the server to. Can be used to specify the port.",
        "  - Example: :25565 will bind the server to port 25565",
        "Login -> Message: The message displayed when the players are kicked from the server."
})
public class ServerConf {
    public String Address = "";
    public LoginConf Login = new LoginConf();

    public static class LoginConf {
        public List<String> Message;
    }

    public static ServerConf getExample() {
        ServerConf conf = new ServerConf();
        conf.Address = ":25595";
        conf.Login.Message = Collections.singletonList(
                Helper.joinLines(
                        "&aWelcome %player%! We're currently doing maintenance on our server.",
                        "&ePlease try again later! :)")
        );
        return conf;
    }

}
