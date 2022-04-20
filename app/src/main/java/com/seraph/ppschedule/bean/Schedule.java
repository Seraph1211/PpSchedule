package com.seraph.ppschedule.bean;

import android.support.annotation.NonNull;

import com.seraph.ppschedule.utils.DateUtils;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

// TODO: 2022/4/18 LitePal支持的数据类型有8种，分别为：int、short、long、float、double、boolean、String和Date。不支持Calendar! 
/**
 * 记录任务信息的实体类
 */
public class Schedule extends LitePalSupport implements Serializable {

    private long id;  //唯一标识
    private String title;  //标题
    private String desc;  //描述
    private boolean isFinish;  //完成标记
    private long time;  //HH:mm
    private int year;
    private int month;
    private int day;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @NonNull
    @Override
    public String toString() {
        String result = "[info=" + super.toString()
                + ", id=" + id
                + ", isFinish=" + isFinish
                + ", tittle=" + title
                + ", desc=" + desc
                + ", date=" + year + "/" + month + "/" + day
                + ", time=" + time
                + ", timeOfDate=" + DateUtils.timeStamp2Date(time, null)
                + "]";

        return result;
    }
}
