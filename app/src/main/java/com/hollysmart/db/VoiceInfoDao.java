package com.hollysmart.db;

import android.content.Context;

import com.hollysmart.beans.ResModelBean;
import com.hollysmart.beans.VoiceInfoBean;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class VoiceInfoDao {

    private Dao<VoiceInfoBean, String> voiceInfoDao;
    private DatabaseHelper helper;

    public VoiceInfoDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            voiceInfoDao = helper.getDao(VoiceInfoBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改或增加数据 列表
     */
    public boolean addOrUpdate(List<?> beans) {
        for (VoiceInfoBean bean : (List<VoiceInfoBean>) beans) {
            try {
                if (voiceInfoDao.idExists(bean.getId()+"")) {
                    voiceInfoDao.update(bean);
                } else {
                    voiceInfoDao.create(bean);
                }
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
    public boolean addOrUpdate(VoiceInfoBean bean) {
        try {
            if (voiceInfoDao.idExists(bean.getId()+""))
                voiceInfoDao.update(bean);
            else
                voiceInfoDao.create(bean);
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }


    public VoiceInfoBean getLatestBean(){

        try {
            VoiceInfoBean query =  voiceInfoDao.queryBuilder().queryForFirst();
            return query;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }
    public VoiceInfoBean clearData(){

        try {
           voiceInfoDao.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }




}