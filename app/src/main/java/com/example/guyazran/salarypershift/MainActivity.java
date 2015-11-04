package com.example.guyazran.salarypershift;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;

import Finance.Currency;
import Finance.Money;
import Finance.Salary;
import SimpleTime.Time;

public class MainActivity extends AppCompatActivity {

    EditText txtSalary;
    Button btnStartTime, btnEndTime;
    TextView lblStartTime, lblEndTime, lblSalary, lblWorkTime, lblSalaryCurrency;
    Currency currency = Currency.ILS;

    Time startTime;
    Time endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSalary = (EditText) findViewById(R.id.txtSalary);
        btnStartTime = (Button) findViewById(R.id.btnStartTime);
        btnEndTime = (Button) findViewById(R.id.btnEndTime);
        lblStartTime = (TextView) findViewById(R.id.lblStartTime);
        lblEndTime = (TextView) findViewById(R.id.lblEndTime);
        lblSalary = (TextView) findViewById(R.id.lblSalary);
        lblWorkTime = (TextView) findViewById(R.id.lblWorkTime);

        lblSalaryCurrency = (TextView) findViewById(R.id.lblSalaryCurrency);
        lblSalaryCurrency.setText(currency.toString());

    }

    public void btnChooseStartTime(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        if (startTime == null){
            startTime = new Time();
        }
        newFragment.setFragment(startTime.getHour(), startTime.getMinutes(), new TimePickerFragment.onTimeChosenListener() {
            @Override
            public void setTime(int hour, int minute) {
                startTime.setHour(hour);
                startTime.setMinutes(minute);
                lblStartTime.setText(startTime.toString());
                clearSalary();

                if (endTime != null){
                    showHoursWorked();
                }
            }
        });
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void btnChooseEndTime(View view) {
        if (endTime == null){
            endTime = new Time();
        }
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setFragment(endTime.getHour(), endTime.getMinutes(), new TimePickerFragment.onTimeChosenListener() {
            @Override
            public void setTime(int hour, int minute) {
                endTime.setHour(hour);
                endTime.setMinutes(minute);
                lblEndTime.setText(endTime.toString());
                clearSalary();

                if (startTime != null){
                    showHoursWorked();
                }
            }
        });
        newFragment.show(getFragmentManager(), "timePicker");
    }

    private void showHoursWorked(){
        lblWorkTime.setText(startTime.timeDifference(endTime).toString());
    }

    private void clearSalary(){
        lblSalary.setText("");
    }

    public void btnCalculateSalary(View view) {
        if (startTime == null || endTime == null) {
            Toast.makeText(MainActivity.this, "נא להכניס שעת התחלה וסיום", Toast.LENGTH_SHORT).show();
            return;
        }

        Time timeWorked = startTime.timeDifference(endTime);
        double salaryPerHour;
        try {
            salaryPerHour = getSalaryPerHour();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "משכורת לא תקינה", Toast.LENGTH_LONG).show();
            return;
        }

        Salary salary = new Salary(new Money(salaryPerHour, Currency.ILS), timeWorked, null, null);

        lblSalary.setText(salary.getFinalPay().toString());
    }

    private double getSalaryPerHour() throws Exception{
        String salaryString = txtSalary.getText().toString();
        double salary;
        try {
            salary = Double.valueOf(salaryString);
        } catch (Exception e){
            throw e;
        }
        return salary;
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        onTimeChosenListener listener;
        int hour;
        int minute;

        public void setFragment(int hour, int minute, onTimeChosenListener listener){
            this.listener = listener;
            this.hour = hour;
            this.minute = minute;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (listener != null)
                listener.setTime(hourOfDay, minute);
        }

        public static interface onTimeChosenListener{
            public void setTime(int hour, int minute);
        }
    }


}
