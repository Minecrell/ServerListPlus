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

package net.minecrell.serverlistplus.bukkit.scheduler;

import net.minecrell.serverlistplus.core.plugin.ScheduledTask;
import net.minecrell.serverlistplus.core.util.Wrapper;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public final class BukkitScheduler implements Scheduler {
    private final Plugin plugin;

    public BukkitScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsync(Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit) {
        repeat = unit.toMillis(repeat) / 50;
        return new Task(plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, repeat, repeat));
    }

    static final class Task extends Wrapper<BukkitTask> implements ScheduledTask {
        public Task(BukkitTask handle) {
            super(handle);
        }

        @Override
        public void cancel() {
            handle.cancel();
        }
    }
}
