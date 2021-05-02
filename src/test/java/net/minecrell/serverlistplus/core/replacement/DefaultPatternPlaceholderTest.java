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

package net.minecrell.serverlistplus.core.replacement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.minecrell.serverlistplus.core.status.StatusResponse;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DefaultPatternPlaceholderTest {
    @Test
    public void testPlayerList() {
        final DynamicPlaceholder playerList = DefaultPatternPlaceholder.PLAYER_LIST;
        final List<String> players = Arrays.asList("Peter", "Steve", "Bob", "Notch", "Hans", "Marry", "Tim", "Herobrine");
        final String pattern = "%random_players,7|, |. |\n%";
        final StatusResponse response = mock(StatusResponse.class);

        when(response.getRandomPlayers()).thenReturn(players.iterator());

        assertEquals("Peter, Steve. Bob\nNotch, Hans. Marry\nTim", playerList.replace(response, pattern));
    }
}
