package com.guyazran.salarypershift.UI;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.guyazran.salarypershift.R;

import com.guyazran.Finance.Currency;

public class SettingsActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "App Prefferences";

    public static final String PREFERRED_CURRENCY = "Preferred Currency";
    public static final String OVERTIME_ON = "Overtime On";
    public static final String PREFERRED_OVERALL_TIME_DISPLAY_FORMAT = "Preferred Overall Time Display Format";
    public static final String PREFERRED_LAUNCH_TAB = "Preferred Launch Tab";
    public static final String PREFERRED_TIMER_USE_CURRENT_START_TIME = "Preferred Timer Use Current Start Time";
    public static final String PREFERRED_TIMER_OVERTIME_ON = "Preferred Timer Overtime On";

    public static final int USD = 0;
    public static final int EUR = 1;
    public static final int ILS = 2;


    SharedPreferences sharedPreferences;

    Spinner spnSelectCurrency;
    Spinner spnSelectOverallTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set back button in action bar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        //set values for currency selection
        spnSelectCurrency = (Spinner) findViewById(R.id.spnSelectCurrency);
        Currency[] currencies = Currency.values();
        String[] currencyNames = new String[currencies.length];
        for (int i = 0; i < currencies.length; i++) {
            currencyNames[i] = currencies[i].name();
        }
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, currencyNames);
        spnSelectCurrency.setAdapter(currencyAdapter);
        spnSelectCurrency.setSelection(sharedPreferences.getInt(PREFERRED_CURRENCY, 0));

        //set item select listener for currency selection
        spnSelectCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(PREFERRED_CURRENCY, position);
                editor.commit();

                setResult(RESULT_OK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //set values for overall time format selection
        spnSelectOverallTimeFormat = (Spinner) findViewById(R.id.spnSelectOverallTimeDisplayFormat);
        ArrayAdapter<String> timeFormatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.overall_time_display_formats));
        spnSelectOverallTimeFormat.setAdapter(timeFormatAdapter);
        spnSelectOverallTimeFormat.setSelection(sharedPreferences.getInt(PREFERRED_OVERALL_TIME_DISPLAY_FORMAT, 0));

        spnSelectOverallTimeFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(PREFERRED_OVERALL_TIME_DISPLAY_FORMAT, position);
                editor.commit();

                setResult(RESULT_OK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
