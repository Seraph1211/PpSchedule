package com.seraph.ppschedule.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleRecyclerView;
import com.jimmy.common.util.DeviceUtils;
import com.seraph.ppschedule.R;
import com.seraph.ppschedule.activity.MainActivity;

import java.util.Calendar;

public class ScheduleFragment extends Fragment implements View.OnClickListener, OnCalendarClickListener {

   private static final String TAG = "ScheduleFragment";

   private Activity mActivity;
   private View mView;

   private ScheduleLayout slSchedule;  //日历控件
   //private ScheduleRecyclerView rvScheduleList;
   private EditText etInputContent;  //底部输入框
   private RelativeLayout rLNoTask;  //当天用户无任务时的展示控件

   //private ScheduleAdapter mScheduleAdapter;
   private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;   //当前被选中的日期数据
   private long mTime;

   public static ScheduleFragment getInstance() {
      return new ScheduleFragment();
   }

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      mActivity = getActivity();
      mView = inflater.inflate(R.layout.fragment_schedule, container, false);

      initView();
      initDate();
      initBottomInputBar();

      return mView;
   }

   private void initView() {
      slSchedule = mView.findViewById(R.id.slSchedule);
      etInputContent = mView.findViewById(R.id.etInputContent);
      rLNoTask = mView.findViewById(R.id.rlNoTask);

      slSchedule.setOnCalendarClickListener(this);  //为日历控件注册点击事件监听器

      //为底部输入框两侧按钮注册点击事件监听器
      mView.findViewById(R.id.ibMainClock).setOnClickListener(this);
      mView.findViewById(R.id.ibMainOk).setOnClickListener(this);
   }

   /**
    * 初始化Fragment底部输入框
    */
   private void initBottomInputBar() {
      etInputContent.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {

         }

         @Override
         public void afterTextChanged(Editable s) {
            etInputContent.setGravity(s.length() == 0 ? Gravity.CENTER : Gravity.CENTER_VERTICAL);
         }
      });
      etInputContent.setOnKeyListener(new View.OnKeyListener() {
         @Override
         public boolean onKey(View v, int keyCode, KeyEvent event) {
            return false;
         }
      });
   }

   /**
    * 关闭输入弹框
    */
   private void closeSoftInput() {
      etInputContent.clearFocus();
      DeviceUtils.closeSoftInput(mActivity, etInputContent);
   }

   /**
    * 初始化当前日期
    */
   private void initDate() {
      Calendar calendar = Calendar.getInstance();
      setCurrentSelectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

      Log.d(TAG, "initDate: " + mCurrentSelectYear + "/" + mCurrentSelectMonth + "/" + mCurrentSelectDay);
   }

   /**
    * 用户点击日历控件后，更新当前被选中的日期数据
    * @param year
    * @param month
    * @param day
    */
   private void setCurrentSelectDate(int year, int month, int day) {
      mCurrentSelectYear = year;
      mCurrentSelectMonth = month;
      mCurrentSelectDay = day;
      if (mActivity instanceof MainActivity) {
         ((MainActivity) mActivity).resetMainTitleDate(year, month, day);
      }
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.ibMainClock:
            Toast.makeText(mActivity, "Clock", Toast.LENGTH_SHORT).show();
            break;

         case R.id.ibMainOk:
            Toast.makeText(mActivity, "Ok", Toast.LENGTH_SHORT).show();
            break;

      }
   }

   /**
    * 日历控件选中日期后的回调
    * @param year
    * @param month
    * @param day
    */
   @Override
   public void onClickDate(int year, int month, int day) {
      Toast.makeText(mActivity, "date: " +year + "/" + month + "/" + day, Toast.LENGTH_SHORT).show();
      setCurrentSelectDate(year, month, day);
   }

   @Override
   public void onPageChange(int year, int month, int day) {

   }
}
