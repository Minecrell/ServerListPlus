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
