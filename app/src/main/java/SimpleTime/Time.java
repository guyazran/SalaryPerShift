package SimpleTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by guyazran on 11/4/15.
 * Time is a class that represents a clock: its hours and its minutes
 */

public class Time {

    private int hour;
    private int minutes;

    public Time(){
        this(0, 0);
    }

    public Time(int hour, int minutes){
        setHour(hour);
        setMinutes(minutes);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        if (hour < 24 && hour >= 0) {
            this.hour = hour;
        }
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        if (minutes < 60 && minutes >= 0) {
            this.minutes = minutes;
        }
    }

    public Time timeDifference(Time time){
        return TimeMath.findTimeDifference(this, time);
    }

    public double getMinutesInHours(){
        return TimeMath.convertMinutesToHours(this.minutes);
    }

    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("00");
        return formatter.format(hour) + ":" + formatter.format(minutes);
    }
}
