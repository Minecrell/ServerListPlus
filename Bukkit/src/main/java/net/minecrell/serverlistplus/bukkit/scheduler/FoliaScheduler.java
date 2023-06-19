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

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.NonNull;
import net.minecrell.serverlistplus.core.util.Wrapper;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class FoliaScheduler implements Scheduler {
    private final Plugin plugin;
    private final AsyncScheduler scheduler;

    public FoliaScheduler(Plugin plugin, Method method) throws ReflectiveOperationException {
        this.plugin = plugin;
        this.scheduler = (AsyncScheduler) method.invoke(plugin.getServer());
    }

    @Override
    public void runAsync(Runnable task) {
        scheduler.runNow(plugin, new RunnableConsumer<ScheduledTask>(task));
    }

    @Override
    public net.minecrell.serverlistplus.core.plugin.ScheduledTask scheduleAsync(Runnable task, long repeat, TimeUnit unit) {
        return new Task(scheduler.runAtFixedRate(plugin, new RunnableConsumer<ScheduledTask>(task), repeat, repeat, unit));
    }

    static final class RunnableConsumer<T> extends Wrapper<Runnable> implements Consumer<T> {
        public RunnableConsumer(@NonNull Runnable handle) {
            super(handle);
        }

        @Override
        public void accept(T unused) {
            getHandle().run();
        }
    }

    static final class Task extends Wrapper<ScheduledTask> implements net.minecrell.serverlistplus.core.plugin.ScheduledTask {
        public Task(ScheduledTask handle) {
            super(handle);
        }

        @Override
        public void cancel() {
            handle.cancel();
        }
    }
}
