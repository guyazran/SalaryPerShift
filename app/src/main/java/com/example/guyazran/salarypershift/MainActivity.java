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
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    EditText txtSalary;
    Button btnStartTime;
    Button btnEndTime;
    TextView lblStartTime;
    TextView lblEndTime;
    TextView lblSalary;

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

        startTime = new Time();
        endTime = new Time();


    }


    public void btnChooseStartTime(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setFragment(startTime.getHour(), startTime.getMinute(), new TimePickerFragment.onTimeChosenListener() {
            @Override
            public void setTime(int hour, int minute) {
                NumberFormat formatter = new DecimalFormat("00");
                lblStartTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
                startTime.hour = hour;
                startTime.minute = minute;
                clearSalary();
            }
        });
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void btnChooseEndTime(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setFragment(endTime.getHour(), endTime.getMinute(),new TimePickerFragment.onTimeChosenListener() {
            @Override
            public void setTime(int hour, int minute) {
                NumberFormat formatter = new DecimalFormat("00");
                lblEndTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
                endTime.hour = hour;
                endTime.minute = minute;
                clearSalary();
            }
        });
        newFragment.show(getFragmentManager(), "timePicker");
    }

    private void clearSalary(){
        lblSalary.setText("");
    }

    public void btnCalculateSalary(View view) {
        if (lblStartTime.getText().toString().equals("") || lblEndTime.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "נא להכניס שעת התחלה וסיום", Toast.LENGTH_SHORT).show();
            return;
        }

        int startHour = startTime.getHour();
        int endHour = endTime.getHour();

        int hourDifference;
        if (startHour <= endHour){
            hourDifference = endHour - startHour;
        } else {
            hourDifference = (24 - startHour) + endHour;
        }

        int startMinute = startTime.getMinute();
        int endMinute = endTime.getMinute();

        int minuteDifference;
        if (startMinute <= endMinute){
            minuteDifference = endMinute - startMinute;
        } else {
            minuteDifference = (60 - startMinute) + endMinute;
            hourDifference--;
        }

        double minutesInHours = minuteDifference * (1.0/60);
        double hoursWorked = hourDifference + minutesInHours;

        double salaryPerHour;
        try {
            salaryPerHour = getSalaryPerHour();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "משכורת לא תקינה", Toast.LENGTH_LONG).show();
            return;
        }

        DecimalFormat df = new DecimalFormat("####0.00");
        String salary = df.format(salaryPerHour * hoursWorked) + "₪";

        lblSalary.setText(salary);
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

    class Time{
        private int hour = 0;
        private int minute = 0;

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }
    }
}
