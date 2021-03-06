package com.seraph.ppschedule.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.seraph.ppschedule.R;
import com.seraph.ppschedule.activity.MainActivity;
import com.seraph.ppschedule.activity.ScheduleDetailActivity;
import com.seraph.ppschedule.bean.Schedule;
import com.seraph.ppschedule.dao.ScheduleDao;
import com.seraph.ppschedule.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    
    private AlarmManager am;
    private AlarmBinder binder = new AlarmBinder();

    public AlarmService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
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

        remindAllScheduleOfToady();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    public List<Schedule> getUndoListOfDate(int year, int month, int day) {
        List<Schedule> res = ScheduleDao.getInstance().findScheduleByDate(year, month, day);

        for(int i=0; i < res.size(); i++) {
            if(res.get(i).isFinish()) {
                res.remove(i);
            }
        }

        return res;
    }

    public void remindAllScheduleOfToady() {
        String remindTime = "08:00";
        Calendar calendar = Calendar.getInstance();
        //?????????????????????????????????
        long time = DateUtils.date2TimeStamp(String.format("%s-%s-%s %s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), remindTime),
                "yyyy-MM-dd HH:mm");

        Log.d(TAG, "remindAllScheduleOfToady: time=" + (calendar.getTimeInMillis() - time));
        if(calendar.getTimeInMillis() <= time) {
            //???????????????????????????????????????????????????????????????
            List<Schedule> res = getUndoListOfDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            Log.d(TAG, "remindAllScheduleOfToady: res=" + res.toString());
            if(res.size() != 0) {
                showNotification(AlarmService.this, res);
            }
        }

        //???????????????????????????8?????????Service????????????
        //??????????????????????????????????????????
        time = DateUtils.date2TimeStamp(String.format("%s-%s-%s %s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH) + 1, remindTime),
                "yyyy-MM-dd HH:mm");

        long t = calendar.getTimeInMillis() - time;
        Intent i = new Intent(this, AlarmService.class);
        PendingIntent pi = PendingIntent.getService(this, Integer.MAX_VALUE, i, 0);
        am.set(AlarmManager.RTC_WAKEUP, t, pi);
    }

    /**
     * ?????????????????????????????????????????????
     * @param context
     * @param list
     */
    public void showNotification(Context context, List<Schedule> list) {
        Log.d(TAG, "showNotification: ");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default";
            String channelName = "????????????";
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        StringBuilder sb = new StringBuilder();
        for(int i=0; i < list.size(); i++) {
            sb.append(list.get(i).getTitle());
            if(i != list.size() -1) {
                sb.append("\n");
            }
        }
        String affair = sb.toString();

        Notification notification = new NotificationCompat.Builder(context, "default")
                .setContentTitle("????????????")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(affair))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(MainActivity.ID_NOTIFICATION_ALL, notification);
    }

    public class AlarmBinder extends Binder {
        /**
         * ??????????????????
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
         * ??????????????????
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