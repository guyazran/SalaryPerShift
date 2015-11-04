package Finance;

import SimpleTime.Time;

/**
 * Created by guyazran on 11/4/15.
 * Salary is a class that contains the information about a workers pay.
 */
public class Salary {
    private Money payRate, overtimePayRate, finalPay;
    private Time timeWorked, overtimeWorked;
    private boolean finalPayCreated;

    public Salary(Money payRate, Time timeWorked, Money overtimePayRate, Time overtimeWorked) {
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

    public Time getTimeWorked() {
        return timeWorked;
    }

    public void setTimeWorked(Time timeWorked) {
        this.timeWorked = timeWorked;
    }

    public Time getOvertimeWorked() {
        return overtimeWorked;
    }

    public void setOvertimeWorked(Time overtimeWorked) {
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
