package net.minecrell.serverlistplus.server.config;

import net.minecrell.serverlistplus.core.util.Helper;

import java.util.Collections;
import java.util.List;

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
