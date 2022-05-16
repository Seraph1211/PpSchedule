package com.seraph.ppschedule.activity;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seraph.ppschedule.R;
import com.seraph.ppschedule.bean.Schedule;
import com.seraph.ppschedule.dao.ScheduleDao;
import com.seraph.ppschedule.fragment.EventSetFragment;
import com.seraph.ppschedule.fragment.ScheduleFragment;
import com.seraph.ppschedule.service.AlarmService;
import com.seraph.ppschedule.utils.StatusBarUtils;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    public static final String ACTION_ALARM_NOTIFICATION = "action.alarm.notification";
    public static final int ID_NOTIFICATION_ALL = 1;
    public static final int ID_NOTIFICATION_SINGLE = 2;

    private DrawerLayout mDrawer;
    private ImageButton btnMenu;

    //菜单options数据控件
    private LinearLayout llMenuSchedule;
    private LinearLayout llMenuEvent;
    private LinearLayout llMenuTomato;
    private LinearLayout llMenuCharts;

    //标题栏当前被选中日期数据展示控件
    private LinearLayout llTitleDate;
    private TextView tvTitleMonth, tvTitleDay;
    private String[] mMonthText;

    private TextView tvMainTitle;  //标题栏title

    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;  //用于记录当前被选中日期

    private ScheduleFragment mScheduleFragment;
    private EventSetFragment mEventSetFragment;

    private AlarmBroadcastReceiver receiver;
    private static AlarmService.AlarmBinder alarmBinder;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            alarmBinder = (AlarmService.AlarmBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initStatusBar();
        initView();
        initData();
        initBroadcastReceiver();
        initService();
        gotoScheduleFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(ID_NOTIFICATION_ALL);
        nm.cancel(ID_NOTIFICATION_SINGLE);
    }

    private void initView() {
        mDrawer = findViewById(R.id.dlMain);
        btnMenu = findViewById(R.id.btnMenu);
        llMenuSchedule = findViewById(R.id.llMenuSchedule);
        llMenuEvent = findViewById(R.id.llMenuEvent);
        llMenuTomato = findViewById(R.id.llMenuTomato);
        llMenuCharts = findViewById(R.id.llMenuCharts);
        llTitleDate = findViewById(R.id.llTitleDate);
        tvTitleMonth = findViewById(R.id.tvTitleMonth);
        tvTitleDay = findViewById(R.id.tvTitleDay);
        tvMainTitle = findViewById(R.id.tvMainTitle);

        btnMenu.setOnClickListener(this);
        llMenuEvent.setOnClickListener(this);
        llMenuSchedule.setOnClickListener(this);
        llMenuTomato.setOnClickListener(this);
        llMenuCharts.setOnClickListener(this);
        tvMainTitle.setOnClickListener(this);

        mMonthText = getResources().getStringArray(R.array.calendar_month);  //获取月份字符串数组
        llTitleDate.setVisibility(View.VISIBLE);
        //初始化标题栏的日期数据
        tvTitleMonth.setText(mMonthText[Calendar.getInstance().get(Calendar.MONTH)]);
        tvTitleDay.setText(getString(R.string.calendar_today));
    }

    private void initData() {

    }

    /**
     * 初始化AlarmService
     */
    public void initService(){
        Intent bindIntent = new Intent(MainActivity.this, AlarmService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        startService(bindIntent);
    }

    /**
     * 初始化BroadcastReceiver
     */
    private void initBroadcastReceiver() {
        receiver = new AlarmBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_ALARM_NOTIFICATION);
        registerReceiver(receiver, filter);
    }

    /**
     * 初始化状态栏
     */
    private void initStatusBar() {
        StatusBarUtils.transparencyBar(this);  //设为透明
        StatusBarUtils.setLightStatusBar(this, false, true);  //状态栏字体颜色-黑

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMenu:
                mDrawer.openDrawer(Gravity.START);
                break;

            case R.id.llMenuSchedule:
                gotoScheduleFragment();
                Toast.makeText(this, "Schedule option is clicked !", Toast.LENGTH_SHORT).show();
                break;

            case R.id.llMenuEvent:
                gotoEventSetFragment();
                Toast.makeText(this, "Event option is clicked !", Toast.LENGTH_SHORT).show();
                break;

            case R.id.llMenuTomato:
                Toast.makeText(this, "Tomato option is clicked !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, TomatoClockActivity.class));
                break;

            case R.id.llMenuCharts:
                Toast.makeText(this, "Charts option is clicked !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, ChartsActivity.class));
                break;

            case R.id.tvMainTitle:
                Log.d(TAG, "show Database:" + ScheduleDao.getInstance().findAllSchedule().toString());
        }
    }

    /**
     * 用户选中日历控件中某个日期后，响应的UI展示，即更改标题栏显示的日期数据
     * @param year
     * @param month
     * @param day
     */
    public void resetMainTitleDate(int year, int month, int day) {
        llTitleDate.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        if (year == calendar.get(Calendar.YEAR) &&
                month == calendar.get(Calendar.MONTH) &&
                day == calendar.get(Calendar.DAY_OF_MONTH)) {
            tvTitleMonth.setText(mMonthText[month]);
            tvTitleDay.setText(getString(R.string.calendar_today));
        } else {
            if (year == calendar.get(Calendar.YEAR)) {
                tvTitleMonth.setText(mMonthText[month]);
            } else {
                tvTitleMonth.setText(String.format("%s%s", String.format(getString(R.string.calendar_year), year),
                        mMonthText[month]));
            }
            tvTitleDay.setText(String.format(getString(R.string.calendar_day), day));
        }
        setCurrentSelectDate(year, month, day);
    }

    /**
     * 设置当前被选中的日期数据
     * @param year
     * @param month
     * @param day
     */
    private void setCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
    }

    /**
     * 视图切换为ScheduleFragment
     */
    private void gotoScheduleFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        if (mScheduleFragment == null) {
            mScheduleFragment = ScheduleFragment.getInstance();
            ft.add(R.id.flMainContainer, mScheduleFragment);
        }
        if (mEventSetFragment != null) {
            ft.hide(mEventSetFragment);
        }
        ft.show(mScheduleFragment);
        ft.commit();
        llTitleDate.setVisibility(View.VISIBLE);
        tvMainTitle.setVisibility(View.GONE);
        mDrawer.closeDrawer(Gravity.START);
    }

    /**
     * 视图切换为EventSetFragment
     */
    private void gotoEventSetFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        if(mEventSetFragment == null) {
            mEventSetFragment = EventSetFragment.getInstance();
            ft.add(R.id.flMainContainer, mEventSetFragment);
        }
        if(mScheduleFragment != null) {
            ft.hide(mScheduleFragment);
        }
        ft.show(mEventSetFragment);
        ft.commit();
        llTitleDate.setVisibility(View.GONE);
        tvMainTitle.setVisibility(View.VISIBLE);
        tvMainTitle.setText("收集箱");
        mDrawer.closeDrawer(Gravity.START);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);  //注销广播接收器
    }

    /**
     * 设置通知提醒用户
     * @param context
     * @param id
     */
    public void showNotification(Context context, long id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, ScheduleDetailActivity.class);
        intent.putExtra(ScheduleDetailActivity.SCHEDULE_ID, id);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default";
            String channelName = "默认通知";
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        Schedule schedule = ScheduleDao.getInstance().findScheduleById(id);
        String affair = (schedule == null) ? "" : schedule.getTitle();

        Notification notification = new NotificationCompat.Builder(context, "default")
                .setContentText(affair)
                .setContentTitle("便签提醒")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(MainActivity.ID_NOTIFICATION_SINGLE, notification);
    }

    /**
     * 用于实现到点提醒的广播接收器
     */
    public class AlarmBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(ScheduleDetailActivity.SCHEDULE_ID, -1);
            showNotification(MainActivity.this, id);

        }
    }

    public static AlarmService.AlarmBinder getAlarmBinder() {
        return alarmBinder;
    }
}