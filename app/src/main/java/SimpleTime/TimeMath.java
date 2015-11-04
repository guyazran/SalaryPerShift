package SimpleTime;

/**
 * Created by guyazran on 11/4/15.
 * TimeMath is a collection of methods that perform calculations between SimpleTime.Time instances
 */
public abstract class TimeMath {

    public static Time findTimeDifference(Time startTime, Time endTime){
        int startHour = startTime.getHour();
        int endHour = endTime.getHour();

        int hourDifference;
        if (startHour <= endHour){
            hourDifference = endHour - startHour;
        } else {
            hourDifference = (24 - startHour) + endHour;
        }

        int startMinute = startTime.getMinutes();
        int endMinute = endTime.getMinutes();

        int minuteDifference;
        if (startMinute <= endMinute){
            minuteDifference = endMinute - startMinute;
        } else {
            minuteDifference = (60 - startMinute) + endMinute;
            hourDifference--;
        }

        return new Time(hourDifference, minuteDifference);
    }

    public static double convertMinutesToHours(int minutes){
        return minutes * (1.0/60);
    }
}
