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
 *
 * Based on Metrics:
 * Copyright 2011-2013 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package net.minecrell.serverlistplus.api.metrics;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a custom graph on the website
 */
public class Graph {
    /**
     * The graph's name, alphanumeric and spaces only :) If it does not comply to the above when submitted, it is
     * rejected
     */
    private final String name;

    /**
     * The set of plotters that are contained within this graph
     */
    private final Set<Plotter> plotters = new LinkedHashSet<Plotter>();

    protected Graph(final String name) {
        this.name = name;
    }

    /**
     * Gets the graph's name
     *
     * @return the Graph's name
     */
    public String getName() {
        return name;
    }

    /**
     * Add a plotter to the graph, which will be used to plot entries
     *
     * @param plotter the plotter to add to the graph
     */
    public void addPlotter(final Plotter plotter) {
        plotters.add(plotter);
    }

    /**
     * Remove a plotter from the graph
     *
     * @param plotter the plotter to remove from the graph
     */
    public void removePlotter(final Plotter plotter) {
        plotters.remove(plotter);
    }

    /**
     * Gets an <b>unmodifiable</b> set of the plotter objects in the graph
     *
     * @return an unmodifiable {@link java.util.Set} of the plotter objects
     */
    public Set<Plotter> getPlotters() {
        return Collections.unmodifiableSet(plotters);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Graph)) {
            return false;
        }

        final Graph graph = (Graph) object;
        return graph.name.equals(name);
    }
}
