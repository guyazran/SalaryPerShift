package com.guyazran.SimpleTime;

public enum TimerState {
    STOPPED, RUNNING;

    @Override
    public String toString() {
        switch (this){
            case RUNNING:
                return "running";
            case STOPPED:
                return "done";
        }

        return super.toString();
    }
}
