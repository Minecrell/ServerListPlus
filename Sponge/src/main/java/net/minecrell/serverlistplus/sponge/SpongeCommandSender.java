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

package net.minecrell.serverlistplus.sponge;

import net.minecrell.serverlistplus.core.plugin.ServerCommandSender;
import net.minecrell.serverlistplus.core.util.Wrapper;

import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandSource;

public class SpongeCommandSender extends Wrapper<CommandSource> implements ServerCommandSender {
    public SpongeCommandSender(CommandSource handle) {
        super(handle);
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public void sendMessage(String message) {
        handle.sendMessage(Texts.legacy().fromUnchecked(message));
    }

    @Override
    public void sendMessages(String... messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return handle.hasPermission(permission);
    }

    @Override
    public String toString() {
        return getName();
    }
}
