/*
 * ServerListPlus - Customize your server's ping information!
 * Copyright (C) 2013, Minecrell
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

package net.minecrell.serverlistplus.api.util;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class SimplePrefixLogger extends Logger {
    private final String prefix;

    public SimplePrefixLogger(String name, Logger parent, String prefix) {
        super(name, null);
        this.prefix = "[" + prefix + "] ";
        this.setParent(parent); this.setUseParentHandlers(true);
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void log(LogRecord record) {
        record.setMessage(prefix + record.getMessage());
        super.log(record);
    }
}
