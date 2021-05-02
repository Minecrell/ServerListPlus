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

package net.minecrell.serverlistplus.core.logging;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import net.minecrell.serverlistplus.core.replacement.util.Literals;

public abstract class AbstractLogger<E extends Throwable> implements Logger<E> {
    public static final String ARG_PATTERN = "{}";

    private final Class<? extends E> exceptionClass;

    protected AbstractLogger(Class<? extends E> errorClass) {
        this.exceptionClass = Preconditions.checkNotNull(errorClass, "exceptionClass");
    }

    protected static String format(String s, Object arg) {
        return Literals.replace(s, ARG_PATTERN, Iterators.singletonIterator(arg));
    }

    protected static String format(String s, Object[] args) {
        return Literals.replace(s, ARG_PATTERN, args);
    }

    /*protected String formatAdvanced(String s, Object[] args) {
        return String.format(s, args);
    }*/

    protected abstract E createException(String message, Throwable thrown);

    @Override
    public final Logger<E> log(Level level, String message, Object arg) {
        return log(level, format(message, arg));
    }

    @Override
    public final Logger<E> log(Level level, String message, Object... args) {
        return log(level, format(message, args));
    }

    /*@Override
    public final Logger<E> logf(Level level, String message, Object... args) {
        return log(level, formatAdvanced(message, args));
    }*/

    @Override
    public final Logger<E> log(Throwable thrown, String message) {
        return log(Level.ERROR, thrown, message);
    }

    @Override
    public final Logger<E> log(Throwable thrown, String message, Object arg) {
        return log(Level.ERROR, thrown, message, arg);
    }

    @Override
    public final Logger<E> log(Throwable thrown, String message, Object... args) {
        return log(Level.ERROR, thrown, message, args);
    }

    /*@Override
    public final Logger<E> logf(Throwable thrown, String message, Object... args) {
        return logf(Level.ERROR, thrown, message, args);
    }*/

    @Override
    public final Logger<E> log(Level level, Throwable thrown, String message, Object arg) {
        return log(level, thrown, format(message, arg));
    }

    @Override
    public final Logger<E> log(Level level, Throwable thrown, String message, Object... args) {
        return log(level, thrown, format(message, args));
    }

    /*@Override
    public final Logger<E> logf(Level level, Throwable thrown, String message, Object... args) {
        return log(level, thrown, formatAdvanced(message, args));
    }*/

    @Override
    public final E process(Throwable thrown, String message) {
        return process(Level.ERROR, thrown, message);
    }

    @Override
    public final E process(Throwable thrown, String message, Object arg) {
        return process(thrown, format(message, arg));
    }

    @Override
    public final E process(Throwable thrown, String message, Object... args) {
        return process(thrown, format(message, args));
    }

    /*@Override
    public final E processf(Throwable thrown, String message, Object... args) {
        return process(thrown, formatAdvanced(message, args));
    }*/

    @Override
    @SuppressWarnings("unchecked")
    public E process(Level level, Throwable thrown, String message) {
        if (thrown != null && thrown.getClass() == exceptionClass) return (E) thrown;
        this.log(level, thrown, message);
        return createException(message, thrown);
    }

    @Override
    public final E process(Level level, Throwable thrown, String message, Object arg) {
        return process(level, thrown, format(message, arg));
    }

    @Override
    public final E process(Level level, Throwable thrown, String message, Object... args) {
        return process(level, thrown, format(message, args));
    }

    /*@Override
    public final E processf(Level level, Throwable thrown, String message, Object... args) {
        return process(level, thrown, formatAdvanced(message, args));
    }*/
}
