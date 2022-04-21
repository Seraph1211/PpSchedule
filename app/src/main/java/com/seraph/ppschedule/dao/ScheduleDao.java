package com.seraph.ppschedule.dao;

import android.util.Log;

import com.seraph.ppschedule.bean.Schedule;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class ScheduleDao {
    private static final String TAG = "ScheduleDao";

    private static ScheduleDao dao;

    private ScheduleDao() {

    }

    public static ScheduleDao getInstance() {
        if(dao == null) {
            dao = new ScheduleDao();
        }
        return dao;
    }

    public boolean updateSchedule(Schedule schedule) {
        if(schedule != null) {
            Schedule data = LitePal.find(Schedule.class, schedule.getId());
            data.setId(schedule.getId());
            data.setTitle(schedule.getTitle());
            data.setDesc(schedule.getDesc());
            data.setYear(schedule.getYear());
            data.setMonth(schedule.getMonth());
            data.setDay(schedule.getDay());
            data.setTime(schedule.getTime());
            data.setFinish(schedule.isFinish());
            return data.save();
        }

        return false;
    }

    /**
     * 根据id删除Schedule
     * @param id
     * @return
     */
    public int deleteSchedule(long id) {
        return LitePal.delete(Schedule.class, id);
    }

    /**
     * 添加Schedule
     * @param title
     * @param desc
     * @param year
     * @param month
     * @param day
     * @param time
     * @return
     */
    public boolean addSchedule(String title, String desc, int year, int month, int day, long time) {
        //Log.d(TAG, "addSchedule: " + );
        //return new Schedule(title, desc, calendar, false, time).save();
        Schedule schedule = new Schedule();
        schedule.setTitle(title);
        return false;
    }

    /**
     * 添加Schedule
     * @param schedule
     * @return
     */
    public boolean addSchedule(Schedule schedule) {
        if(schedule != null) {
            return schedule.save();
        }
        return false;
    }

    public Schedule findScheduleById(long id) {
        return LitePal.find(Schedule.class, id);
    }

    public List<Schedule> findAllSchedule() {
        return LitePal.findAll(Schedule.class);
    }

    /**
     * 根据完成标记查询Schedule数据
     * @param isFinish
     * @return
     */
    public List<Schedule> findScheduleByFlag(boolean isFinish) {
        return LitePal.where("isFinish = ?", String.valueOf(isFinish)).find(Schedule.class);
    }

    /**
     * 根据日期查询Schedule数据
     * @param year
     * @param month
     * @param day
     * @return
     */
    public List<Schedule> findScheduleByDate(int year, int month, int day) {
        List<Schedule> scheduleList = LitePal.findAll(Schedule.class);
        List<Schedule> res = new ArrayList<>();
        Iterator<Schedule> iterator = scheduleList.iterator();
        Schedule schedule;
        //获取DB中与目标日期一致的Schedule数据
        while (iterator.hasNext()) {
            schedule = iterator.next();

            if(schedule.getYear() == year
            && schedule.getMonth() == month
            && schedule.getDay() == day) {
                res.add(schedule);
            }
        }

        return res;
    }

}
