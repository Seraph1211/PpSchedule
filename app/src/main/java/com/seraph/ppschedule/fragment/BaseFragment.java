package com.seraph.ppschedule.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.seraph.ppschedule.bean.Schedule;

public class BaseFragment extends Fragment {

    /**
     * 添加schedule
     */
    public void insertSchedule() {}

    /**
     * 删除schedule
     * @param schedule
     */
    public void removeSchedule(Schedule schedule) {}

    /**
     * 用于EventSetFragment中，schedule完成状态改变后的UI变更
     * @param schedule
     */
    public void changeScheduleSate(Schedule schedule) {}

    /**
     * 更新无任务时兜底View的可见性
     */
    public void resetVisibilityOfNoTaskView() {}

    /**
     *按以下两步更新schedule数据
     * 1、从DB中读取schedule数据
     * 2、调用adapter的notify方法刷新视图
     */
    public void resetScheduleList(){}

    @Override
    public void onStart() {
        Log.d("BaseFrag", "onStart: ");
        resetScheduleList();
        super.onStart();
    }

    public int getCurrentCalendarPosition() {
        return -1;
    }
}
