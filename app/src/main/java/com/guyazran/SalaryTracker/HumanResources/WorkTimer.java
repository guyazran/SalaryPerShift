package com.guyazran.SalaryTracker.HumanResources;

import com.guyazran.Finance.Currency;
import com.guyazran.Finance.Money;
import com.guyazran.Finance.Salary;
import com.guyazran.SimpleTime.Clock;
import com.guyazran.SimpleTime.Date;
import com.guyazran.SimpleTime.OverflowClock;
import com.guyazran.SimpleTime.TimeMath;
import com.guyazran.SimpleTime.Timer;
import com.guyazran.SimpleTime.TimerState;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guyazran on 11/14/15.
 */
public class WorkTimer extends Timer {
    public static final String START_TIME_HOUR = "startTimeHour";
    public static final String START_TIME_MINUTES = "startTimeMinutes";
    public static final String START_DATE_DAY = "startDateDay";
    public static final String START_DATE_MONTH = "startDateMonth";
    public static final String START_DATE_YEAR = "startDateYear";
    public static final String STATE = "state";
    public static final String END_TIME_HOUR = "endTimeHour";
    public static final String END_TIME_MINUTES = "endTimeMinutes";
    public static final String END_DATE_DAY = "endDateDay";
    public static final String END_DATE_MONTH = "endDateMonth";
    public static final String END_DATE_YEAR = "endDateYear";
    public static final String TAG = "tag";
    public static final String EMPLOYEE_FIRST_NAME = "employeeFirstName";
    public static final String EMPLOYEE_LAST_NAME = "employeeLastName";
    public static final String PAY_RATE_AMOUNT = "payRateAmount";
    public static final String TIME_WORKED_HOUR = "timeWorkedHour";
    public static final String TIME_WORKED_MINUTES = "timeWorkedMinutes";
    public static final String OVERTIME_PAY_RATE_AMOUNT = "overtimePayRateAmount";
    public static final String OVERTIME_WORKED_HOUR = "overtimeWorkedHour";
    public static final String OVERTIME_WORKED_MINUTES = "overtimeWorkedMinutes";
    public static final String OVERTIME_START_TIME_HOUR = "overtimeStartTimeHour";
    public static final String OVERTIME_START_TIME_MINUTES = "overtimeStartTimeMinutes";
    public static final String OVERTIME_START_DATE_DAY = "overtimeStartDateDay";
    public static final String OVERTIME_START_DATE_MONTH = "overtimeStartDateMonth";
    public static final String OVERTIME_START_DATE_YEAR = "overtimeStartDateYear";
    public static final String PAY_RATE_CURRENCY = "payRateCurrency";
    public static final String OVERTIME_PAY_RATE_CURRENCY = "overtimePayRateCurrency";
    private Employee employee;
    private Salary salary;
    private Clock overtimeStartTime;
    private Date overtimeStartDate;

    private WorkTimer(Employee employee, Salary salary, Clock overtimeStartTime) {
        super();
        this.employee = employee;
        this.salary = salary;
        this.overtimeStartTime = overtimeStartTime;
        this.overtimeStartDate = Date.getCurrentDate();
    }

    private WorkTimer(Employee employee, Salary salary, Clock overtimeStartTime, Clock earlierStartTime) {
        super(earlierStartTime);
        this.employee = employee;
        this.salary = salary;
        this.overtimeStartTime = overtimeStartTime;
        overtimeStartDate = Date.getCurrentDate();
    }

    private WorkTimer(Employee employee, Salary salary, Clock overtimeStartTime, Clock earlierStartTime, Date earlierStartDate) {
        super(earlierStartTime, earlierStartDate);
        this.employee = employee;
        this.salary = salary;
        this.overtimeStartTime = overtimeStartTime;
        this.overtimeStartDate = Date.getCurrentDate();
    }

    private WorkTimer(Employee employee, Salary salary, Clock overtimeStartTime, Date overtimeStartDate, Timer timer) {
        super(timer);
        this.employee = employee;
        this.salary = salary;
        this.overtimeStartTime = overtimeStartTime;
        this.overtimeStartDate = overtimeStartDate;
    }

    public static WorkTimer restoreWorkTimer(Clock startTime, Clock endTime, TimerState state, String tag, Date startDate, Date endDate, Employee employee, Salary salary, Clock overtimeStartTime, Date overtimeStartDate) {
        Timer timer = Timer.restoreTimer(startTime, endTime, state, tag, startDate, endDate);
        return new WorkTimer(employee, salary, overtimeStartTime, overtimeStartDate, timer);
    }

    public JSONObject asJSONObject() throws JSONException {
        JSONObject workTimerAsJSON = new JSONObject();

        //timer values
        //startTime
        if (getStartTime() != null) {
            workTimerAsJSON.put(START_TIME_HOUR, getStartTime().getHour());
            workTimerAsJSON.put(START_TIME_MINUTES, getStartTime().getMinutes());
        }
        //startDate
        if (getStartDate() != null) {
            workTimerAsJSON.put(START_DATE_DAY, getStartDate().getDay());
            workTimerAsJSON.put(START_DATE_MONTH, getStartDate().getMonth());
            workTimerAsJSON.put(START_DATE_YEAR, getStartDate().getYear());
        }
        //state
        workTimerAsJSON.put(STATE, getState().name());
        //endTime
        if (getEndTime() != null) {
            workTimerAsJSON.put(END_TIME_HOUR, getEndTime().getHour());
            workTimerAsJSON.put(END_TIME_MINUTES, getEndTime().getMinutes());
        }
        //endDate
        if (getEndDate() != null) {
            workTimerAsJSON.put(END_DATE_DAY, getEndDate().getDay());
            workTimerAsJSON.put(END_DATE_MONTH, getEndDate().getMonth());
            workTimerAsJSON.put(END_DATE_YEAR, getEndDate().getYear());
        }
        //Tag
        if (getTag() != null) {
            workTimerAsJSON.put(TAG, getTag());
        }

        //workTimer values
        //employee
        if (getEmployee() != null) {
            if (employee.getFirstName() != null) {
                workTimerAsJSON.put(EMPLOYEE_FIRST_NAME, employee.getFirstName());
            }
            if (employee.getLastName() != null) {
                workTimerAsJSON.put(EMPLOYEE_LAST_NAME, employee.getLastName());
            }
        }
        //salary
        if (salary != null) {
            if (salary.getPayRate() != null) {
                workTimerAsJSON.put(PAY_RATE_AMOUNT, salary.getPayRate().getAmount());
                workTimerAsJSON.put(PAY_RATE_CURRENCY, salary.getPayRate().getCurrency().name());
            }
            if (salary.getTimeWorked() != null) {
                workTimerAsJSON.put(TIME_WORKED_HOUR, salary.getTimeWorked().getHour());
                workTimerAsJSON.put(TIME_WORKED_MINUTES, salary.getTimeWorked().getMinutes());
            }
            if (salary.getOvertimePayRate() != null) {
                workTimerAsJSON.put(OVERTIME_PAY_RATE_AMOUNT, salary.getOvertimePayRate().getAmount());
                workTimerAsJSON.put(OVERTIME_PAY_RATE_CURRENCY, salary.getOvertimePayRate().getCurrency().name());
            }
            if (salary.getOvertimeWorked() != null) {
                workTimerAsJSON.put(OVERTIME_WORKED_HOUR, salary.getOvertimeWorked().getHour());
                workTimerAsJSON.put(OVERTIME_WORKED_MINUTES, salary.getOvertimeWorked().getMinutes());
            }
        }
        //overtimeStartTime
        if (getOvertimeStartTime() != null) {
            workTimerAsJSON.put(OVERTIME_START_TIME_HOUR, getOvertimeStartTime().getHour());
            workTimerAsJSON.put(OVERTIME_START_TIME_MINUTES, getOvertimeStartTime().getMinutes());
        }
        //overtimeStartDate
        if (getOvertimeStartDate() != null) {
            workTimerAsJSON.put(OVERTIME_START_DATE_DAY, getOvertimeStartDate().getDay());
            workTimerAsJSON.put(OVERTIME_START_DATE_MONTH, getOvertimeStartDate().getMonth());
            workTimerAsJSON.put(OVERTIME_START_DATE_YEAR, getOvertimeStartDate().getYear());
        }

        return workTimerAsJSON;
    }

    public static WorkTimer restoreFromJSONObject(JSONObject workTimerAsJSON) {


        //startTime
        Clock startTime;
        try {
            int hour = workTimerAsJSON.getInt(START_TIME_HOUR);
            int minutes = workTimerAsJSON.getInt(START_TIME_MINUTES);
            startTime = new Clock(hour, minutes);
        }catch (JSONException e){
            startTime = null;
        }

        //startDate
        Date startDate;
        try {
            int day = workTimerAsJSON.getInt(START_DATE_DAY);
            int month = workTimerAsJSON.getInt(START_DATE_MONTH);
            int year = workTimerAsJSON.getInt(START_DATE_YEAR);
            startDate = new Date(day, month, year);
        } catch (JSONException e) {
            startDate = null;
        }


        //state
        TimerState state;
        try {
            state = TimerState.valueOf(workTimerAsJSON.getString(STATE));
        } catch (JSONException e) {
            state = TimerState.STOPPED;
        }
        //endTime
        Clock endTime;
        try {
            int hour = workTimerAsJSON.getInt(END_TIME_HOUR);
            int minutes = workTimerAsJSON.getInt(END_TIME_MINUTES);
            endTime = new Clock(hour, minutes);
        } catch (JSONException e) {
            endTime = null;
        }


        //endDate
        Date endDate;
        try {
            int day = workTimerAsJSON.getInt(END_DATE_DAY);
            int month = workTimerAsJSON.getInt(END_DATE_MONTH);
            int year = workTimerAsJSON.getInt(END_DATE_YEAR);
            endDate = new Date(day, month, year);
        } catch (JSONException e) {
            endDate = null;
        }


        //Tag
        String tag;
        try {
            tag = workTimerAsJSON.getString(TAG);
        } catch (JSONException e) {
            tag = null;
        }


        //workTimer values
        //employee
        Employee employee;
        try {
            String firstName = workTimerAsJSON.getString(EMPLOYEE_FIRST_NAME);
            String lastName = workTimerAsJSON.getString(EMPLOYEE_LAST_NAME);
            employee = new Employee(firstName, lastName);
        } catch (JSONException e) {
            employee = null;
        }

        //salary
        Salary salary;
        //regular work
        Money payRate;
        OverflowClock timeWorked;
        try {
            double payRateAmount = workTimerAsJSON.getDouble(PAY_RATE_AMOUNT);
            Currency payRateCurrency = Currency.valueOf(workTimerAsJSON.getString(PAY_RATE_CURRENCY));
            int hoursWorked = workTimerAsJSON.getInt(TIME_WORKED_HOUR);
            int minutesWorked = workTimerAsJSON.getInt(TIME_WORKED_MINUTES);
            payRate = new Money(payRateAmount, payRateCurrency);
            timeWorked = new OverflowClock(hoursWorked, minutesWorked);
        } catch (JSONException e) {
            payRate = null;
            timeWorked = null;
        }
        //overtime work
        Money overTimePayRate;
        try{
            double overtimePayRateAmount = workTimerAsJSON.getDouble(OVERTIME_PAY_RATE_AMOUNT);
            Currency overtimePayRateCurrency = Currency.valueOf(workTimerAsJSON.getString(OVERTIME_PAY_RATE_CURRENCY));
            overTimePayRate = new Money(overtimePayRateAmount, overtimePayRateCurrency);
        } catch (JSONException e){
            overTimePayRate = null;
        }
        OverflowClock overTimeWorked;
        try {
            int overtimeHoursWorked = workTimerAsJSON.getInt(OVERTIME_WORKED_HOUR);
            int overtimeMinutesWorked = workTimerAsJSON.getInt(OVERTIME_WORKED_MINUTES);
            overTimeWorked = new OverflowClock(overtimeHoursWorked, overtimeMinutesWorked);
        }catch (JSONException e){
            overTimeWorked = null;
        }
        salary = new Salary(payRate, timeWorked, overTimePayRate, overTimeWorked);

        //overtimeStartTime
        Clock overtimeStartTime;
        try {
            int hour = workTimerAsJSON.getInt(OVERTIME_START_TIME_HOUR);
            int minutes = workTimerAsJSON.getInt(OVERTIME_START_TIME_MINUTES);
            overtimeStartTime = new Clock(hour, minutes);
        } catch (JSONException e) {
            overtimeStartTime = null;
        }

        //overtimeStartDate
        Date overtimeStartDate;
        try {
            int day =workTimerAsJSON.getInt(OVERTIME_START_DATE_DAY);
            int month = workTimerAsJSON.getInt(OVERTIME_START_DATE_MONTH);
            int year = workTimerAsJSON.getInt(OVERTIME_START_DATE_YEAR);
            overtimeStartDate = new Date(day, month, year);
        } catch (JSONException e) {
            overtimeStartDate = null;
        }


        return restoreWorkTimer(startTime, endTime, state, tag, startDate, endDate, employee, salary, overtimeStartTime, overtimeStartDate);
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

    public Clock getOvertimeStartTime() {
        return overtimeStartTime;
    }

    public void setOvertimeStartTime(Clock overtimeStartTime) {
        this.overtimeStartTime = overtimeStartTime;
    }

    public Date getOvertimeStartDate() {
        return overtimeStartDate;
    }

    public void setOvertimeStartDate(Date overtimeStartDate) {
        this.overtimeStartDate = overtimeStartDate;
    }

    public static WorkTimer startWorkerTimer(Employee employee, Salary salary, Clock overtimeStartTime) {
        return new WorkTimer(employee, salary, overtimeStartTime);
    }

    public static WorkTimer startWorkerTimer(Employee employee, Salary salary, Clock overtimeStartTime, Clock earlierStartTime) {
        return new WorkTimer(employee, salary, overtimeStartTime, earlierStartTime);
    }

    @Override
    public OverflowClock getTimePassed() {
        if (!isOvertime()) {
            return super.getTimePassed();
        } else {
            OverflowClock timePassed = new OverflowClock(TimeMath.findHourAndMinutesDifference(getStartTime(), overtimeStartTime));
            int daysPassed = (int) TimeMath.findDayDifference(getStartDate(), getOvertimeStartDate());
            if (getStartTime().compareTo(overtimeStartTime) == 1){
                daysPassed--;
            }
            timePassed = TimeMath.addTimes(timePassed, new OverflowClock(24 * daysPassed, 0));
            return timePassed;
        }
    }

    public OverflowClock getOvertimePassed() {
        if (isOvertime()) {
            OverflowClock timePassed = new OverflowClock(TimeMath.findHourAndMinutesDifference(overtimeStartTime, Clock.getCurrentClock()));
            int daysPassed = 0;
            if (getState() == TimerState.RUNNING) {
                daysPassed = (int) TimeMath.findDayDifference(overtimeStartDate, Date.getCurrentDate());
                if (overtimeStartTime.compareTo(Clock.getCurrentClock()) == 1) {
                    daysPassed--;
                }
            } else if (getState() == TimerState.STOPPED && getEndTime() != null && getEndDate() != null){
                daysPassed = (int) TimeMath.findDayDifference(overtimeStartDate, getEndDate());
                if (overtimeStartTime.compareTo(getEndTime()) == 1){
                    daysPassed--;
                }
            }
            timePassed = TimeMath.addTimes(timePassed, new OverflowClock(24 * daysPassed, 0));
            return timePassed;
        } else {
            return null;
        }
    }

    private boolean isOvertime() {
        if (overtimeStartTime != null && overtimeStartDate != null) {
            if (overtimeStartDate.compareTo(Date.getCurrentDate()) == -1) {
                return true;
            }
            if (overtimeStartDate.compareTo(Date.getCurrentDate()) == 0 && overtimeStartTime.compareTo(Clock.getCurrentClock()) <= 0) {
                return true;
            }
        }

        return false;
    }
}
