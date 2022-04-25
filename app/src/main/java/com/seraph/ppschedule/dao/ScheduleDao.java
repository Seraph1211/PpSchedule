package com.seraph.ppschedule.dao;

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


    public List<Schedule> findScheduleOfOneWeekRelatedToState(int year, int month, int day, boolean state) {
        //筛选指定日期区间内的数据
        List<Schedule> res = findScheduleOfOneWeek(year, month, day);

        //筛选指定状态的数据
        Iterator<Schedule> iterator = res.iterator();
        List<Schedule> removedItems = new ArrayList<>();
        Schedule schedule;
        while (iterator.hasNext()) {
            schedule = iterator.next();
            if(schedule.isFinish() != state) {
                removedItems.add(schedule);
                //res.remove(schedule);
            }
        }

        res.removeAll(removedItems);

        return res;
    }

    /**
     * 获取目标日期所属那一周的全部Schedule
     * @param year
     * @param month
     * @param day
     * @return
     */
    public List<Schedule> findScheduleOfOneWeek(int year, int month, int day) {
        Calendar firstDayOfWeek = Calendar.getInstance();
        firstDayOfWeek.set(year, month, day);
        firstDayOfWeek.set(Calendar.DAY_OF_WEEK, 1);

        return findScheduleBetweenDateSection(firstDayOfWeek.get(Calendar.YEAR), firstDayOfWeek.get(Calendar.MONTH), firstDayOfWeek.get(Calendar.DAY_OF_MONTH),
                year, month, day);
    }

    /**
     * 获得两个日期区间之内的全部Schedule
     * @param fromYear
     * @param fromMonth
     * @param fromDay
     * @param toYear
     * @param toMonth
     * @param toDay
     * @return
     */
    public List<Schedule> findScheduleBetweenDateSection(int fromYear, int fromMonth, int fromDay,
                                                         int toYear, int toMonth, int toDay) {

        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.set(fromYear, fromMonth, fromDay, 0, 0, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.set(toYear, toMonth, toDay, 23, 59, 59);

        List<Schedule> res = LitePal.findAll(Schedule.class);
        Iterator<Schedule> iterator = res.iterator();
        Schedule schedule;
        Calendar calendar = Calendar.getInstance();
        //筛选指定日期区间内的数据
        List<Schedule> removedItems = new ArrayList<>();
        while (iterator.hasNext()) {
            schedule = iterator.next();
            calendar.set(schedule.getYear(), schedule.getMonth(), schedule.getDay(), 0, 0, 0);

            if(calendar.compareTo(fromCalendar) < 0 || calendar.compareTo(toCalendar) > 0) {
                removedItems.add(schedule);
                //res.remove(schedule);
            }
        }
        res.removeAll(removedItems);

        return res;
    }

    /**
     * 返回指定日期区间和完成状态的Schedule
     * @param fromYear
     * @param fromMonth
     * @param fromDay
     * @param toYear
     * @param toMonth
     * @param toDay
     * @param isFinish
     * @return
     */
    public List<Schedule> findScheduleByDateSectionAndState(int fromYear, int fromMonth, int fromDay,
                                                            int toYear, int toMonth, int toDay,
                                                            boolean isFinish) {
        //筛选指定日期区间内的数据
        List<Schedule> res = findScheduleBetweenDateSection(fromYear, fromMonth, fromDay, toYear, toMonth, toDay);

        //筛选指定状态的数据
        Iterator<Schedule> iterator = res.iterator();
        Schedule schedule;
        while (iterator.hasNext()) {
            schedule = iterator.next();
            if(schedule.isFinish() != isFinish) {
                res.remove(schedule);
            }
        }

        return res;
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
