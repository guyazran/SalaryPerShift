package com.guyazran.SalaryTracker.UI;

import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.guyazran.Finance.Currency;
import com.guyazran.Finance.Money;
import com.guyazran.SimpleTime.Clock;
import com.guyazran.SalaryTracker.R;

/**
 * Created by guyazran on 11/14/15.
 */
public class AddTimerDialogFragment extends DialogFragment {

    public static final String CHOOSE_START_TIME_FRAGMENT = "Choose Start Time Fragment";
    public static final String ADD_OVERTIME_FOR_TIMER_FRAGMENT = "Add Overtime For Timer Fragment";

    private EditText txtEmployeeName, txtRate;
    private Button btnConfirm, btnCancel;
    private TextView lblRateCurrency;
    private CheckBox chkAddOvertime, chkUseCurrentHour;

    private Currency currency;

    private SharedPreferences sharedPreferences;

    private OnTimerAddedListener timerAddedListener;
    private OnExtrasAddedListener extrasAddedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_timer_dialog, container, false);

        sharedPreferences = getActivity().getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

        txtEmployeeName = (EditText) view.findViewById(R.id.txtAddTimerEmployeeName);
        txtRate = (EditText) view.findViewById(R.id.txtAddTimerRate);


        btnConfirm = (Button) view.findViewById(R.id.btnAddTimerConfirm);
        btnCancel = (Button) view.findViewById(R.id.btnAddTimerCancel);

        lblRateCurrency = (TextView) view.findViewById(R.id.lblAddTimerRateCurrency);

        chkUseCurrentHour = (CheckBox) view.findViewById(R.id.chkAddTimerUseCurrentHour);
        chkAddOvertime = (CheckBox) view.findViewById(R.id.chkAddTimerAddOvertime);

        //get currency for label
        int currencyInt = sharedPreferences.getInt(SettingsActivity.PREFERRED_CURRENCY, 0);
        currency = Currency.values()[currencyInt];
        lblRateCurrency.setText(currency.toString());

        chkUseCurrentHour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FragmentManager manager = getChildFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                ChooseStartTimeForTimerFragment chooseStartTimeFragment = (ChooseStartTimeForTimerFragment) manager.findFragmentByTag(CHOOSE_START_TIME_FRAGMENT);
                if (isChecked) {
                    if (chooseStartTimeFragment != null) {
                        transaction.detach(chooseStartTimeFragment);
                        transaction.commit();
                    }
                } else {
                    if (chooseStartTimeFragment != null) {
                        transaction.attach(chooseStartTimeFragment);
                        transaction.commit();
                    } else {
                        chooseStartTimeFragment = new ChooseStartTimeForTimerFragment();
                        transaction.add(R.id.chooseStartTimeFragmentContainer, chooseStartTimeFragment, CHOOSE_START_TIME_FRAGMENT);
                        transaction.commit();
                    }
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SettingsActivity.PREFERRED_TIMER_USE_CURRENT_START_TIME, isChecked);
                editor.commit();
            }
        });
        chkUseCurrentHour.setChecked(sharedPreferences.getBoolean(SettingsActivity.PREFERRED_TIMER_USE_CURRENT_START_TIME, true));

        chkAddOvertime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FragmentManager manager = getChildFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                AddOvertimeForTimerFragment addOvertimeFragment = (AddOvertimeForTimerFragment) manager.findFragmentByTag(ADD_OVERTIME_FOR_TIMER_FRAGMENT);
                if (isChecked) {
                    if (addOvertimeFragment != null) {
                        transaction.attach(addOvertimeFragment);
                        transaction.commit();
                    } else {
                        addOvertimeFragment = new AddOvertimeForTimerFragment();
                        transaction.add(R.id.addOvertimeFragmentContainer, addOvertimeFragment, ADD_OVERTIME_FOR_TIMER_FRAGMENT);
                        transaction.commit();
                    }
                } else {
                    if (addOvertimeFragment != null) {
                        transaction.detach(addOvertimeFragment);
                        transaction.commit();
                    }
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SettingsActivity.PREFERRED_TIMER_OVERTIME_ON, isChecked);
                editor.commit();
            }
        });
        chkAddOvertime.setChecked(sharedPreferences.getBoolean(SettingsActivity.PREFERRED_TIMER_OVERTIME_ON, false));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        txtRate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    startTimer();
                }
                return false;
            }
        });

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(getString(R.string.add_timer_dialog_title));

        return view;
    }

    public void startTimer(){
        String name = txtEmployeeName.getText().toString();

        Clock startTime;
        if (chkUseCurrentHour.isChecked()){
            startTime = Clock.getCurrentClock();
        } else {
            startTime = extrasAddedListener.onCustomStartTimeAdded();
            if (startTime.compareTo(Clock.getCurrentClock()) == 1){
                Toast.makeText(getActivity(), R.string.invalid_timer_start_time_toast, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Clock overtimeStartTime;
        Money overtimeRate;
        if (chkAddOvertime.isChecked()){
            overtimeRate = extrasAddedListener.onCustomOvertimeRateAdded();
            overtimeStartTime = extrasAddedListener.onCustomOvertimeStartTimeAdded();
            if (overtimeRate == null){
                Toast.makeText(getActivity(), R.string.invalid_overtime_salary_input_toast, Toast.LENGTH_LONG).show();
                return;
            }
            if (overtimeStartTime.compareTo(startTime) <= 0){
                Toast.makeText(getActivity(), R.string.invalid_timer_overtime_start_time_toast, Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            overtimeStartTime = null;
            overtimeRate = null;
        }

        Money rate = null;
        String rateString = txtRate.getText().toString();
        try {
            double amount = Double.valueOf(rateString);
            rate = new Money(amount, currency);
        } catch (Exception e){
            Toast.makeText(getActivity(), R.string.invalid_salary_input_toast, Toast.LENGTH_LONG).show();
            return;
        }

        timerAddedListener.onTimerAdded(name, startTime, overtimeStartTime, rate, overtimeRate);
        dismiss();
    }

    public void setTimerAddedListener(OnTimerAddedListener timerAddedListener) {
        this.timerAddedListener = timerAddedListener;
    }

    public void setExtrasAddedListener(OnExtrasAddedListener extrasAddedListener){
        this.extrasAddedListener = extrasAddedListener;
    }

    public static interface OnTimerAddedListener {
        void onTimerAdded(String employeeName, Clock startTime, Clock overtimeStartTime, Money rate, Money overTimeRate);
    }

    public static interface OnExtrasAddedListener{
        Clock onCustomStartTimeAdded();
        Clock onCustomOvertimeStartTimeAdded();
        Money onCustomOvertimeRateAdded();
    }
}
