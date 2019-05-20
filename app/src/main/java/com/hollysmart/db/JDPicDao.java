package com.hollysmart.db;
import android.content.Context;

import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.utils.CCM_DateTime;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by cai on 2017/12/13
 */

public class JDPicDao {


    private Dao<JDPicInfo, String> shuaKaDetailsDao;
    private DatabaseHelper helper;

    public JDPicDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            shuaKaDetailsDao = helper.getDao(JDPicInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


     /**
     * 修改或增加数据 列表
     */
    public boolean addOrUpdate(List<?> beans) {
        for (JDPicInfo bean : (List<JDPicInfo>) beans) {
            try {
                if (shuaKaDetailsDao.idExists(bean.getJdId()+""))
                    shuaKaDetailsDao.update(bean);
                else
                    shuaKaDetailsDao.create(bean);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 修改或增加数据 对象
     */
    public boolean addOrUpdate(JDPicInfo bean) {
        try {
            if (shuaKaDetailsDao.idExists(bean.getId()+""))
                shuaKaDetailsDao.update(bean);
            else
                shuaKaDetailsDao.create(bean);
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 根据id 获取数据
     *
     * @return
     */
    public List<JDPicInfo> getUuidDate(String idcarNo,String loginuerId) {
        int year = new CCM_DateTime().getYear();
        int month = new CCM_DateTime().getMonth();
        int day = new CCM_DateTime().getDay();

        String startTimestr = new CCM_DateTime().mergeDatetimeString2(year, month - 1, day, 0, 0, 0);
        String endTimestr = new CCM_DateTime().mergeDatetimeString2(year, month - 1, day, 23, 59, 59);

        long startTimeLong = new CCM_DateTime().StringToLong(startTimestr);
        long endTimeLong = new CCM_DateTime().StringToLong(endTimestr);



        try {
            return shuaKaDetailsDao.queryBuilder().orderBy("createDate", false)
                    .where().eq("idcarNo", idcarNo)
                    .and().between("createDate", startTimeLong, endTimeLong)
                    .and().eq("userInfoId", loginuerId)
                    .query();
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return null;
    }




    public List<JDPicInfo> getData() {

        try {
            return shuaKaDetailsDao.queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 根据景区id 查询景点；
     * @param jqId
     * @return
     */
    public List<JDPicInfo> getData(String jqId ) {
        try {
           return shuaKaDetailsDao.queryBuilder().where()
                    .eq("jqId", jqId)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /***
     * 根据景区id 查询景点；
     * @param jdId
     * @return
     */
    public List<JDPicInfo> getDataByJDId(String jdId ) {
        try {
           return shuaKaDetailsDao.queryBuilder().where()
                    .eq("jdId", jdId)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据项目ID删除照片
     * @param AreaId
     * @return
     */
    public boolean deletByTaskId(String AreaId){
        try {
            DeleteBuilder builder = shuaKaDetailsDao.deleteBuilder();
            builder.where().in("jqId", AreaId);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据资源ID删除照片
     * @param resId
     * @return
     */
    public boolean deletByResId(String resId){
        try {
            DeleteBuilder builder = shuaKaDetailsDao.deleteBuilder();
            builder.where().in("jdId", resId);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean deletByPicId(int PicId){
        try {
            DeleteBuilder builder = shuaKaDetailsDao.deleteBuilder();
            builder.where().in("id", PicId);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}






















