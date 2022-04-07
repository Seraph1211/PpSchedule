package com.seraph.ppschedule.dao;

import com.seraph.ppschedule.bean.Schedule;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class ScheduleDao {

    public boolean updateSchedule(Schedule schedule) {
        return schedule.save();
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
     * 新增Schedule
     * @param title
     * @param desc
     * @param calendar
     * @return
     */
    public boolean addSchedule(String title, String desc, Calendar calendar) {
        return new Schedule(title, desc, calendar, false).save();
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
     * @param date
     * @return
     */
    public List<Schedule> findScheduleByDate(Calendar date) {
        if(date == null) {
            return null;
        }

        List<Schedule> scheduleList = LitePal.findAll(Schedule.class);
        Iterator<Schedule> iterator = scheduleList.iterator();
        Schedule schedule;
        //获取DB中与目标日期一致的Schedule数据
        while (iterator.hasNext()) {
            schedule = iterator.next();

            if(schedule.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR)
            && schedule.getDate().get(Calendar.MONTH) == date.get(Calendar.MONTH)
                    && schedule.getDate().get(Calendar.DATE) == date.get(Calendar.DATE)) {
                scheduleList.add(schedule);
            }
        }

        return scheduleList;
    }

}
