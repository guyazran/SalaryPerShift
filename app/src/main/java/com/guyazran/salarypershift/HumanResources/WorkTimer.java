package com.guyazran.salarypershift.HumanResources;

import com.guyazran.Finance.Salary;
import com.guyazran.SimpleTime.Clock;
import com.guyazran.SimpleTime.Date;
import com.guyazran.SimpleTime.TimeMath;
import com.guyazran.SimpleTime.Timer;

/**
 * Created by guyazran on 11/14/15.
 */
public class WorkTimer extends Timer{
    private Employee employee;
    private Salary salary;
    private Clock overtimeStartTime;
    private Date overtimeStartDate;

    private WorkTimer(Employee employee, Salary salary, Clock overtimeStartTime) {
        super();
        this.employee = employee;
        this.salary = salary;
        this.overtimeStartTime = overtimeStartTime;
        overtimeStartDate = Date.getCurrentDate();
    }

    private WorkTimer(Employee employee, Salary salary, Clock overtimeStartTime, Clock earlierStartTime) {
        super(earlierStartTime);
        this.employee = employee;
        this.salary = salary;
        this.overtimeStartTime = overtimeStartTime;
        overtimeStartDate = Date.getCurrentDate();

    }



    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Salary getSalary() {
        return salary;
    }

    public void setSalary(Salary salary) {
        this.salary = salary;
    }

    public static WorkTimer startWorkerTimer(Employee employee, Salary salary, Clock overtimeStartTime){
        return new WorkTimer(employee, salary, overtimeStartTime);
    }

    public static WorkTimer startWorkerTimer(Employee employee, Salary salary, Clock overtimeStartTime, Clock earlierStartTime){
        return new WorkTimer(employee, salary, overtimeStartTime, earlierStartTime);
    }

    @Override
    public Clock getTimePassed() {
        if (!isOvertime()) {
            return super.getTimePassed();
        } else {
            return TimeMath.findHourAndMinutesDifference(getStartTime(), overtimeStartTime);
        }
    }

    public Clock getOvertimePassed(){
        if (isOvertime()){
            return TimeMath.findHourAndMinutesDifference(overtimeStartTime, Clock.getCurrentClock());
        } else {
            return null;
        }
    }

    private boolean isOvertime(){
        if (overtimeStartTime != null && overtimeStartDate != null) {
            if (overtimeStartDate.compareTo(Date.getCurrentDate()) == -1) {
                return true;
            }
            if (overtimeStartDate.compareTo(Date.getCurrentDate()) == 0 && overtimeStartTime.compareTo(Clock.getCurrentClock()) <= 0){
                return true;
            }
        }

        return false;
    }
}
