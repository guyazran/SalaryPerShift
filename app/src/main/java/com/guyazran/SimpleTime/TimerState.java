package com.guyazran.SimpleTime;

public enum TimerState {
    RUNNING, DONE;

    @Override
    public String toString() {
        switch (this){
            case RUNNING:
                return "running";
            case DONE:
                return "done";
        }

        return super.toString();
    }
}
