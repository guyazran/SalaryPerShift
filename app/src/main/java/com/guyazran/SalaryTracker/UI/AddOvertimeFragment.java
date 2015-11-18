package com.guyazran.SalaryTracker.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.guyazran.Finance.Currency;
import com.guyazran.Finance.Money;

import com.guyazran.SimpleTime.OverflowClock;
import com.guyazran.SalaryTracker.R;
import com.guyazran.SimpleTime.Clock;
import com.guyazran.SimpleTime.TimeMath;

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddOvertimeFragment extends Fragment {


    private OnChangeMadeListener mListener;

    private Button btnStartOvertime, btnEndOvertime;
    private TextView lblSalaryCurrency, lblStartOvertime, lblEndOvertime, lblOvertimeWorked, lblOverallTimeWorked;
    private EditText txtOvertimeSalary;
    private Currency currency;

    private Clock startTime;
    private Clock endTime;
    private Clock regularTimeWorked;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_overtime, container, false);

        mListener = (OnChangeMadeListener) getActivity();

        btnStartOvertime = (Button) view.findViewById(R.id.btnStartOvertime);
        btnStartOvertime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChooseStartTime(v);
            }
        });

        btnEndOvertime = (Button) view.findViewById(R.id.btnEndOvertime);
        btnEndOvertime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChooseEndTime(v);
            }
        });

        lblSalaryCurrency = (TextView) view.findViewById(R.id.lblSalaryCurrency);
        lblStartOvertime = (TextView) view.findViewById(R.id.lblStartOvertime);
        lblEndOvertime = (TextView) view.findViewById(R.id.lblEndOvertime);
        lblOvertimeWorked = (TextView) view.findViewById(R.id.lblOvertimeWorked);
        lblOverallTimeWorked = (TextView) view.findViewById(R.id.lblOverallTimeWorked);
        txtOvertimeSalary = (EditText) view.findViewById(R.id.txtOvertimeSalary);
        txtOvertimeSalary.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    mListener.onEnterPressed();
                }
                return false;
            }
        });

        //get shared preferences
        sharedPreferences = getActivity().getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

        //get currency from settings
        int currencyInt = sharedPreferences.getInt(SettingsActivity.PREFERRED_CURRENCY, 0);
        switch (currencyInt) {
            case 0:
                currency = Currency.USD;
                break;
            case 1:
                currency = Currency.EUR;
                break;
            case 2:
                currency = Currency.ILS;
                break;
            default:
                currency = Currency.USD;
        }

        lblSalaryCurrency.setText(currency.toString());

        regularTimeWorked = mListener.onGetRegularTimeOnFragmentCreation();

        return view;
    }

    public void btnChooseStartTime(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        int startHour, startMinutes;
        if (startTime == null) {
            Clock regularStartTime = mListener.onGetRegularEndTimeForOvertimeStartTime();
            if (regularStartTime != null){
                startHour = regularStartTime.getHour();
                startMinutes = regularStartTime.getMinutes();
            } else {
                startHour = 0;
                startMinutes = 0;
            }
        } else {
            startHour = startTime.getHour();
            startMinutes = startTime.getMinutes();
        }
        newFragment.setFragment(startHour, startMinutes, new TimePickerFragment.onTimeChosenListener() {
            @Override
            public void setTime(int hour, int minute) {
                if (startTime != null) {
                    startTime.setHour(hour);
                    startTime.setMinutes(minute);
                } else {
                    startTime = new Clock(hour, minute);
                }
                lblStartOvertime.setText(startTime.toString());
                mListener.onTimeChanged();

                if (endTime != null) {
                    showOvertimeWorked();
                }
                setOverallTimeWorked();
            }
        });
        newFragment.show(getActivity().getFragmentManager(), "timePicker");
    }

    public void btnChooseEndTime(View view) {
        int startHour, startMinutes;
        if (endTime == null) {
            startHour = 0;
            startMinutes = 0;
        } else {
            startHour = endTime.getHour();
            startMinutes = endTime.getMinutes();
        }
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setFragment(startHour, startMinutes, new TimePickerFragment.onTimeChosenListener() {
            @Override
            public void setTime(int hour, int minute) {
                if(endTime != null) {
                    endTime.setHour(hour);
                    endTime.setMinutes(minute);
                } else {
                    endTime = new Clock(hour, minute);
                }
                lblEndOvertime.setText(endTime.toString());
                mListener.onTimeChanged();

                if (startTime != null) {
                    showOvertimeWorked();
                }
                setOverallTimeWorked();
            }
        });
        newFragment.show(getActivity().getFragmentManager(), "timePicker");
    }

    private void showOvertimeWorked() {
        if (sharedPreferences.getInt(SettingsActivity.PREFERRED_OVERALL_TIME_DISPLAY_FORMAT, 0) == 0) {
            lblOvertimeWorked.setText(getOverTimeWorked().toString());
        } else {
            DecimalFormat df = new DecimalFormat("####0.00");
            lblOvertimeWorked.setText(df.format(getOverTimeWorked().getDecimalValue()));
        }
    }

    public OverflowClock getOverTimeWorked() {
        if (startTime != null && endTime != null) {
            return new OverflowClock(startTime.hourAndMinutesDifference(endTime));
        } else {
            return null;
        }
    }

    public Money getOvertimeSalary() throws Exception {
        String salaryString = txtOvertimeSalary.getText().toString();
        double salary;
        salary = Double.valueOf(salaryString);
        return new Money(salary, currency);
    }

    public void setRegularTimeWorked(Clock regularTimeWorked) {
        this.regularTimeWorked = regularTimeWorked;
        setOverallTimeWorked();
    }

    private void setOverallTimeWorked() {
        if (regularTimeWorked != null && startTime != null && endTime != null) {
            Clock overallTimeWorked = TimeMath.addTimes(regularTimeWorked, startTime.hourAndMinutesDifference(endTime));
            lblOverallTimeWorked.setText(overallTimeWorked.toString());
        }
    }

    public interface OnChangeMadeListener {
        void onTimeChanged();

        void onEnterPressed();

        Clock onGetRegularTimeOnFragmentCreation();

        Clock onGetRegularEndTimeForOvertimeStartTime();
    }
}
