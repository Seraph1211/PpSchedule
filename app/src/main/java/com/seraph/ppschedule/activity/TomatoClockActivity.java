package com.seraph.ppschedule.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.seraph.ppschedule.R;
import com.seraph.ppschedule.bean.ConcentrationData;
import com.seraph.ppschedule.dao.ConcentrationDataDao;
import com.seraph.ppschedule.tomato.TomatoView;
import com.seraph.ppschedule.utils.StatusBarUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

/*
计时策略：

 */

public class TomatoClockActivity extends AppCompatActivity implements TomatoView.ClockListener {
    private static final String TAG = "TomatoClockActivity";

    private ConcentrationData data;  //计时数据模型

    private TomatoView clockView;
    private Button btnClock;

    private int duration;  //用户选择的计时时长
    private int restTime;  //计时结束时的剩余时长

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomato_clock);

        initStatusBar();
        initData();
        initView();
    }

    /**
     * 初始化状态栏
     */
    private void initStatusBar() {
        StatusBarUtils.transparencyBar(TomatoClockActivity.this);  //设为透明
        StatusBarUtils.setLightStatusBar(TomatoClockActivity.this, true, true);  //状态栏字体颜色-黑

    }


    private void initView() {
        clockView = findViewById(R.id.clockView);
        btnClock = findViewById(R.id.btn_start);

        clockView.setClockListener(this);

        btnClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clockView.isStarted() && clockView.getCountdownTime() != 0){
                    startClock();
                }else if (!clockView.isStarted() && clockView.getCountdownTime() == 0){
                    Toast.makeText(TomatoClockActivity.this, "请先设置专注时长", Toast.LENGTH_SHORT).show();
                } else if(clockView.isStarted() && clockView.getCountdownTime() != 0){
                    cancelClock();
                }
            }
        });

    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();

        data = ConcentrationDataDao.getInstance()
                .isExistInDB(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if(data == null) {
            data = new ConcentrationData();

            data.setYear(calendar.get(Calendar.YEAR));
            data.setMonth(calendar.get(Calendar.MONTH));
            data.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * 开始计时
     */
    private void startClock() {
        Toast.makeText(this, "计时开始", Toast.LENGTH_SHORT).show();
        duration = clockView.getCountdownTime();
        clockView.start();
        btnClock.setText("结 束");
    }

    /**
     * 取消计时
     */
    private void cancelClock() {
        restTime = clockView.getCountdownTime();
        clockView.cancel();
        btnClock.setText("开 始");

        data.setDuration(data.getDuration() + duration - restTime);  //更新数据模型
        saveData();
    }

    /**
     * 将计时数据写入DB
     */
    private void saveData() {
        if(ConcentrationDataDao.getInstance().isExistInDB(data.getYear(), data.getMonth(), data.getDay()) != null) {
            //如果DB中存在当天数据
            ConcentrationDataDao.getInstance().updateDuration(data);
        } else {
            //如果DB中不存在当天数据
            ConcentrationDataDao.getInstance().addDurationData(data);
        }

        Log.d(TAG, "saveData: " + ConcentrationDataDao.getInstance().getAllData().toString());
    }

    //计时正常结束的监听
    @Override
    public void onFinish() {
        btnClock.setText("开 始");
        restTime = 0;
        data.setDuration(data.getDuration() + duration - restTime);  //更新数据模型
        saveData();
    }
}