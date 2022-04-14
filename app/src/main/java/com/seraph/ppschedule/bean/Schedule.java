package com.seraph.ppschedule.bean;

import android.support.annotation.NonNull;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * 记录任务信息的实体类
 */
public class Schedule extends LitePalSupport implements Serializable {

    private long id;  //唯一标识
    private String title;  //标题
    private String desc;  //描述
    private Calendar date; //时间
    private boolean isFinish;  //完成标记

    public Schedule() {
        title = "";
        desc = "";
        date = Calendar.getInstance();
        isFinish = false;
    }

    public Schedule(String title, String desc, Calendar date, boolean isFinish) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.isFinish = isFinish;
    }

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

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    @NonNull
    @Override
    public String toString() {
        //String result = "[id=" + id + ", "

        return super.toString();
    }
}
