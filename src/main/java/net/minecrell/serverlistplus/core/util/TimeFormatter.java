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

package net.minecrell.serverlistplus.core.util;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class TimeFormatter {

    public static final TimeFormatter DEFAULT_FORMATTER = new TimeFormatter(Locale.getDefault(Locale.Category.FORMAT));

    private static final LoadingCache<String, TimeFormatter> formatterCache = CacheBuilder.newBuilder().build(
            new CacheLoader<String, TimeFormatter>() {
        @Override
        public TimeFormatter load(String key) throws Exception {
            int pos = key.indexOf('_');
            Locale locale;
            if (pos >= 0) {
                locale = new Locale(key.substring(0, pos), key.substring(pos + 1));
            } else {
                locale = new Locale(key);
            }
            return new TimeFormatter(locale);
        }
    });

    public static TimeFormatter get(String locale) {
        if (Strings.isNullOrEmpty(locale)) {
            return DEFAULT_FORMATTER;
        }

        return formatterCache.getUnchecked(locale);
    }

    private final Locale locale;
    private PrettyTime prettyTime;
    private LoadingCache<String, DateFormat> dateFormatCache;
    private LoadingCache<String, DateFormat> dateTimeFormatCache;

    private TimeFormatter(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    private PrettyTime getPrettyTime() {
        if (this.prettyTime == null) {
            this.prettyTime = new PrettyTime(this.locale);
        }

        return this.prettyTime;
    }

    private DateFormat getDateFormat(String format) {
        if (this.dateFormatCache == null) {
            this.dateFormatCache = CacheBuilder.newBuilder().build(new DateFormatLoader(false));
        }

        return this.dateFormatCache.getUnchecked(format);
    }

    private DateFormat getDateTimeFormat(String format) {
        if (this.dateTimeFormatCache == null) {
            this.dateTimeFormatCache = CacheBuilder.newBuilder().build(new DateFormatLoader(true));
        }

        return this.dateTimeFormatCache.getUnchecked(format);
    }

    public String formatDuration(Date date) {
        return getPrettyTime().format(date);
    }

    public String formatPreciseDuration(Date date) {
        PrettyTime prettyTime = getPrettyTime();
        List<Duration> durations = prettyTime.calculatePreciseDuration(date);
        return prettyTime.format(durations);
    }

    public String formatDate(Date date, String format) {
        return getDateFormat(format).format(date);
    }

    public String formatDateTime(Date date, String format) {
        return getDateTimeFormat(format).format(date);
    }

    private class DateFormatLoader extends CacheLoader<String, DateFormat> {

        private final boolean dateTime;

        private DateFormatLoader(boolean dateTime) {
            this.dateTime = dateTime;
        }

        @Override
        public DateFormat load(String key) throws Exception {
            try {
                DateStyle style = DateStyle.valueOf(key.toUpperCase());
                return dateTime ? DateFormat.getDateTimeInstance(style.getStyle(), style.getStyle(), locale) :
                        DateFormat.getDateInstance(style.getStyle(), locale);
            } catch (IllegalArgumentException ignored) {
            }

            return new SimpleDateFormat(key, locale);
        }
    }

    enum DateStyle {
        FULL(DateFormat.FULL),
        LONG(DateFormat.LONG),
        MEDIUM(DateFormat.MEDIUM),
        SHORT(DateFormat.SHORT),
        DEFAULT(DateFormat.DEFAULT);

        private final int style;

        DateStyle(int style) {
            this.style = style;
        }

        public int getStyle() {
            return this.style;
        }
    }

}
