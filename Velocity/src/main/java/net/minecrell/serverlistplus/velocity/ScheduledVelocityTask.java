package net.minecrell.serverlistplus.velocity;

import com.velocitypowered.api.scheduler.ScheduledTask;

class ScheduledVelocityTask implements net.minecrell.serverlistplus.core.plugin.ScheduledTask {

    private final ScheduledTask task;

    ScheduledVelocityTask(ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }

}
