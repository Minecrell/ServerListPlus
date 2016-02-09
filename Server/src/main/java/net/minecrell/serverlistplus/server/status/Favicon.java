package net.minecrell.serverlistplus.server.status;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class Favicon {

    private static final String FAVICON_PREFIX = "data:image/png;base64,";

    private Favicon() {
    }

    public static String create(BufferedImage image) throws IOException {
        checkArgument(image.getWidth() == 64, "favicon must be 64 pixels wide");
        checkArgument(image.getHeight() == 64, "favicon must be 64 pixels high");

        ByteBuf buf = Unpooled.buffer();
        try {
            ImageIO.write(image, "PNG", new ByteBufOutputStream(buf));
            ByteBuf base64 = Base64.encode(buf);
            try {
                return FAVICON_PREFIX + base64.toString(Charsets.UTF_8);
            } finally {
                base64.release();
            }
        } finally {
            buf.release();
        }
    }

}
