package com.seraph.ppschedule.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
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
import com.jimmy.common.util.ToastUtils;
import com.seraph.ppschedule.R;
import com.seraph.ppschedule.adapter.ScheduleAdapter;
import com.seraph.ppschedule.activity.MainActivity;
import com.seraph.ppschedule.bean.Schedule;
import com.seraph.ppschedule.dialog.SelectDateDialog;
import com.seraph.ppschedule.utils.DateUtils;

import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleFragment extends BaseFragment
        implements View.OnClickListener, OnCalendarClickListener, SelectDateDialog.OnSelectDateListener {

   private static final String TAG = "ScheduleFragment";

   private Activity mActivity;
   private View mView;

   private ScheduleLayout slSchedule;  //日历控件
   private ScheduleRecyclerView rvScheduleList;  //List展示控件
   private ScheduleAdapter mScheduleAdapter;  //List适配器
   private RelativeLayout rLNoTask;  //当天用户无任务时的展示控件
   private EditText etInputContent;  //底部输入框

   private List<Schedule> scheduleList = new ArrayList<>();
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
      loadScheduleListFromDB();
      initScheduleList();

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

   private void initScheduleList() {
      rvScheduleList = slSchedule.getSchedulerRecyclerView();
      LinearLayoutManager manager = new LinearLayoutManager(mActivity);
      manager.setOrientation(LinearLayoutManager.VERTICAL);
      rvScheduleList.setLayoutManager(manager);
      DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
      itemAnimator.setSupportsChangeAnimations(false);
      rvScheduleList.setItemAnimator(itemAnimator);
      mScheduleAdapter = new ScheduleAdapter(mActivity, this, scheduleList, rvScheduleList);
      rvScheduleList.setAdapter(mScheduleAdapter);
   }

   /**
    * 从DB中加载Schedule数据
    *
    * 加载list数据的时机：
    * 1、每次ScheduleFragment加载时，获取当日的list数据
    * 2、在ScheduleFragment中切换日期时，加载该日期的list数据
    * 3、每次EventSetFragment加载时，获取全部list数据
    * 4、从ScheduleDetailActivity返回时，更新list数据
    */
   private void loadScheduleListFromDB() {
      Log.d(TAG, "LoadScheduleList: ");

      if(scheduleList.size() > 0) {
         scheduleList.clear();
      }

      // TODO: 2022/4/9 从DB中读取选中日期的schedule数据
      Calendar calendar = Calendar.getInstance();
      if(mCurrentSelectYear ==  calendar.get(Calendar.YEAR)
      && mCurrentSelectMonth == calendar.get(Calendar.MONTH)
      && mCurrentSelectDay == calendar.get(Calendar.DATE)) {
         for(int i = 0; i < 5; i++) {
            scheduleList.add(new Schedule("Title" + (i+1), "", Calendar.getInstance(), false));
         }

         scheduleList.get(3).setFinish(true );
      }

      resetVisibilityOfNoTaskView(); //设置兜底View的可见性
   }

   @Override
   public void resetScheduleList() {
      loadScheduleListFromDB();
      mScheduleAdapter.updateAllScheduleData(scheduleList);
   }

   @Override
   public void resetVisibilityOfNoTaskView() {
      rLNoTask.setVisibility(scheduleList.size()==0 ? View.VISIBLE : View.GONE);
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
      Log.d(TAG, "setCurrentSelectDate: selectDate=" + mCurrentSelectYear + "/" + mCurrentSelectMonth + "/" + mCurrentSelectDay);

   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.ibMainClock:
            Toast.makeText(mActivity, "Clock", Toast.LENGTH_SHORT).show();
            showSelectDateDialog();
            break;

         case R.id.ibMainOk:
            Toast.makeText(mActivity, "Ok", Toast.LENGTH_SHORT).show();
            insertSchedule();
            break;

      }
   }

   private void showSelectDateDialog() {
      new SelectDateDialog(mActivity, this, mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay, slSchedule.getMonthCalendar().getCurrentItem()).show();
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
      //loadScheduleListFromDB();
      resetScheduleList();
   }

   @Override
   public void onPageChange(int year, int month, int day) {

   }

   @Override
   public void insertSchedule() {
      String content = etInputContent.getText().toString();
      if (TextUtils.isEmpty(content)) {
         ToastUtils.showShortToast(mActivity, R.string.schedule_input_content_is_no_null);
      } else {
         closeSoftInput();
         etInputContent.getText().clear();

         Calendar calendar = Calendar.getInstance();
         calendar.set(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);

         Schedule schedule = new Schedule(content, "", calendar, false);
         schedule.setTime(mTime);

         mTime = 0;
         if(mScheduleAdapter != null) {
            mScheduleAdapter.insertItem(schedule);
         }
      }

   }

   @Override
   public void removeSchedule(Schedule schedule) {
      if(mScheduleAdapter != null) {
         mScheduleAdapter.removeItem(schedule);
      }
   }

   @Override
   public void onSelectDate(final int year, final int month, final int day, long time, int position) {
      slSchedule.getMonthCalendar().setCurrentItem(position);
      slSchedule.postDelayed(new Runnable() {
         @Override
         public void run() {
            slSchedule.getMonthCalendar().getCurrentMonthView().clickThisMonth(year, month, day);
         }
      }, 100);
      mTime = time;
      Log.d(TAG, "onSelectDate: mTime=" + mTime);
      Log.d(TAG, "onSelectDate: mTime=" + DateUtils.timeStamp2Date(mTime, null));
      Log.d(TAG, "onSelectDate: selectDate=" + mCurrentSelectYear + "/" + mCurrentSelectMonth + "/" + mCurrentSelectDay);
   }

   @Override
   public int getCurrentCalendarPosition() {
      return slSchedule.getMonthCalendar().getCurrentItem();
   }
}
