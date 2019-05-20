package com.hollysmart.db;

import android.content.Context;

import com.hollysmart.beans.LuXianInfo;
import com.hollysmart.beans.ResDataBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by cai on 2017/12/13
 */

public class LuXianDao {


    private Dao<LuXianInfo, String> shuaKaDetailsDao;
    private DatabaseHelper helper;

    public LuXianDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            shuaKaDetailsDao = helper.getDao(LuXianInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


     /**
     * 修改或增加数据 列表
     */
    public boolean addOrUpdate(List<?> beans) {
        for (LuXianInfo bean : (List<LuXianInfo>) beans) {
            try {
                if (shuaKaDetailsDao.idExists(bean.getId()+""))
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
    public boolean addOrUpdate(LuXianInfo bean) {
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

    public List<LuXianInfo> getData() {

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
    public List<LuXianInfo> getData(String jqId ) {
        try {
           return shuaKaDetailsDao.queryBuilder().where()
                    .eq("projectId", jqId)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 根据id 获取数据
     *
     * @return
     */
    public List<LuXianInfo> getUnUpLoadDataList(ResDataBean resDataBean) {
        try {
            return shuaKaDetailsDao.queryBuilder().where().eq("isUpload","false")
                    .and().eq("fd_restaskid",resDataBean.getFdTaskId())
                    .and().eq("fd_resmodelid",resDataBean.getFd_resmodelid()).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public boolean deletAll(){
        try {
            shuaKaDetailsDao.deleteBuilder().delete();
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean deletById(String jqId){
        try {
            DeleteBuilder builder = shuaKaDetailsDao.deleteBuilder();
            builder.where().in("id", jqId);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}






















