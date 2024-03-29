/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.api;

import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.StatusResponse;

import java.util.OptionalInt;

/**
 * Represents a specific Minecraft version of a client or a server.
 */
public interface MinecraftVersion extends Comparable<MinecraftVersion> {

    /**
     * Gets the name of this Minecraft version.
     *
     * @implNote The returned name does not necessarily represent
     * the name of a Minecraft version. Depending on the client and
     * implementation, this may also just return a numeric value.
     * @return The version name
     */
    String name();

    /**
     * Gets the protocol version of this Minecraft version.
     *
     * @implNote This should be interpreted together with {@link #isLegacy()},
     * since legacy clients use a different protocol version numbering scheme.
     * @return The protocol version
     * @see <a href="https://minecraft.fandom.com/wiki/Protocol_version">Protocol version (Minecraft Wiki)</a>
     */
    int protocolVersion();

    /**
     * Returns whether this version is an older version that doesn't support
     * all of the features in {@link StatusResponse}. These versions are only
     * supported for the {@link ClientPingServerEvent}, normally they should not be
     * able to join the server.
     *
     * @implNote For Vanilla, this returns {@code true} for all clients older than 1.7.
     * @return {@code True} if this version is a legacy version
     */
    boolean isLegacy();

    /**
     * Gets the data version of this Minecraft version.
     *
     * @implNote The data version will not be available in a status response.
     * @return The data version
     */
    OptionalInt dataVersion();

    @Override
    default int compareTo(MinecraftVersion o) {
        final int result = Boolean.compare(this.isLegacy(), o.isLegacy());
        if (result != 0) {
            return result;
        }
        return this.protocolVersion() - o.protocolVersion();
    }
}
