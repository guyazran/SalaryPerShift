package com.guyazran.SimpleTime;

import java.util.ArrayList;

/**
 * Created by guyazran on 11/14/15.
 */
public class UpdateTimersThread extends Thread {

    ArrayList<Timer> timers;
    OnTimerUpdatedListener listener;

    boolean go = true;

    long updateFrequencyInMillis;

    public UpdateTimersThread(ArrayList<Timer> timers, long updateFrequencyInMillis,OnTimerUpdatedListener listener){
        this.timers = timers;
        this.listener = listener;
        this.updateFrequencyInMillis = updateFrequencyInMillis;
    }

    @Override
    public void run() {
        while (go) {
            try {
                Thread.sleep(updateFrequencyInMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < timers.size(); i++) {
                synchronized (timers) {
                    if (timers.get(i).getState() == TimerState.RUNNING) {
                        listener.onTimerUpdated(i);
                    }
                }
            }
        }
    }

    public void stopUpdatingTimers(){
        go = false;
    }

    public static interface OnTimerUpdatedListener {
        void onTimerUpdated(int position);
    }
}
