package com.guyazran.SimpleTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

/**
 * Created by guyazran on 11/11/15.
 */
public class Date implements Comparable<Date> {
    private boolean monthChanged = false;

    private int day;
    private int month;
    private int year;

    public Date(){
        this(1, 1, 1970);
    }

    public Date(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        if (day > 0 && day <= getNumberOfDaysInMonth(getMonth(), getYear())) {
            this.day = day;
        } else if (monthChanged){
            this.day = 1;
        }
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        if (month > 0 && month <= 12) {
            this.month = month;
            monthChanged = true;
            setDay(getDay());
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        if (getMonth() == 2){
            setDay(day);
        }
    }

    public static Date getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return new Date(day, month, year);
    }

    @Override
    public int compareTo(Date another) {
        //compare years
        if (this.getYear() > another.getYear())
            return 1;
        if (this.getYear() < another.getYear())
            return -1;

        //compare months if years are equal
        if (this.getMonth() > another.getMonth())
            return 1;
        if (this.getMonth() < another.getMonth())
            return -1;

        //compare days if months are also equal
        if (this.getDay() > another.getDay())
            return 1;
        if (this.getDay() < another.getDay())
            return -1;

        //return 0 (equal) if days are also equal;
        return 0;
    }

    /**
     * Returns the number of days in are there in a specific month of a specific year.
     * @param month The month that will have it's days counted.
     * @param year The year in which the month is in (used to account for leap years).
     * @return The number of days in the given month in the given year.
     */
    public static int getNumberOfDaysInMonth(int month, int year){
        switch (month){
            case 1:
                return 31;
            case 2:
                if (year % 4 == 0) {
                    return 29;
                } else {
                    return 28;
                }
            case 3:
                return 31;
            case 4:
                return 30;
            case 5:
                return 31;
            case 6:
                return 30;
            case 7:
                return 31;
            case 8:
                return 31;
            case 9:
                return 30;
            case 10:
                return 31;
            case 11:
                return 30;
            case 12:
                return 31;
        }
        return -1;
    }

    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("00");
        return formatter.format(day) + "/" + formatter.format(month) + "/" + formatter.format(year);
    }
}
