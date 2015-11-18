package com.guyazran.SalaryTracker.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.guyazran.SimpleTime.Clock;
import com.guyazran.SalaryTracker.R;

/**
 * Created by guyazran on 11/15/15.
 */
public class AddOvertimeForTimerFragment extends Fragment {

    private EditText txtOvertimeRate;
    private TextView lblOvertimeRateCurrency, lblOvertimeStartTime;
    private Button btnChooseOvertimeStartTime;

    private Clock overtimeStartTime;

    private SharedPreferences sharedPreferences;
    private Currency currency;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_overtime_for_timer, container, false);

        sharedPreferences = getActivity().getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

        txtOvertimeRate = (EditText) view.findViewById(R.id.txtAddTimerOvertimeRate);
        lblOvertimeRateCurrency = (TextView) view.findViewById(R.id.lblAddTimerOvertimeRateCurrency);
        lblOvertimeStartTime = (TextView) view.findViewById(R.id.lblAddTimerOvertimeStartTime);
        btnChooseOvertimeStartTime = (Button) view.findViewById(R.id.btnAddTimerChooseOvertimeStartTime);

        btnChooseOvertimeStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment newFragment = new TimePickerFragment();
                if (overtimeStartTime == null){
                    overtimeStartTime = new Clock();
                }
                newFragment.setFragment(overtimeStartTime.getHour(), overtimeStartTime.getMinutes(), new TimePickerFragment.onTimeChosenListener() {
                    @Override
                    public void setTime(int hour, int minute) {
                        overtimeStartTime.setHour(hour);
                        overtimeStartTime.setMinutes(minute);
                        lblOvertimeStartTime.setText(overtimeStartTime.toString());

                    }
                });
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
            }
        });

        overtimeStartTime = Clock.getCurrentClock();
        lblOvertimeStartTime.setText(overtimeStartTime.toString());

        //get currency for label
        int currencyInt = sharedPreferences.getInt(SettingsActivity.PREFERRED_CURRENCY, 0);
        currency = Currency.values()[currencyInt];
        lblOvertimeRateCurrency.setText(currency.toString());

        txtOvertimeRate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    ((AddTimerDialogFragment)getParentFragment()).startTimer();
                }
                return false;
            }
        });

        return view;
    }

    public Clock getOvertimeStartTime(){
        return overtimeStartTime;
    }

    public Money getOvertimeRate(){
        String overtimeRateString = txtOvertimeRate.getText().toString();

        double amount;
        try {
            amount = Double.valueOf(overtimeRateString);
        } catch (Exception e){
            return null;
        }
        return new Money(amount, currency);
    }
}
