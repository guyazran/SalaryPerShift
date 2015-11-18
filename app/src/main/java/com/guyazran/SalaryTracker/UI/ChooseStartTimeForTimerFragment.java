package com.guyazran.SalaryTracker.UI;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.guyazran.SimpleTime.Clock;
import com.guyazran.SalaryTracker.R;

/**
 * Created by guyazran on 11/15/15.
 */
public class ChooseStartTimeForTimerFragment extends Fragment {

    private Button btnChooseStartTime;
    private TextView lblStartTime;

    private Clock startTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_start_time_for_timer, container, false);

        btnChooseStartTime = (Button) view.findViewById(R.id.btnAddTimerChooseStartTime);
        btnChooseStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                    }
                });
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
            }
        });

        lblStartTime = (TextView) view.findViewById(R.id.lblAddTimerStartTime);
        startTime = Clock.getCurrentClock();
        lblStartTime.setText(startTime.toString());

        return view;
    }

    public Clock getStartTime(){
        return startTime;
    }
}
