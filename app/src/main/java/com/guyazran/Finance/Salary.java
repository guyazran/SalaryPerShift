package com.guyazran.Finance;

import com.guyazran.SimpleTime.Clock;

/**
 * Created by guyazran on 11/4/15.
 * Salary is a class that contains the information about a workers pay.
 */
public class Salary {
    private Money payRate, overtimePayRate, finalPay;
    private Clock timeWorked, overtimeWorked;
    private boolean finalPayCreated;

    public Salary(Money payRate, Clock timeWorked, Money overtimePayRate, Clock overtimeWorked) {
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
    }

    public Money getOvertimePayRate() {
        return overtimePayRate;
    }

    public void setOvertimePayRate(Money overtimePayRate) {
        this.overtimePayRate = overtimePayRate;
    }

    public Clock getTimeWorked() {
        return timeWorked;
    }

    public void setTimeWorked(Clock timeWorked) {
        this.timeWorked = timeWorked;
    }

    public Clock getOvertimeWorked() {
        return overtimeWorked;
    }

    public void setOvertimeWorked(Clock overtimeWorked) {
        this.overtimeWorked = overtimeWorked;
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
