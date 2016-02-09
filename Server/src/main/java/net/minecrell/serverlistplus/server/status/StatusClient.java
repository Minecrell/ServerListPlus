package net.minecrell.serverlistplus.server.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.OptionalInt;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class StatusClient {

    private InetSocketAddress address;
    private OptionalInt protocol;
    private InetSocketAddress virtualHost;

    public void setProtocol(int protocol) {
        this.protocol = OptionalInt.of(protocol);
    }

}
