package com.guyazran.salarypershift.UI;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

/**
 * Created by guyazran on 11/10/15.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    onTimeChosenListener listener;
    int hour;
    int minute;

    public void setFragment(int hour, int minute, onTimeChosenListener listener) {
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

    public interface onTimeChosenListener {
        void setTime(int hour, int minute);
    }
}
