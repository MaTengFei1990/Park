package com.hollysmart.db;

import android.content.Context;

import com.hollysmart.beans.SoundInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by cai on 2017/12/13
 */

public class JDSoundDao {


    private Dao<SoundInfo, String> shuaKaDetailsDao;
    private DatabaseHelper helper;

    public JDSoundDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            shuaKaDetailsDao = helper.getDao(SoundInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


     /**
     * 修改或增加数据 列表
     */
    public boolean addOrUpdate(List<?> beans) {
        for (SoundInfo bean : (List<SoundInfo>) beans) {
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
    public boolean addOrUpdate(SoundInfo bean) {
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





    public List<SoundInfo> getData() {

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
    public List<SoundInfo> getData(String jqId ) {
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
    public List<SoundInfo> getDataByJDId(String jdId ) {
        try {
           return shuaKaDetailsDao.queryBuilder().where()
                    .eq("jdId", jdId)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }




    public boolean deletByAreaId(String AreaId){
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






















