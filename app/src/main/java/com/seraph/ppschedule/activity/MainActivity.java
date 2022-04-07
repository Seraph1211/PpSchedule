package com.seraph.ppschedule.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.seraph.ppschedule.R;
import com.seraph.ppschedule.bean.Schedule;
import com.seraph.ppschedule.dao.ScheduleDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ScheduleDao dao = new ScheduleDao();
    List<Schedule> scheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void add(View view) {
        dao.addSchedule("TITLE", "DESC", Calendar.getInstance());
    }

    public void show(View view) {
        scheduleList = dao.findAllSchedule();

        if(scheduleList.size() == 0) {
            Toast.makeText(MainActivity.this, "show(): ScheduleList is null !", Toast.LENGTH_SHORT).show();
        }

        Iterator<Schedule> iterator = scheduleList.iterator();
        while(iterator.hasNext()) {
            Schedule schedule = iterator.next();
            Log.d(TAG, "[id=" + schedule.getId() + ", title=" + schedule.getTitle()
                    + ", time=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(schedule.getDate().getTime()) + "]");
        }
    }

    public void delete(View view) {
        if(scheduleList == null || scheduleList.size() <= 0) {
            Toast.makeText(MainActivity.this, "delete(): ScheduleList is null !", Toast.LENGTH_SHORT).show();
            return ;
        }

        dao.deleteSchedule(scheduleList.size());
    }
}