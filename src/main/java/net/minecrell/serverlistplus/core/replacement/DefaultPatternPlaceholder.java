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

package net.minecrell.serverlistplus.core.replacement;

import lombok.Getter;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.config.PluginConf;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import net.minecrell.serverlistplus.core.replacement.util.Patterns;
import net.minecrell.serverlistplus.core.status.StatusResponse;
import net.minecrell.serverlistplus.core.util.ContinousIterator;
import net.minecrell.serverlistplus.core.util.TimeFormatter;

import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DefaultPatternPlaceholder implements DynamicPlaceholder {
    ONLINE_AT (Pattern.compile("%online@((?:\\w|[-_])+)%")) {
        @Override
        public String replace(final ServerListPlusCore core, String s) {
            final Matcher matcher = matcher(s);
            return Patterns.replace(matcher, s, new ContinousIterator<Object>() {
                private final String unknown = Matcher.quoteReplacement(core.getConf(PluginConf.class).Unknown
                        .PlayerCount);

                @Override
                public Object next() {
                    Integer players = core.getPlugin().getOnlinePlayers(matcher.group(1));
                    return players != null ? players : unknown;
                }
            });
        }
    },
    RANDOM_PLAYER_AT (Pattern.compile("%random_player@((?:\\w|[-_])+)%")) {
        @Override
        public String replace(final StatusResponse response, String s) {
            final Matcher matcher = matcher(s);
            final String unknown = response.getCore().getConf(PluginConf.class).Unknown.PlayerName;
            return Patterns.replace(matcher, s, new ContinousIterator<Object>() {
                @Override
                public Object next() {
                    Iterator<String> players = response.getRandomPlayers(matcher.group(1));
                    return players != null && players.hasNext() ? players.next() : unknown;
                }
            });
        }

        @Override
        public String replace(final ServerListPlusCore core, String s) {
            return replace(s, core.getConf(PluginConf.class).Unknown.PlayerName);
        }
    },
    PLAYER_LIST (Pattern.compile("%random_players(?:@((?:\\w|[-_])+))?(?:,(\\d+))?(?:\\|([^%]+))?%", Pattern.MULTILINE)) {
        private static final int DEFAULT_LIMIT = 5;
        private static final String DEFAULT_DELIMITER = "\n";

        @Override
        public String replace(final StatusResponse response, String s) {
            final Matcher matcher = matcher(s);
            return Patterns.replace(matcher, s, new ContinousIterator<Object>() {
                @Override
                public Object next() {
                    String group = matcher.group(1);
                    Iterator<String> players = group == null ? response.getRandomPlayers()
                            : response.getRandomPlayers(group);
                    if (players == null || !players.hasNext()) return "";
                    group = matcher.group(2);
                    int max = group == null ? DEFAULT_LIMIT : Integer.parseInt(group);
                    group = matcher.group(3);
                    String delimiter = group == null ? DEFAULT_DELIMITER : group;

                    StringBuilder result = new StringBuilder();
                    int i = 1;
                    while (true) {
                        result.append(players.next());
                        if (i >= max || !players.hasNext()) break;
                        result.append(delimiter); i++;
                    }

                    return result.toString();
                }
            });
        }

        @Override
        public String replace(ServerListPlusCore core, String s) {
            return replace(s, core.getConf(PluginConf.class).Unknown.PlayerName);
        }
    },
    LAST_ONLINE(Pattern.compile("%last_online(?:_precise)?_duration(?:@([\\w-]+))?%")) {

        @Override
        public String replace(StatusResponse response, String s) {
            PlayerIdentity identity = response.getRequest().getIdentity();
            if (identity == null) {
                return super.replace(response, s);
            }

            final Date date = identity.getTime();
            if (date == null) {
                return super.replace(response, s);
            }

            final Matcher matcher = matcher(s);
            return Patterns.replace(matcher, s, new ContinousIterator<Object>() {
                @Override
                public Object next() {
                    boolean exact = matcher.group().startsWith("%last_online_precise");
                    TimeFormatter formatter = TimeFormatter.get(matcher.group(1));
                    return exact ? formatter.formatPreciseDuration(date) : formatter.formatDuration(date);
                }
            });
        }

        @Override
        public String replace(ServerListPlusCore core, String s) {
            return replace(s, core.getConf(PluginConf.class).Unknown.Date);
        }
    },
    LAST_ONLINE_DATE(Pattern.compile("%last_online_date(?:time)?(?:\\|(.*?))?(?:@([\\w-]+))?%")) {
        @Override
        public String replace(StatusResponse response, String s) {
            PlayerIdentity identity = response.getRequest().getIdentity();
            if (identity == null) {
                return super.replace(response, s);
            }

            final Date date = identity.getTime();
            if (date == null) {
                return super.replace(response, s);
            }

            final Matcher matcher = matcher(s);
            return Patterns.replace(matcher, s, new ContinousIterator<Object>() {
                @Override
                public Object next() {
                    boolean dateTime = matcher.group().startsWith("%last_online_datetime");

                    TimeFormatter formatter = TimeFormatter.get(matcher.group(2));

                    String format = matcher.group(1);
                    if (format == null) {
                        format = "DEFAULT";
                    }

                    return dateTime ? formatter.formatDateTime(date, format) : formatter.formatDate(date, format);
                }
            });
        }

        @Override
        public String replace(ServerListPlusCore core, String s) {
            return replace(s, core.getConf(PluginConf.class).Unknown.Date);
        }
    },
    BAN_EXPIRATION_DATE(Pattern.compile("%ban_expiration_date(?:time)?(?:\\|(.*?))?(?:@([\\w-]+))?%")) {
        @Override
        public String replace(StatusResponse response, String s) {
            PlayerIdentity identity = response.getRequest().getIdentity();
            if (identity == null) {
                return super.replace(response, s);
            }
            
            BanProvider banDetector = response.getCore().getBanProvider();
            final Date date = banDetector.getBanExpiration(identity);
            if (date == null) {
                return super.replace(response, s);
            }

            final Matcher matcher = matcher(s);
            return Patterns.replace(matcher, s, new ContinousIterator<Object>() {
                @Override
                public Object next() {
                    boolean dateTime = matcher.group().startsWith("%ban_expiration_datetime");

                    TimeFormatter formatter = TimeFormatter.get(matcher.group(2));

                    String format = matcher.group(1);
                    if (format == null) {
                        format = "DEFAULT";
                    }

                    return dateTime ? formatter.formatDateTime(date, format) : formatter.formatDate(date, format);
                }
            });
        }

        @Override
        public String replace(ServerListPlusCore core, String s) {
            return replace(s, core.getConf(PluginConf.class).Unknown.BanExpirationDate);
        }
    };

    protected final @Getter Pattern pattern;

    DefaultPatternPlaceholder(Pattern pattern) {
        this.pattern = pattern;
    }

    protected Matcher matcher(String s) {
        return pattern.matcher(s);
    }

    @Override
    public boolean find(String s) {
        return Patterns.find(pattern, s);
    }

    @Override
    public String replace(String s, Object replacement) {
        return Patterns.replace(s, pattern, replacement);
    }

    @Override
    public String replace(String s, Iterator<?> replacements) {
        return Patterns.replace(s, pattern, replacements);
    }

    @Override
    public String replace(String s, Iterator<?> replacements, Object others) {
        return Patterns.replace(s, pattern, replacements, others);
    }

    @Override
    public String replace(StatusResponse response, String s) {
        return replace(response.getCore(), s);
    }
}
