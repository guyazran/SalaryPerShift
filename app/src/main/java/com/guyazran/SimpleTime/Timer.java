package com.guyazran.SimpleTime;

/**
 * Created by guyazran on 11/11/15.
 * Timer is a class that measures the amount of time that has passed from the moment it is
 * started until the moment it is stopped.
 */
public class Timer {

    private Clock startTime;
    private Date startDate;
    private TimerState state;
    private Clock endTime;
    private Date endDate;
    private String tag;


    protected Timer(){
        this(Clock.getCurrentClock());
    }

    protected Timer(Clock startTime){
        this(startTime, Date.getCurrentDate());

    }

    protected Timer(Clock startTime, Date startDate){
        this(startTime, startDate, TimerState.RUNNING, null, null, null);
    }

    private Timer(Clock startTime, Date startDate, TimerState state, Clock endTime, Date endDate, String tag){
        this.setStartTime(startTime);
        this.startDate = startDate;
        this.state = state;
        this.endTime = endTime;
        this.endDate = endDate;
        this.tag = tag;
    }

    protected Timer(Timer timer){
        this(timer.getStartTime(), timer.getStartDate(), timer.getState(), timer.getEndTime(), timer.getEndDate(), timer.getTag());
    }

    protected static Timer restoreTimer(Clock startTime, Clock endTime, TimerState state, String tag, Date startDate, Date endDate) {
        Timer timer = new Timer(startTime, startDate);
        timer.endTime = endTime;
        timer.state = state;
        timer.tag = tag;
        timer.endDate = endDate;
        return timer;
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
     * @param earlierTime A Clock that represents the start time of the timer. If this clock is later
     *                    than the current timer, the Timer will begin from the current time.
     * @return the created timer
     */
    public static Timer startTimerFromEarlierTime(Clock earlierTime){
        if (earlierTime.compareTo(Clock.getCurrentClock()) == 1){
            earlierTime = Clock.getCurrentClock();
        }
        return new Timer(earlierTime);
    }

    public void stopTimer(){
        state = TimerState.STOPPED;
        endTime = Clock.getCurrentClock();
        endDate = Date.getCurrentDate();
    }

    public void resumeTimer(){
        state = TimerState.RUNNING;
        endTime = null;
        endDate = null;
    }

    private void setStartTime(Clock startTime){
        this.startTime = new Clock(startTime);
    }

    public Clock getStartTime() {
        return new Clock(startTime);
    }

    public Clock getEndTime() {
        if (endTime != null) {
            return new Clock(endTime);
        }
        return null;
    }

    public TimerState getState() {
        return state;
    }

    /**
     * Calculates the number of 24 hour intervals that have passed since the starting of the Timer
     * @return A long value indicating the number of days passed
     */
    public long getDaysPassed() {
        Date currentDate = Date.getCurrentDate();

        long daysPassed;
        if (state == TimerState.RUNNING && endDate != null) {
            daysPassed = TimeMath.findDayDifference(startDate, endDate);
        } else {
            daysPassed = TimeMath.findDayDifference(startDate, currentDate);
        }

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

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public OverflowClock getTimePassed(){
        if (endTime != null && endDate != null && state == TimerState.STOPPED){
            OverflowClock timePassed = new OverflowClock(startTime.hourAndMinutesDifference(endTime));
            int daysPassed = (int) getDaysPassed();
            if (startTime.compareTo(endTime) == 1){
                daysPassed--;
            }
            timePassed = TimeMath.addTimes(timePassed, new OverflowClock(24 * daysPassed, 0));
            return timePassed;
        } else {
            Clock currentTime = Clock.getCurrentClock();
            OverflowClock timePassed = new OverflowClock(startTime.hourAndMinutesDifference(currentTime));
            int daysPassed = (int) getDaysPassed();
            if (startTime.compareTo(currentTime) == 1){
                daysPassed--;
            }
            timePassed = TimeMath.addTimes(timePassed, new OverflowClock(24 * daysPassed, 0));
            return timePassed;
        }
    }
}

