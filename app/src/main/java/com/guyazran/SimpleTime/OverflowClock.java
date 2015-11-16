package com.guyazran.SimpleTime;

/**
 * Created by guyazran on 11/11/15.
 * OverflowClock is a subclass of Clock that is not restricted to the 24 hours of a traditional
 * Clock. In addition, minutes can be added freely, with no 59 minute restriction, and will be
 * converted into hours that are added to the hour field.
 *
 *This class should be used for accumulating time in an hour:minute format.
 */
public class OverflowClock extends Clock {

    public OverflowClock(){
        super();
    }

    public OverflowClock(int hour, int minutes){
        super(hour, minutes);
    }

    public OverflowClock(Clock clock){
        super(clock.getHour(), clock.getMinutes());
    }

    /**
     * Changes the hour field of the OverflowClock to the given hour. The clock is not limited to
     * 24 hours.
     * @param hour The hour we wish to set the OverflowClock to.
     */
    @Override
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * Changes the minutes field of the OverflowClock to the given minutes. A minutes parameter
     * that is larger or smaller than a Clock's restrictions (0 - 59 minutes) will affect the hour
     * field accordingly and the minutes field will remain within the restrictions. No data is
     * lost.
     * @param minutes The minutes we wish to set for the OverflowClock.
     */
    @Override
    public void setMinutes(int minutes) {
        if (minutes > 59){
            setHour(getHour() + 1);
            minutes -= 60;
            setMinutes(minutes);
        } else if (minutes < 0){
            setHour(getHour() - 1);
            minutes += 60;
            setMinutes(minutes);
        } else {
            this.minutes = minutes;
        }
    }

    public double getDecimalValue(){
        return getHour() + (getMinutes()/60.0);
    }
}
