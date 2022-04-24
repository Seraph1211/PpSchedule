package com.seraph.ppschedule.dao;

import com.seraph.ppschedule.bean.ConcentrationData;

import org.litepal.LitePal;

import java.util.Iterator;
import java.util.List;

public class ConcentrationDataDao {

   private static ConcentrationDataDao dao;

   private ConcentrationDataDao() {

   }

   public static ConcentrationDataDao getInstance() {
      if(dao == null) {
         dao = new ConcentrationDataDao();
      }

      return dao;
   }

   /**
    * 获取全部DB数据
    * @return
    */
   public List<ConcentrationData> getAllData() {
      return LitePal.findAll(ConcentrationData.class);
   }

   /**
    * 获取某一天的专注时长
    * @param year
    * @param month
    * @param day
    * @return
    */
   public long getDurationOfDate(int year, int month, int day) {
      List<ConcentrationData> res = LitePal.findAll(ConcentrationData.class);
      if(res.size() == 0) {
         return 0;
      }

      Iterator<ConcentrationData> iterator = res.iterator();
      ConcentrationData data;
      while(iterator.hasNext()) {
         data = iterator.next();
         if(data.getYear() == year
         && data.getMonth() == month
         && data.getDay() == day) {
            return data.getDuration();
         }
      }

      return 0;
   }

   /**
    * 获取某个月的专注时长
    * @param year
    * @param month
    * @return
    */
   public int getDurationOfMonth(int year, int month) {
      List<ConcentrationData> res = LitePal.findAll(ConcentrationData.class);
      if(res.size() == 0) {
         return 0;
      }

      int duration = 0;
      Iterator<ConcentrationData> iterator = res.iterator();
      ConcentrationData data;
      while(iterator.hasNext()) {
         data = iterator.next();
         if(data.getYear() == year
                 && data.getMonth() == month) {
            duration += data.getDuration();
         }
      }

      return duration;
   }

   /**
    * 获取某一年的专注时长
    * @param year
    * @return
    */
   public int getDurationOfMonth(int year) {
      List<ConcentrationData> res = LitePal.findAll(ConcentrationData.class);
      if(res.size() == 0) {
         return 0;
      }

      int duration = 0;
      Iterator<ConcentrationData> iterator = res.iterator();
      ConcentrationData data;
      while(iterator.hasNext()) {
         data = iterator.next();
         if(data.getYear() == year) {
            duration += data.getDuration();
         }
      }

      return duration;
   }

   /**
    * 更新专注时长
    * @param concentrationData
    * @return
    */
   public boolean updateDuration(ConcentrationData concentrationData) {
      if(concentrationData != null && concentrationData.getDuration() > 0) {
         ConcentrationData data = LitePal.find(ConcentrationData.class, concentrationData.getId());
         data.setDuration(concentrationData.getDuration());
         return data.save();
      }

      return false;
   }

   public boolean addDurationData(ConcentrationData data) {
      return data.save();
   }

   /**
    * 查询DB中是否存在某天的数据
    * @param year
    * @param month
    * @param day
    * @return
    */
   public ConcentrationData isExistInDB(int year, int month, int day) {
      List<ConcentrationData> res = LitePal.findAll(ConcentrationData.class);
      if(res.size() == 0) {
         return null;
      }

      Iterator<ConcentrationData> iterator = res.iterator();
      ConcentrationData data;
      while(iterator.hasNext()) {
         data = iterator.next();
         if(data.getYear() == year
                 && data.getMonth() == month
                 && data.getDay() == day) {
            return data;
         }
      }

      return null;
   }

}
