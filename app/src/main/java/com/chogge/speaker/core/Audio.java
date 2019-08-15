package com.chogge.speaker.core;

import android.media.AudioTrack;
import android.util.Log;

public class Audio implements Runnable {
    public static final int SAWTOOTH = 2;
    public static final int SINE = 0;
    public static final int SQUARE = 1;
    private AudioTrack audioTrack;
    public double frequency = 20.0d;
    public double level = 0.0d;
    public boolean mute;
    public Thread thread;
    public int waveCount = 0;
    public int waveform = 1;

    public void start() {
        this.thread = new Thread(this, "Audio");
        this.thread.start();
    }

    public void stop() {
        Thread t = this.thread;
        this.thread = null;
        while (t != null && t.isAlive()) {
            Thread.yield();
        }
    }

    public void run() {
        processAudio();
    }

    public void processAudio() {
        int rate = AudioTrack.getNativeOutputSampleRate(3);
        int minSize = AudioTrack.getMinBufferSize(rate, 4, 2);
        int[] iArr = new int[6];
        int size = 0;
        for (int s : new int[]{1024, 2048, 4096, 8192, 16384, 32768}) {
            if (s > minSize) {
                size = s;
                break;
            }
        }
        double K = 6.283185307179586d / ((double) rate);
        this.audioTrack = new AudioTrack(3, rate, 4, 2, minSize, 1);
        if (this.audioTrack != null) {
            if (this.audioTrack.getState() != 1) {
                this.audioTrack.release();
                return;
            }
            this.audioTrack.play();
            short[] buffer = new short[size];
            double f = this.frequency;
            double l = 0.0d;
            double q = 0.0d;
            while (this.thread != null) {
                if (this.waveCount > 0) {
                    this.waveCount--;
                    for (int i = 0; i < buffer.length; i++) {
                        f += (this.frequency - f) / 4096.0d;
                        l += (((this.mute ? 0.0d : 2.0d) * 16384.0d) - l) / 4096.0d;
                        q += q < 3.141592653589793d ? f * K : (f * K) - 6.283185307179586d;
                        switch (this.waveform) {
                            case 0:
                                buffer[i] = (short) ((int) Math.round(Math.sin(q) * l));
                                break;
                            case 1:
                                buffer[i] = (short) ((int) (q > 0.0d ? l : -l));
                                break;
                            case 2:
                                buffer[i] = (short) ((int) Math.round((q / 3.141592653589793d) * l));
                                break;
                            default:
                                break;
                        }
                    }
                    Log.e("------wangxiao-----", "buffer length = " + buffer);
                    this.audioTrack.write(buffer, 0, buffer.length);
                }
            }
            this.audioTrack.stop();
            this.audioTrack.release();
        }
    }
}
