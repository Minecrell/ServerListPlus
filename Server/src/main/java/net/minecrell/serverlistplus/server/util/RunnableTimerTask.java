package net.minecrell.serverlistplus.server.util;

import java.util.TimerTask;

public final class RunnableTimerTask extends TimerTask {

    private final Runnable runnable;

    public RunnableTimerTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        this.runnable.run();
    }

}
