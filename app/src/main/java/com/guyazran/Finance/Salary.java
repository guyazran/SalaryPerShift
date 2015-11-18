package com.guyazran.Finance;

import com.guyazran.SimpleTime.Clock;
import com.guyazran.SimpleTime.OverflowClock;

/**
 * Created by guyazran on 11/4/15.
 * Salary is a class that contains the information about a workers pay.
 */
public class Salary {
    private Money payRate, overtimePayRate, finalPay;
    private OverflowClock timeWorked, overtimeWorked;
    private boolean finalPayCreated;

    public Salary(Money payRate, OverflowClock timeWorked, Money overtimePayRate, OverflowClock overtimeWorked) {
        this.payRate = payRate;
        this.timeWorked = timeWorked;
        this.overtimePayRate = overtimePayRate;
        this.overtimeWorked = overtimeWorked;
        this.finalPayCreated = false;
    }

    public Money getPayRate() {
        return payRate;
    }

    public void setPayRate(Money payRate) {
        this.payRate = payRate;
        this.finalPayCreated = false;
    }

    public Money getOvertimePayRate() {
        return overtimePayRate;
    }

    public void setOvertimePayRate(Money overtimePayRate) {
        this.overtimePayRate = overtimePayRate;
        this.finalPayCreated = false;
    }

    public OverflowClock getTimeWorked() {
        return timeWorked;
    }

    public void setTimeWorked(OverflowClock timeWorked) {
        this.timeWorked = timeWorked;
        this.finalPayCreated = false;
    }

    public Clock getOvertimeWorked() {
        return overtimeWorked;
    }

    public void setOvertimeWorked(OverflowClock overtimeWorked) {
        this.overtimeWorked = overtimeWorked;
        this.finalPayCreated = false;
    }

    public Money getFinalPay(){
        double hoursWorked = getTimeWorked().getHour() + getTimeWorked().getMinutesInHours();
        if (!finalPayCreated) {
            finalPay = new Money(getPayRate().getAmount() * hoursWorked, getPayRate().getCurrency());
            finalPayCreated = true;
        } else {
            finalPay.setAmount(getPayRate().getAmount() * hoursWorked);
        }
        if (overtimeWorked != null && overtimePayRate != null) {
            double overtimeHoursWorked = getOvertimeWorked().getHour() + getOvertimeWorked().getMinutesInHours();
            finalPay.setAmount(finalPay.getAmount() + getOvertimePayRate().getAmount() * overtimeHoursWorked);
        }
        return finalPay;
    }

}
