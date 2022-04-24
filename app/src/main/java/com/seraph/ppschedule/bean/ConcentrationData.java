package com.seraph.ppschedule.bean;

import org.litepal.crud.LitePalSupport;

/**
 * 记录专注数据的实体类
 * 描述用户某一天的专注时长，时长通过番茄钟不断累加
 */
public class ConcentrationData extends LitePalSupport {

   private int id;
   private long duration;  //专注总时长（单位：秒）
   //日期
   private int year;
   private int month;  //比实际月份值小1
   private int day;


   //getter & setter
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public long getDuration() {
      return duration;
   }

   public void setDuration(long duration) {
      this.duration = duration;
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

   @Override
   public String toString() {
      return "ConcentrationData{" +
              "id=" + id +
              ", duration=" + duration +
              ", year=" + year +
              ", month=" + month +
              ", day=" + day +
              '}';
   }
}
