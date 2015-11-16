package com.guyazran.salarypershift.UI;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import com.guyazran.Finance.Currency;
import com.guyazran.Finance.Money;
import com.guyazran.Finance.Salary;
import com.guyazran.SimpleTime.Clock;
import com.guyazran.SimpleTime.UpdateTimersThread;
import com.guyazran.salarypershift.R;
import com.guyazran.SimpleTime.Timer;
import com.guyazran.salarypershift.HumanResources.Employee;
import com.guyazran.salarypershift.HumanResources.WorkTimer;


/**
 * A {@link Fragment} subclass.
 * This class is responsible for the UI presented when choosing the timer option in the
 * CalculateSalaryActivity.
 */
public class TimerCalculateSalaryFragment extends Fragment implements AddTimerDialogFragment.OnTimerAddedListener, AddTimerDialogFragment.OnExtrasAddedListener {

    public static final String ADD_TIMER_DIALOG_FRAGMENT = "Add Timer Dialog Fragment";
    private RecyclerView lstTimers;
    private TimerListAdapter timerListAdapter;

    private FloatingActionButton btnAddTimer;

    private ArrayList<Timer> timers;

    private Handler handler;

    private UpdateTimersThread updateTimersThread;

    private Currency currency;
    private SharedPreferences sharedPreferences;

    private BroadcastReceiver timeUpdatedReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_calculate_salary, container, false);

        lstTimers = (RecyclerView) view.findViewById(R.id.lstTimers);

        timers = new ArrayList<Timer>();

        sharedPreferences = getActivity().getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

        int currencyInt = sharedPreferences.getInt(SettingsActivity.PREFERRED_CURRENCY, 0);
        currency = Currency.values()[currencyInt];

        lstTimers.setLayoutManager(new LinearLayoutManager(getActivity()));
        timerListAdapter = new TimerListAdapter(timers, getActivity());
        lstTimers.setAdapter(timerListAdapter);
        lstTimers.setItemAnimator(new DefaultItemAnimator());
        lstTimers.addItemDecoration(new DividerItemDecoration(getActivity()));

        btnAddTimer = (FloatingActionButton) view.findViewById(R.id.fab);
        btnAddTimer.setBackgroundTintList(ColorStateList.valueOf(0xff37474f));
        btnAddTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                AddTimerDialogFragment addTimerDialogFragment = new AddTimerDialogFragment();
                addTimerDialogFragment.setTimerAddedListener(TimerCalculateSalaryFragment.this);
                addTimerDialogFragment.setExtrasAddedListener(TimerCalculateSalaryFragment.this);
                addTimerDialogFragment.show(manager, ADD_TIMER_DIALOG_FRAGMENT);
            }
        });

        handler = new Handler();

        timeUpdatedReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                timerListAdapter.notifyDataSetChanged();
            }
        };

        return view;
    }

    public void addTimer(String employeeName, Clock startTime, Clock overtimeStartTime, Money rate, Money overTimeRate){
        Employee newEmployee = new Employee(employeeName);
        if (startTime == null){
            startTime = Clock.getCurrentClock();
        }

        Salary salary = new Salary(rate, new Clock(), overTimeRate, null);

        Timer newTimer = WorkTimer.startWorkerTimer(newEmployee, salary, overtimeStartTime, startTime);
        timers.add(0, newTimer);
        timerListAdapter.notifyItemInserted(0);

        if (timers.size() == 1){
            //startTimers();
            getActivity().registerReceiver(timeUpdatedReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        }

        //hide keyboard after adding a new item to the list
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getFragmentManager().findFragmentByTag(ADD_TIMER_DIALOG_FRAGMENT).getView().getWindowToken(), 0);
    }

    public void startTimers(){
        if (timeUpdatedReceiver != null && timers.size() >= 1) {
            getActivity().registerReceiver(timeUpdatedReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        }
    }

    public void stopTimers(){
        if (timeUpdatedReceiver != null){
            getActivity().unregisterReceiver(timeUpdatedReceiver);
        }
    }

    public void updateAllTimers(){
        if (timerListAdapter != null) {
            timerListAdapter.notifyDataSetChanged();
        }
    }

    public BroadcastReceiver getReceiver(){
        return timeUpdatedReceiver;
    }

    @Override
    public void onTimerAdded(String employeeName, Clock startTime, Clock overtimeStartTime, Money rate, Money overTimeRate) {
        addTimer(employeeName, startTime, overtimeStartTime, rate, overTimeRate);
    }

    @Override
    public Clock onCustomStartTimeAdded() {
        FragmentManager manager = getFragmentManager();
        AddTimerDialogFragment df = (AddTimerDialogFragment) manager.findFragmentByTag(ADD_TIMER_DIALOG_FRAGMENT);
        FragmentManager childManager = df.getChildFragmentManager();
        ChooseStartTimeForTimerFragment fragment = (ChooseStartTimeForTimerFragment) childManager.findFragmentByTag(AddTimerDialogFragment.CHOOSE_START_TIME_FRAGMENT);
        if (fragment != null){
            return fragment.getStartTime();
        }
        return null;
    }

    @Override
    public Clock onCustomOvertimeStartTimeAdded() {
        FragmentManager manager = getFragmentManager();
        AddTimerDialogFragment df = (AddTimerDialogFragment) manager.findFragmentByTag(ADD_TIMER_DIALOG_FRAGMENT);
        FragmentManager childManager = df.getChildFragmentManager();
        AddOvertimeForTimerFragment fragment = (AddOvertimeForTimerFragment) childManager.findFragmentByTag(AddTimerDialogFragment.ADD_OVERTIME_FOR_TIMER_FRAGMENT);
        if (fragment != null) {
            return fragment.getOvertimeStartTime();
        }
        return null;
    }

    @Override
    public Money onCustomOvertimeRateAdded() {
        FragmentManager manager = getFragmentManager();
        AddTimerDialogFragment df = (AddTimerDialogFragment) manager.findFragmentByTag(ADD_TIMER_DIALOG_FRAGMENT);
        FragmentManager childManager = df.getChildFragmentManager();
        AddOvertimeForTimerFragment fragment = (AddOvertimeForTimerFragment) childManager.findFragmentByTag(AddTimerDialogFragment.ADD_OVERTIME_FOR_TIMER_FRAGMENT);
        if (fragment != null){
            return fragment.getOvertimeRate();
        }
        return null;
    }
}