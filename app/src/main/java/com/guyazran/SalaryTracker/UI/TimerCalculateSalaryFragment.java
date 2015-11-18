package com.guyazran.SalaryTracker.UI;


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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.guyazran.Finance.Currency;
import com.guyazran.Finance.Money;
import com.guyazran.Finance.Salary;
import com.guyazran.SimpleTime.Clock;
import com.guyazran.SimpleTime.OverflowClock;
import com.guyazran.SimpleTime.UpdateTimersThread;
import com.guyazran.SalaryTracker.R;
import com.guyazran.SimpleTime.Timer;
import com.guyazran.SalaryTracker.HumanResources.Employee;
import com.guyazran.SalaryTracker.HumanResources.WorkTimer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A {@link Fragment} subclass.
 * This class is responsible for the UI presented when choosing the timer option in the
 * CalculateSalaryActivity.
 */
public class TimerCalculateSalaryFragment extends Fragment implements AddTimerDialogFragment.OnTimerAddedListener, AddTimerDialogFragment.OnExtrasAddedListener {

    public static final String ADD_TIMER_DIALOG_FRAGMENT = "Add Timer Dialog Fragment";
    public static final String TIMERS_JSON_FILE_NAME = "timers.json";

    private RecyclerView lstTimers;
    private TimerListAdapter timerListAdapter;

    private FloatingActionButton btnAddTimer;

    private ArrayList<Timer> timers;

    private Handler handler;

    private UpdateTimersThread updateTimersThread;

    private Currency currency;
    private SharedPreferences sharedPreferences;

    private BroadcastReceiver timeUpdatedReceiver;

    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_calculate_salary, container, false);

        lstTimers = (RecyclerView) view.findViewById(R.id.lstTimers);

        timers = new ArrayList<Timer>();

        sharedPreferences = getActivity().getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

        int currencyInt = sharedPreferences.getInt(SettingsActivity.PREFERRED_CURRENCY, 0);
        currency = Currency.values()[currencyInt];

        timeUpdatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timerListAdapter.notifyDataSetChanged();
            }
        };

        layoutManager = new LinearLayoutManager(getActivity());
        lstTimers.setLayoutManager(layoutManager);
        timerListAdapter = new TimerListAdapter(timers, timeUpdatedReceiver,getActivity());
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

        new Thread() {
            @Override
            public void run() {
                loadTimers();
                for (int i = 0; i < timers.size(); i++) {
                    Salary salary = ((WorkTimer) timers.get(i)).getSalary();
                    if (salary.getPayRate() != null) {
                        salary.getPayRate().setCurrency(currency);
                    }
                    if (salary.getOvertimePayRate() != null) {
                        salary.getOvertimePayRate().setCurrency(currency);
                    }
                }
            }
        }.start();


        return view;
    }


    private void loadTimers() {
        JSONArray timersJSONArray = new JSONArray();
        try {
            File file = new File(getActivity().getFilesDir().getAbsolutePath(), TIMERS_JSON_FILE_NAME);
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            char[] chars = new char[1024];
            int actuallyRead;
            StringBuilder readString = new StringBuilder("");
            while ((actuallyRead = inputStreamReader.read(chars)) > -1) {
                String s = new String(chars, 0, actuallyRead);
                readString.append(s);
            }
            timersJSONArray = new JSONArray(readString.toString());
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < timersJSONArray.length(); i++) {
                JSONObject workTimerAsJSON = timersJSONArray.getJSONObject(i);
                WorkTimer workTimer = WorkTimer.restoreFromJSONObject(workTimerAsJSON);
                timers.add(workTimer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        timerListAdapter.notifyDataSetChanged();
        if (timers.size() > 0) {
            startTimers();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveTimers();
        stopTimers();
    }

    private void saveTimers() {
        JSONArray timersJSONArray = new JSONArray();
        try {
            for (int i = 0; i < timers.size(); i++) {
                timersJSONArray.put(((WorkTimer) timers.get(i)).asJSONObject());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            File file = new File(getActivity().getFilesDir().getAbsolutePath(), TIMERS_JSON_FILE_NAME);
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(timersJSONArray.toString());
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTimer(String employeeName, Clock startTime, Clock overtimeStartTime, Money rate, Money overTimeRate) {
        Employee newEmployee = new Employee(employeeName);
        if (startTime == null) {
            startTime = Clock.getCurrentClock();
        }

        Salary salary = new Salary(rate, new OverflowClock(), overTimeRate, null);

        Timer newTimer = WorkTimer.startWorkerTimer(newEmployee, salary, overtimeStartTime, startTime);
        timers.add(0, newTimer);
        timerListAdapter.notifyItemInserted(0);
        layoutManager.scrollToPosition(0);

        if (timers.size() == 1) {
            //startTimers();
            getActivity().registerReceiver(timeUpdatedReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        }

        //hide keyboard after adding a new item to the list
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getFragmentManager().findFragmentByTag(ADD_TIMER_DIALOG_FRAGMENT).getView().getWindowToken(), 0);
    }

    public void startTimers() {
        if (timeUpdatedReceiver != null && timers.size() >= 1) {
            getActivity().registerReceiver(timeUpdatedReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        }
    }

    public void stopTimers() {
        if (timeUpdatedReceiver != null && timers.size() > 0) {
            getActivity().unregisterReceiver(timeUpdatedReceiver);
        }
    }

    public void updateAllTimers() {
        if (timerListAdapter != null) {
            timerListAdapter.notifyDataSetChanged();
        }
    }

    public BroadcastReceiver getReceiver() {
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
        if (fragment != null) {
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
        if (fragment != null) {
            return fragment.getOvertimeRate();
        }
        return null;
    }
}