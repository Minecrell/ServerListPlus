package net.minecrell.serverlistplus.velocity;

import net.minecrell.serverlistplus.core.plugin.ScheduledTask;

import java.util.concurrent.ScheduledFuture;

class ScheduledVelocityTask implements ScheduledTask {

    private final ScheduledFuture<?> future;

    ScheduledVelocityTask(ScheduledFuture<?> future) {
        this.future = future;
    }

    @Override
    public void cancel() {
        this.future.cancel(false);
    }

}
