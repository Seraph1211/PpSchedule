package com.seraph.ppschedule.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.seraph.ppschedule.activity.MainActivity;
import com.seraph.ppschedule.activity.ScheduleDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    
    private AlarmManager am;
    private AlarmBinder binder = new AlarmBinder();

    public AlarmService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "onBind: ");
        return binder;
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }


    public class AlarmBinder extends Binder {
        /**
         * 添加提醒任务
         * @param id
         * @param time
         */
        public void addAlarm(long id, long time) {
            Log.d(TAG, "addAlarm: ");
            Intent intent = new Intent();
            intent.setAction(MainActivity.ACTION_ALARM_NOTIFICATION);
            intent.putExtra(ScheduleDetailActivity.SCHEDULE_ID, id);
            PendingIntent pi = PendingIntent.getBroadcast(AlarmService.this, (int) id, intent, 0);
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }

        /**
         * 取消提醒任务
         * @param id
         */
        public void cancelAlarm(long id) {
            Log.d(TAG, "cancelAlarm: ");
            Intent intent = new Intent();
            intent.setAction(MainActivity.ACTION_ALARM_NOTIFICATION);
            intent.putExtra(ScheduleDetailActivity.SCHEDULE_ID, id);
            PendingIntent pi = PendingIntent.getBroadcast(AlarmService.this, (int) id, intent, 0);
            am.cancel(pi);
        }

    }
}