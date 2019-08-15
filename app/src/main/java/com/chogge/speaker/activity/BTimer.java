package com.chogge.speaker.activity;

import android.os.Handler;

public class BTimer {
    private Runnable delegate;
    private Handler handler = new Handler();
    private long interval;
    private Runnable tickHandler;
    private boolean ticking;

    public long getInterval() {
        return this.interval;
    }

    public void setInterval(long delay) {
        this.interval = delay;
    }

    public boolean isTicking() {
        return this.ticking;
    }

    public BTimer(long interv) {
        this.interval = interv;
    }

    public BTimer(long interv, Runnable onTickHandler) {
        this.interval = interv;
        setOnTickHandler(onTickHandler);
    }

    public void start(long interv, Runnable onTickHandler) {
        if (!this.ticking) {
            this.interval = interv;
            setOnTickHandler(onTickHandler);
            this.handler.postDelayed(this.delegate, this.interval);
            this.ticking = true;
        }
    }

    public void start() {
        if (!this.ticking) {
            this.handler.postDelayed(this.delegate, this.interval);
            this.ticking = true;
        }
    }

    public void stop() {
        this.handler.removeCallbacks(this.delegate);
        this.ticking = false;
    }

    public void setOnTickHandler(Runnable onTickHandler) {
        if (onTickHandler != null) {
            this.tickHandler = onTickHandler;
            this.delegate = new Runnable() {
                public void run() {
                    if (BTimer.this.tickHandler != null) {
                        BTimer.this.handler.postDelayed(BTimer.this.delegate, BTimer.this.interval);
                        BTimer.this.tickHandler.run();
                    }
                }
            };
        }
    }
}
