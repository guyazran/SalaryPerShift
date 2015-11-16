package com.guyazran.SimpleTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

/**
 * Created by guyazran on 11/4/15.
 * Clock is a class that represents a clock: its hours and its minutes
 */

public class Clock implements Comparable<Clock>{

    protected int hour;
    protected int minutes;

    public Clock(){
        this(0, 0);
    }

    public Clock(int hour, int minutes){
        setHour(hour);
        setMinutes(minutes);
    }

    public Clock(Clock newTime){
        this.setHour(newTime.getHour());
        this.setMinutes(newTime.getMinutes());
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        if (hour < 24 && hour >= 0)
            this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        if (minutes >= 0 && minutes < 60) {
            this.minutes = minutes;
        }
    }

    public Clock hourAndMinutesDifference(Clock clock){
        return TimeMath.findHourAndMinutesDifference(this, clock);
    }

    public double getMinutesInHours(){
        return TimeMath.convertMinutesToHours(this.minutes);
    }

    public void addTime(Clock timeToAdd){
        Clock newTime = TimeMath.addTimes(this, timeToAdd);
        this.setHour(newTime.getHour());
        this.setMinutes(newTime.getMinutes());
    }

    public static Clock getCurrentClock(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return new Clock(hour, minutes);
    }

    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("00");
        return formatter.format(hour) + ":" + formatter.format(minutes);
    }

    @Override
    public int compareTo(Clock another) {
        //check hours
        if (this.getHour() > another.getHour())
            return 1;
        if (this.getHour() < another.getHour())
            return -1;

        //check minutes if hours are equal
        if (this.getMinutes() > another.getMinutes())
            return 1;
        if (this.getMinutes() < another.getMinutes())
            return -1;

        //return 0 if hour and minutes are equal for both Clocks
        return 0;
    }
}
