package com.guyazran.salarypershift.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.LayoutDirection;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.guyazran.Finance.Money;

import com.guyazran.salarypershift.R;
import com.guyazran.SimpleTime.Clock;

public class CalculateSalaryActivity extends AppCompatActivity implements SimpleCalculateSalaryFragment.OnFragmentInteractionListener, AddOvertimeFragment.OnChangeMadeListener {

    public static final int SETTINGS_REQUEST_CODE = 1;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * An integer holding the value of the layout direction
     */
    private int layoutDirection;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_salary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Get SharedPreferences
        sharedPreferences = getSharedPreferences(SettingsActivity.APP_PREFERENCES, MODE_PRIVATE);

        //get layout direction (LTR/RTL)
        layoutDirection = getResources().getConfiguration().getLayoutDirection();

        int[] tabLayoutResources = {R.layout.tab_simple_calculate_salary, R.layout.tab_timer_calculate_salary};
        int[] RTLTabLayoutResources = {R.layout.tab_timer_calculate_salary, R.layout.tab_simple_calculate_salary};

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if (layoutDirection == LayoutDirection.RTL) {
            setTabLayouts(tabLayout, RTLTabLayoutResources);
        } else {
            setTabLayouts(tabLayout, tabLayoutResources);
        }

        //get preferred launch tab and set to ViewPager
        int page = sharedPreferences.getInt(SettingsActivity.PREFERRED_LAUNCH_TAB, layoutDirection == LayoutDirection.RTL ? 1 : 0);
        mViewPager.setCurrentItem(page);

        //set new preferred launch tab on page change
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SettingsActivity.PREFERRED_LAUNCH_TAB, position);
                editor.commit();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //start the app with the keyboard hidden
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private void setTabLayouts(TabLayout tabLayout, int[] resources){
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(resources[i]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calculate_salary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check if settings have changed
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public Clock OnCalculateSalaryWithOvertimeGetTime() {
        FragmentManager manager = getSupportFragmentManager();
        AddOvertimeFragment addOvertimeFragment = (AddOvertimeFragment) manager.findFragmentByTag(SimpleCalculateSalaryFragment.ADD_OVERTIME_FRAGMENT);
        if (addOvertimeFragment != null) {
            return addOvertimeFragment.getOverTimeWorked();
        }
        return null;
    }

    @Override
    public Money OnCalculateSalaryWithOvertimeGetSalary() {
        FragmentManager manager = getSupportFragmentManager();
        AddOvertimeFragment addOvertimeFragment = (AddOvertimeFragment) manager.findFragmentByTag(SimpleCalculateSalaryFragment.ADD_OVERTIME_FRAGMENT);
        if (addOvertimeFragment != null) {
            try {
                return addOvertimeFragment.getOvertimeSalary();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void onRegularTimeChanged(Clock timeWorked) {
        FragmentManager manager = getSupportFragmentManager();
        AddOvertimeFragment addOvertimeFragment = (AddOvertimeFragment) manager.findFragmentByTag(SimpleCalculateSalaryFragment.ADD_OVERTIME_FRAGMENT);
        if (addOvertimeFragment != null) {
            addOvertimeFragment.setRegularTimeWorked(timeWorked);
        }
    }

    @Override
    public void onTimeChanged() {
        //get an instance of the current page
        SimpleCalculateSalaryFragment simpleCalculateSalaryFragment = (SimpleCalculateSalaryFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
        if (simpleCalculateSalaryFragment != null) {
            simpleCalculateSalaryFragment.clearSalary();
        }
    }

    @Override
    public void onEnterPressed() {
        Fragment f = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
        if (f instanceof  SimpleCalculateSalaryFragment) {
            SimpleCalculateSalaryFragment simpleCalculateSalaryFragment = (SimpleCalculateSalaryFragment) f;
            if (simpleCalculateSalaryFragment != null) {
                simpleCalculateSalaryFragment.btnCalculateSalary(new View(this));
            }
        }
    }

    @Override
    public Clock onGetRegularTimeOnFragmentCreation() {
        Fragment f = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
        if (f instanceof  SimpleCalculateSalaryFragment) {
            SimpleCalculateSalaryFragment simpleCalculateSalaryFragment = (SimpleCalculateSalaryFragment) f;
            if (simpleCalculateSalaryFragment != null) {
                return simpleCalculateSalaryFragment.getOverallTime();
            }
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        TimerCalculateSalaryFragment timerFragment = ((TimerCalculateSalaryFragment) mSectionsPagerAdapter.getItem(layoutDirection == LayoutDirection.RTL ? 0 : 1));
        timerFragment.stopTimers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimerCalculateSalaryFragment timerFragment = ((TimerCalculateSalaryFragment) mSectionsPagerAdapter.getItem(layoutDirection == LayoutDirection.RTL ? 0 : 1));
        timerFragment.startTimers();
        timerFragment.updateAllTimers();
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //getItem is called to instantiate the fragment for the given page.
            //Return a PlaceholderFragment (defined as a static inner class below).

            //the viewpager doesn't change direction so the order of the fragments must be flipped
            if (layoutDirection == LayoutDirection.RTL) {
                switch (position) {
                    case 0:
                        return new TimerCalculateSalaryFragment();
                    case 1:
                        return new SimpleCalculateSalaryFragment();
                    default:
                        return new SimpleCalculateSalaryFragment();
                }
            }

            switch (position) {
                case 0:
                    return new SimpleCalculateSalaryFragment();
                case 1:
                    return new TimerCalculateSalaryFragment();
                default:
                    return new SimpleCalculateSalaryFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

    }
}