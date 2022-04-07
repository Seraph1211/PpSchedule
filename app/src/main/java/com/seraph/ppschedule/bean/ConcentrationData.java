package com.seraph.ppschedule.bean;

import org.litepal.crud.LitePalSupport;

import java.util.Calendar;

/**
 * 记录专注数据的实体类
 * 描述用户某一天的专注时长，时长通过番茄钟不断累加
 */
public class ConcentrationData extends LitePalSupport {

   private int concentrationDuration;  //专注总时长
   private Calendar date;  //日期

   //constructor
   public ConcentrationData() {
      concentrationDuration = 0;
      date = Calendar.getInstance();
   }

   public ConcentrationData(int concentrationDuration, Calendar date) {
      this.concentrationDuration = concentrationDuration;
      this.date = date;
   }

   //getter & setter
   public int getConcentrationDuration() {
      return concentrationDuration;
   }

   public void setConcentrationDuration(int concentrationDuration) {
      this.concentrationDuration = concentrationDuration;
   }

   public Calendar getDate() {
      return date;
   }

   public void setDate(Calendar date) {
      this.date = date;
   }
}
