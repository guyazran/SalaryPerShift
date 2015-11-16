package com.guyazran.SimpleTime;

/**
 * Created by guyazran on 11/4/15.
 * TimeMath is a collection of methods that perform calculations between com.guyazran.SimpleTime.Clock instances
 */
public abstract class TimeMath {

    public static OverflowClock addTimes(Clock clock1, Clock clock2) {
        if (clock1 == null && clock2 == null){
            return null;
        }
        if (clock1 == null){
            return new OverflowClock(clock2);
        }
        if (clock2 == null){
            return new OverflowClock(clock1);
        }
        int sumOfHours = clock1.getHour() + clock2.getHour();
        int sumOfMinutes = clock1.getMinutes() + clock2.getMinutes();

        if (sumOfMinutes > 59) {
            sumOfHours++;
            sumOfMinutes -= 60;
        }
        return new OverflowClock(sumOfHours, sumOfMinutes);
    }

    /**
     * Calculates the time difference between two given clocks, while assuming that the first
     * parameter (startTime) is the earlier of the two.
     *
     * @param startTime The earlier time being compared.
     * @param endTime   The later time being compared.
     * @return A Clock containing the hour and minutes that represent the difference in time
     * between the two Clocks.
     */
    public static Clock findHourAndMinutesDifference(Clock startTime, Clock endTime) {
        if (startTime == null || endTime == null){
            return null;
        }
        int startHour = startTime.getHour();
        int endHour = endTime.getHour();

        int hourDifference;
        if (startHour <= endHour) {
            hourDifference = endHour - startHour;
        } else {
            hourDifference = (24 - startHour) + endHour;
        }

        int startMinute = startTime.getMinutes();
        int endMinute = endTime.getMinutes();

        int minuteDifference;
        if (startMinute <= endMinute) {
            minuteDifference = endMinute - startMinute;
        } else {
            minuteDifference = (60 - startMinute) + endMinute;
            hourDifference--;
        }

        return new Clock(hourDifference, minuteDifference);
    }

    public static double convertMinutesToHours(int minutes) {
        return minutes * (1.0 / 60);
    }

    /**
     * Calculates the number of days between the two dates.
     *
     * @param startDate The earlier date being compared.
     * @param endDate   The later date being compared.
     * @return An integer representing the number of days that must pass from startDate in order
     * to reach endDate. -1 is returned if startDate is later than endDate.
     */
    public static long findDayDifference(Date startDate, Date endDate) {
        long sumOfDays = 0;

        //check if startDate is earlier than endDate
        int compare = startDate.compareTo(endDate);
        if (compare > 0)
            return -1;
        if (compare == 0)
            return 0;

        //check if there is a year difference
        if (startDate.getYear() < endDate.getYear()) {
            for (int i = startDate.getYear(); i < endDate.getYear(); i++) {
                if (i % 4 == 0) {
                    sumOfDays += 366;
                } else {
                    sumOfDays += 365;
                }
            }
        }

        //check if there is a month difference
        if (startDate.getMonth() < endDate.getMonth()) {
            for (int i = startDate.getMonth(); i < endDate.getMonth(); i++) {
                sumOfDays += Date.getNumberOfDaysInMonth(i, endDate.getYear());
            }
        } else if (startDate.getMonth() > endDate.getMonth()) {
            for (int i = startDate.getMonth(); i > endDate.getMonth(); i--) {
                sumOfDays -= Date.getNumberOfDaysInMonth(i, endDate.getYear());
            }
        }


        //if startDate's day is larger than the last day of endDate's month, that day must change
        //to be the last day of that month in order to ensure a proper count.
        if (startDate.getDay() > Date.getNumberOfDaysInMonth(endDate.getMonth(), endDate.getYear())) {
            startDate = new Date(Date.getNumberOfDaysInMonth(endDate.getMonth(), endDate.getYear()), endDate.getMonth(), endDate.getYear());
        }

        //add the difference in days to the sum. if startDate is larger, there will be a subtraction
        sumOfDays += endDate.getDay() - startDate.getDay();

        return sumOfDays;
    }
}