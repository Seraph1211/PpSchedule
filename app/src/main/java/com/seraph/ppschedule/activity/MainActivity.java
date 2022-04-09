package com.seraph.ppschedule.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import com.seraph.ppschedule.fragment.ScheduleFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawer;
    private ImageButton btnMenu;

    //菜单options数据控件
    private LinearLayout llMenuSchedule;
    private LinearLayout llMenuEvent;

    //标题栏当前被选中日期数据展示控件
    private LinearLayout llTitleDate;
    private TextView tvTitleMonth, tvTitleDay;
    private String[] mMonthText;

    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;  //用于记录当前被选中日期

    ScheduleFragment mScheduleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        gotoScheduleFragment();

    }

    private void initView() {
        mDrawer = findViewById(R.id.dlMain);
        btnMenu = findViewById(R.id.btnMenu);
        llMenuSchedule = findViewById(R.id.llMenuSchedule);
        llMenuEvent = findViewById(R.id.llMenuEvent);
        llTitleDate = findViewById(R.id.llTitleDate);
        tvTitleMonth = findViewById(R.id.tvTitleMonth);
        tvTitleDay = findViewById(R.id.tvTitleDay);

        btnMenu.setOnClickListener(this);
        llMenuEvent.setOnClickListener(this);
        llMenuSchedule.setOnClickListener(this);

        mMonthText = getResources().getStringArray(R.array.calendar_month);  //获取月份字符串数组
        llTitleDate.setVisibility(View.VISIBLE);
        //初始化标题栏的日期数据
        tvTitleMonth.setText(mMonthText[Calendar.getInstance().get(Calendar.MONTH)]);
        tvTitleDay.setText(getString(R.string.calendar_today));
    }

    private void initData() {

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
                Toast.makeText(this, "Event option is clicked !", Toast.LENGTH_SHORT).show();
                break;
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
//        if (mEventSetFragment != null)
//            ft.hide(mEventSetFragment);
        ft.show(mScheduleFragment);
        ft.commit();
        llTitleDate.setVisibility(View.VISIBLE);
        //tvTitle.setVisibility(View.GONE);
        mDrawer.closeDrawer(Gravity.START);
    }

}