package com.guyazran.SimpleTime;

import java.util.Calendar;

/**
 * Created by guyazran on 11/11/15.
 * Timer is a class that measures the amount of time that has passed from the moment it is
 * started until the moment it is stopped.
 */
public class Timer {

    private Clock startTime;
    private Clock endTime;
    private TimerState state;
    private String tag;
    private Date startDate;
    private Date endDate;

    protected Timer(){
        this(Clock.getCurrentClock());
    }

    protected Timer(Clock startTime){
        this.setStartTime(startTime);
        this.state = TimerState.RUNNING;
        startDate = Date.getCurrentDate();

        Calendar calendar = Calendar.getInstance();

    }

    /**
     * Creates a new Timer and starts Running it from the current time
     * @return the created timer
     */
    public static Timer startTimer(){
        return new Timer();
    }

    /**
     * creates a timer and starts Running it from a specified time. the time will be considered as
     * earlier than the current time
     * @param earlierTime
     * @return the created timer
     */
    public static Timer startTimerFromEarlierTime(Clock earlierTime){
        return new Timer(earlierTime);
    }

    public void stopTimer(){
        state = TimerState.DONE;
    }

    public void resumeTimer(){
        state = TimerState.RUNNING;
    }

    private void setStartTime(Clock startTime){
        this.startTime = new Clock(startTime);
    }

    public Clock getStartTime() {
        return new Clock(startTime);
    }

    public Clock getEndTime() {
        return new Clock(endTime);
    }

    public TimerState getState() {
        return state;
    }

    public long getDaysPassed() {
        Date currentDate = Date.getCurrentDate();
        long daysPassed = TimeMath.findDayDifference(startDate, currentDate);

        if (startTime.compareTo(Clock.getCurrentClock()) == 1){
            daysPassed--;
        }

        return daysPassed;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Clock getTimePassed(){
        if (endTime != null && endDate != null){
            return startTime.hourAndMinutesDifference(endTime);
        } else {
            return startTime.hourAndMinutesDifference(Clock.getCurrentClock());
        }
    }
}

