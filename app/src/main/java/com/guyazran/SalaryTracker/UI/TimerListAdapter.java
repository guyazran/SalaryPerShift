package com.guyazran.SalaryTracker.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.guyazran.SimpleTime.TimeMath;
import com.guyazran.SimpleTime.TimerState;
import com.guyazran.SalaryTracker.R;
import com.guyazran.SimpleTime.OverflowClock;
import com.guyazran.SimpleTime.Timer;
import com.guyazran.SalaryTracker.HumanResources.WorkTimer;

public class TimerListAdapter extends RecyclerView.Adapter<TimerListAdapter.ViewHolder>{

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView lblWorkerName, lblTimerWorked, lblFinalSalary;

        public ImageButton btnDeleteTimer;

        public Button btnStopTimer;

        public View timerStateIndicator;

        public ViewHolder(View v) {
            super(v);
            timerStateIndicator = v.findViewById(R.id.timerStateIndicator);
            lblWorkerName = (TextView) v.findViewById(R.id.lblWorkerName);
            lblTimerWorked = (TextView) v.findViewById(R.id.lblTimerTimeWorked);
            lblFinalSalary = (TextView) v.findViewById(R.id.lblTimerFinalSalary);
            btnDeleteTimer = (ImageButton) v.findViewById(R.id.btnDeleteTimer);
            btnDeleteTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    timers.remove(position);
                    notifyItemRemoved(position);
                    if (timers.size() == 0){
                        context.unregisterReceiver(timeUpdateReceiver);
                    }
                }
            });

            btnStopTimer = (Button) v.findViewById(R.id.btnStopTimer);
            btnStopTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Timer timer = timers.get(getAdapterPosition());
                    if (timer.getState() == TimerState.RUNNING) {
                        timer.stopTimer();
                        btnStopTimer.setText(context.getString(R.string.still_in_shift_button));
                        timerStateIndicator.setBackgroundColor(Color.RED);
                    } else {
                        timer.resumeTimer();
                        btnStopTimer.setText(context.getString(R.string.stop_button));
                        timerStateIndicator.setBackgroundColor(Color.parseColor("#99CC00"));
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    ArrayList<Timer> timers;
    SharedPreferences sharedPreferences;
    Context context;

    BroadcastReceiver timeUpdateReceiver;

    public TimerListAdapter(ArrayList<Timer> timers, BroadcastReceiver timeUpdateReceiver,Context context){
        this.timers = timers;
        this.timeUpdateReceiver = timeUpdateReceiver;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_work_timer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        WorkTimer workTimer = (WorkTimer) timers.get(position);
        holder.lblWorkerName.setText(workTimer.getEmployee().getFirstName() + " " + workTimer.getEmployee().getLastName());
        holder.btnStopTimer.setText(workTimer.getState() == TimerState.RUNNING ? context.getString(R.string.stop_button) : context.getString(R.string.still_in_shift_button));
        holder.timerStateIndicator.setBackgroundColor(workTimer.getState() == TimerState.RUNNING ? Color.GREEN : Color.RED);

            //get overall time worked
            OverflowClock overallTimeWorked = TimeMath.addTimes(workTimer.getTimePassed(), workTimer.getOvertimePassed());
            if (sharedPreferences.getInt(SettingsActivity.PREFERRED_OVERALL_TIME_DISPLAY_FORMAT, 0) == 0) {
                holder.lblTimerWorked.setText(overallTimeWorked.toString());
            } else {
                DecimalFormat df = new DecimalFormat("####0.00");
                holder.lblTimerWorked.setText(df.format(overallTimeWorked.getDecimalValue()));
            }

            workTimer.getSalary().setTimeWorked(workTimer.getTimePassed());
            workTimer.getSalary().setOvertimeWorked(workTimer.getOvertimePassed());
            holder.lblFinalSalary.setText(workTimer.getSalary().getFinalPay().toString());

    }

    @Override
    public int getItemCount() {
        return timers.size();
    }
}
