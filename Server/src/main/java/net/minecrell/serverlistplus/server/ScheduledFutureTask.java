package net.minecrell.serverlistplus.server;

import net.minecrell.serverlistplus.core.plugin.ScheduledTask;
import net.minecrell.serverlistplus.core.util.Wrapper;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public final class ScheduledFutureTask extends Wrapper<ScheduledFuture<?>> implements ScheduledTask {

    public ScheduledFutureTask(ScheduledFuture<?> handle) {
        super(handle);
    }

    @Override
    public void cancel() {
        this.handle.cancel(false);
    }
}
