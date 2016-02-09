package net.minecrell.serverlistplus.server;

import net.minecrell.serverlistplus.core.plugin.ScheduledTask;
import net.minecrell.serverlistplus.core.util.Wrapper;

import java.util.TimerTask;

public final class ScheduledTimerTask extends Wrapper<TimerTask> implements ScheduledTask {

    public ScheduledTimerTask(TimerTask handle) {
        super(handle);
    }

    @Override
    public void cancel() {
        this.handle.cancel();
    }

}
