package com.seraph.ppschedule.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.seraph.ppschedule.R;
import com.seraph.ppschedule.bean.Schedule;
import com.seraph.ppschedule.dao.ScheduleDao;
import com.seraph.ppschedule.dialog.SelectDateDialog;
import com.seraph.ppschedule.fragment.ScheduleFragment;
import com.seraph.ppschedule.utils.DateUtils;
import com.seraph.ppschedule.utils.StatusBarUtils;

import java.util.Calendar;
import java.util.Date;

public class ScheduleDetailActivity extends AppCompatActivity implements SelectDateDialog.OnSelectDateListener {
    public static final String SCHEDULE_ID = "schedule_id";
    public static final String TOOLBAR_TITLE = "toolbar_title";
    public static String CALENDAR_POSITION = "calendar.position";

    private static final String TAG = "ScheduleDetailActivity";

    private ImageButton btnBack;  //返回按钮
    private TextView tvDestination;  //上一级
    private CheckBox cbState;  //完成状态
    private TextView tvDate;  //日期
    private EditText etTitle;  //标题
    private EditText etDesc;  //描述

    private Schedule mSchedule;  //data model
    private String title = "返回";

    private SelectDateDialog mSelectDateDialog;
    private long mTime;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        initStatusBar();
        initData();
        initView();
    }

    /**
     * 初始化状态栏
     */
    private void initStatusBar() {
        StatusBarUtils.setStatusBarColor(this, R.color.colorWrite);  //设置状态栏颜色
        StatusBarUtils.setLightStatusBar(this, true, true);  //状态栏字体颜色-黑
    }

    private void initData() {
        long id = getIntent().getLongExtra(SCHEDULE_ID, -1);
        mPosition = getIntent().getIntExtra(CALENDAR_POSITION, -1);
        title = getIntent().getStringExtra(TOOLBAR_TITLE);

        mSchedule = ScheduleDao.getInstance().findScheduleById(id);
        Log.d(TAG, "initData: mSchedule=" + mSchedule.toString());
    }

    private void initView() {
        btnBack = findViewById(R.id.btnBackOfDetail);
        tvDestination = findViewById(R.id.tvDestinationOfBack);
        cbState = findViewById(R.id.cbOfDetail);
        tvDate = findViewById(R.id.tvDateOfDetail);
        etTitle = findViewById(R.id.etTitleOfDetail);
        etDesc = findViewById(R.id.etDescOfDetail);


        if(mSchedule != null) {
            cbState.setChecked(mSchedule.isFinish());
            etTitle.setText(mSchedule.getTitle());
            etDesc.setText(mSchedule.getDesc());

            resetDateUi();
        } else {
            Log.d(TAG, "initView: mSchedule == null");
        }

        tvDestination.setText(title);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ScheduleDetailActivity.this, "back button was clicked !", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        cbState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSchedule.setFinish(isChecked);
                ScheduleDao.getInstance().updateSchedule(mSchedule);
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDateDialog();
            }
        });

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d(TAG, "afterTextChanged: Title=" + etTitle.getText().toString());
                mSchedule.setTitle(etTitle.getText().toString());
                ScheduleDao.getInstance().updateSchedule(mSchedule);
            }
        });

        etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d(TAG, "afterTextChanged: Desc=" + etDesc.getText().toString());
                mSchedule.setDesc(etDesc.getText().toString());
                ScheduleDao.getInstance().updateSchedule(mSchedule);
            }
        });
    }

    private void showSelectDateDialog() {
        if (mSelectDateDialog == null) {
            mSelectDateDialog = new SelectDateDialog(this, this,
                    mSchedule.getYear(),
                    mSchedule.getMonth(),
                    mSchedule.getDay(),
                    mPosition);
        }
        mSelectDateDialog.show();
    }

    @Override
    public void onSelectDate(int year, int month, int day, long time, int position) {
        long oldTime = mSchedule.getTime();  //用于判断该任务之前是否已设置提醒（为0则说明之前没设置提醒）

        Log.d(TAG, "onSelectDate: " + year + "/" + month + "/" + day);
        Log.d(TAG, "onSelectDate: time=" + DateUtils.timeStamp2Date(time, "yyyy/MM/dd HH:mm:ss"));

        mSchedule.setYear(year);
        mSchedule.setMonth(month);
        mSchedule.setDay(day);
        mSchedule.setTime(time);
        ScheduleDao.getInstance().updateSchedule(mSchedule);
        resetDateUi();

        //设置提醒任务
        if(oldTime == 0) {
            if(time == 0) {
                //do nothing
            } else {
                if(time < Calendar.getInstance().getTimeInMillis()) {
                    MainActivity.getAlarmBinder().cancelAlarm(mSchedule.getId());
                } else {
                    MainActivity.getAlarmBinder().addAlarm(mSchedule.getId(), time);
                }
            }
        } else {
            if(time == 0) {
                MainActivity.getAlarmBinder().cancelAlarm(mSchedule.getId());
            } else {
                if(time < Calendar.getInstance().getTimeInMillis()) {
                    MainActivity.getAlarmBinder().cancelAlarm(mSchedule.getId());
                } else {
                    MainActivity.getAlarmBinder().addAlarm(mSchedule.getId(), time);
                }
            }
        }

    }

    private void resetDateUi() {
        if(mSchedule.getTime() == 0) {
            tvDate.setText(mSchedule.getYear() + "/" + (mSchedule.getMonth() + 1) + "/" + mSchedule.getDay());
        } else {
            tvDate.setText(DateUtils.timeStamp2Date(mSchedule.getTime(), null));
        }

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}