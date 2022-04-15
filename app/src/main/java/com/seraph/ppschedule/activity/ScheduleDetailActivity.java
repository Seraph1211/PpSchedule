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
import com.seraph.ppschedule.dialog.SelectDateDialog;
import com.seraph.ppschedule.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;

// TODO: 2022/4/14 1、内容变更时添加DB操作 2、增加日期的变更能力 
public class ScheduleDetailActivity extends AppCompatActivity implements SelectDateDialog.OnSelectDateListener {
    public static final String SCHEDULE_OBJ = "schedule_obj";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        initData();
        initView();

    }

    private void initData() {
        mSchedule = (Schedule) getIntent().getSerializableExtra(SCHEDULE_OBJ);
        title = getIntent().getStringExtra(TOOLBAR_TITLE);

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
                Toast.makeText(ScheduleDetailActivity.this, "back button was clicked !", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        cbState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                Log.d(TAG, "afterTextChanged: Title = " + etTitle.getText().toString());
                //mSchedule.setTitle(etTitle.getText().toString());
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
                Log.d(TAG, "afterTextChanged: Desc" + etDesc.getText().toString());
                //mSchedule.setDesc(etDesc.getText().toString());
            }
        });
    }


    // TODO: 2022/4/15 研究EventSetFragment如何传入mPosition
    private void showSelectDateDialog() {
        if (mSelectDateDialog == null) {
            mSelectDateDialog = new SelectDateDialog(this, this,
                    mSchedule.getDate().get(Calendar.YEAR), mSchedule.getDate().get(Calendar.MONTH), mSchedule.getDate().get(Calendar.DATE), mPosition);
        }
        mSelectDateDialog.show();
    }

    @Override
    public void onSelectDate(int year, int month, int day, long time, int position) {
        mSchedule.getDate().set(year, month, day);
        mSchedule.setTime(time);
        resetDateUi();
    }

    private void resetDateUi() {

        if(mSchedule.getTime() == 0) {
            tvDate.setText(DateUtils.date2DateString(mSchedule.getDate().getTime()));
        } else {
            tvDate.setText(DateUtils.timeStamp2Date(mSchedule.getTime(), null));
        }

    }

}