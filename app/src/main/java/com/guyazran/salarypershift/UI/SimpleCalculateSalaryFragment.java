package com.guyazran.salarypershift.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import com.guyazran.Finance.Currency;
import com.guyazran.Finance.Money;
import com.guyazran.Finance.Salary;

import com.guyazran.salarypershift.R;
import com.guyazran.SimpleTime.Clock;
import com.guyazran.SimpleTime.OverflowClock;


/**
 * A {@link Fragment} subclass.
 * This Fragment Represents the UI for a simple Salary Calculation
 */
public class SimpleCalculateSalaryFragment extends Fragment {

    public static final String ADD_OVERTIME_FRAGMENT = "Add Overtime Fragment";

    private OnFragmentInteractionListener mListener;

    private EditText txtSalary;
    private Button btnStartTime, btnEndTime, btnCalculateSalary;
    private CheckBox chkAddOvertime;
    private TextView lblStartTime, lblEndTime, lblSalary, lblWorkTime, lblSalaryCurrency;
    private Currency currency;

    private Clock startTime;
    private Clock endTime;

    SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_simple_calculate_salary, container, false);

        txtSalary = (EditText) view.findViewById(R.id.txtSalary);
        txtSalary.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    btnCalculateSalary(btnCalculateSalary);
                }
                return false;
            }
        });

        btnStartTime = (Button) view.findViewById(R.id.btnStartTime);
        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChooseStartTime(v);
            }
        });

        chkAddOvertime = (CheckBox) view.findViewById(R.id.chkAddOvertime);
        chkAddOvertime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            FragmentManager manager = getActivity().getSupportFragmentManager();

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AddOvertimeFragment addOvertimeFragment = (AddOvertimeFragment) manager.findFragmentByTag(ADD_OVERTIME_FRAGMENT);
                    if (addOvertimeFragment != null){
                        FragmentTransaction attachOvertimeTransaction = manager.beginTransaction();
                        attachOvertimeTransaction.attach(addOvertimeFragment);
                        attachOvertimeTransaction.commit();
                    } else {
                        addOvertimeFragment = new AddOvertimeFragment();
                        FragmentTransaction addOvertimeTransaction = manager.beginTransaction();
                        addOvertimeTransaction.add(R.id.overtimeContainer, addOvertimeFragment, ADD_OVERTIME_FRAGMENT);
                        addOvertimeTransaction.commit();
                    }

                } else {
                    AddOvertimeFragment addOvertimeFragment = (AddOvertimeFragment) manager.findFragmentByTag(ADD_OVERTIME_FRAGMENT);
                    FragmentTransaction removeOvertimeTransaction = manager.beginTransaction();
                    if (addOvertimeFragment != null){
                        removeOvertimeTransaction.detach(addOvertimeFragment);
                        removeOvertimeTransaction.commit();
                    }
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SettingsActivity.OVERTIME_ON, isChecked);
                editor.commit();
            }
        });

        btnEndTime = (Button) view.findViewById(R.id.btnEndTime);
        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChooseEndTime(v);
            }
        });

        btnCalculateSalary = (Button) view.findViewById(R.id.btnCalculateSalary);
        btnCalculateSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCalculateSalary(v);
            }
        });

        lblStartTime = (TextView) view.findViewById(R.id.lblStartTime);
        lblEndTime = (TextView) view.findViewById(R.id.lblEndTime);
        lblSalary = (TextView) view.findViewById(R.id.lblSalary);
        lblWorkTime = (TextView) view.findViewById(R.id.lblWorkTime);
        lblSalaryCurrency = (TextView) view.findViewById(R.id.lblSalaryCurrency);

        //get shared preferences
        sharedPreferences = getActivity().getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

        //get currency from settings
        int currencyInt = sharedPreferences.getInt(SettingsActivity.PREFERRED_CURRENCY, 0);
        currency = Currency.values()[currencyInt];

        lblSalaryCurrency.setText(currency.toString());

        //get whether overtime is turned on or off
        boolean overtimeOn = sharedPreferences.getBoolean(SettingsActivity.OVERTIME_ON, false);
        chkAddOvertime.setChecked(overtimeOn);

        mListener = (OnFragmentInteractionListener) getActivity();

        return view;
    }

    public void btnChooseStartTime(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        if (startTime == null){
            startTime = new Clock();
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
        newFragment.show(getActivity().getFragmentManager(), "timePicker");
    }

    public void btnChooseEndTime(View view) {
        if (endTime == null){
            endTime = new Clock();
        }
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setFragment(endTime.getHour(), endTime.getMinutes(), new TimePickerFragment.onTimeChosenListener() {
            @Override
            public void setTime(int hour, int minute) {
                endTime.setHour(hour);
                endTime.setMinutes(minute);
                lblEndTime.setText(endTime.toString());
                clearSalary();

                if (startTime != null) {
                    showHoursWorked();
                }
            }
        });
        newFragment.show(getActivity().getFragmentManager(), "timePicker");
    }

    private void showHoursWorked(){
        OverflowClock timeWorked = getOverallTime();

        if (sharedPreferences.getInt(SettingsActivity.PREFERRED_OVERALL_TIME_DISPLAY_FORMAT, 0) == 0) {
            lblWorkTime.setText(timeWorked.toString());
        } else {
            DecimalFormat df = new DecimalFormat("####0.00");
            lblWorkTime.setText(df.format(timeWorked.getDecimalValue()));
        }
        mListener.onRegularTimeChanged(timeWorked);
    }

    protected OverflowClock getOverallTime(){
        if (startTime != null & endTime != null) {

            OverflowClock overallTime = new OverflowClock(startTime.hourAndMinutesDifference(endTime));

            return overallTime;
        }
        return null;
    }

    public void clearSalary(){
        if (lblSalary != null) {
            lblSalary.setText("");
        }
    }

    public void btnCalculateSalary(View view) {
        if (startTime == null || endTime == null) {
            Toast.makeText(getActivity(), R.string.enter_start_and_end_hour_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        Clock timeWorked = startTime.hourAndMinutesDifference(endTime);
        double salaryPerHour;
        try {
            salaryPerHour = getSalaryPerHour();
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.invalid_salary_input_toast, Toast.LENGTH_LONG).show();
            return;
        }

        Clock overtimeWorked = null;
        Money overtimeSalary = null;
        if (mListener != null) {
            overtimeWorked = mListener.OnCalculateSalaryWithOvertimeGetTime();
            overtimeSalary = mListener.OnCalculateSalaryWithOvertimeGetSalary();
            if (chkAddOvertime.isChecked() == true){
                if (overtimeWorked == null){
                    Toast.makeText(getActivity(), R.string.enter_overtime_start_and_end_hour, Toast.LENGTH_LONG).show();
                    return;
                }
                if (overtimeSalary == null){
                    Toast.makeText(getActivity(), R.string.invalid_overtime_salary_input_toast, Toast.LENGTH_LONG).show();
                }
            }
        }

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Salary salary = new Salary(new Money(salaryPerHour, Currency.ILS), timeWorked, overtimeSalary, overtimeWorked);

        lblSalary.setText(salary.getFinalPay().toString());
    }

    private double getSalaryPerHour(){
        String salaryString = txtSalary.getText().toString();
        double salary;
        salary = Double.valueOf(salaryString);
        return salary;
    }

    public interface OnFragmentInteractionListener {
        Clock OnCalculateSalaryWithOvertimeGetTime();
        Money OnCalculateSalaryWithOvertimeGetSalary();
        void onRegularTimeChanged(Clock timeWorked);
    }

}
