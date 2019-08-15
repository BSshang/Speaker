package com.chogge.speaker.view;

import android.os.Handler;

public class BDelay {
    private Runnable delegate;
    private Handler handler = new Handler();
    private long interval;
    private Runnable tickHandler;

    public long getInterval() {
        return this.interval;
    }

    public void setInterval(long delay) {
        this.interval = delay;
    }

    public void updateInterval(long delay) {
        this.interval = delay;
        if (this.handler != null) {
            this.handler.removeCallbacksAndMessages(null);
            this.handler.postDelayed(this.delegate, this.interval);
        }
    }

    public BDelay(long interv, Runnable onTickHandler) {
        this.interval = interv;
        setOnTickHandler(onTickHandler);
        this.handler.postDelayed(this.delegate, this.interval);
    }

    public void setOnTickHandler(Runnable onTickHandler) {
        if (onTickHandler != null) {
            this.tickHandler = onTickHandler;
            this.delegate = new Runnable() {
                public void run() {
                    if (BDelay.this.tickHandler != null) {
                        BDelay.this.handler.removeCallbacksAndMessages(null);
                        BDelay.this.tickHandler.run();
                    }
                }
            };
        }
    }

    public void clear() {
        if (this.handler != null) {
            this.handler.removeCallbacksAndMessages(null);
        }
    }
}
