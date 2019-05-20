package com.hollysmart.db;
import android.content.Context;

import com.hollysmart.beans.ResModelBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by cai on 2017/12/13
 */

public class ResModelDao {


    private Dao<ResModelBean, String> resBeanListDao;
    private DatabaseHelper helper;

    public ResModelDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            resBeanListDao = helper.getDao(ResModelBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


     /**
     * 修改或增加数据 列表
     */
    public boolean addOrUpdate(List<?> beans) {
        for (ResModelBean bean : (List<ResModelBean>) beans) {
            try {
                if (resBeanListDao.idExists(bean.getId()+""))
                    resBeanListDao.update(bean);
                else
                    resBeanListDao.create(bean);
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
    public boolean addOrUpdate(ResModelBean bean) {
        try {
            if (resBeanListDao.idExists(bean.getId()+""))
                resBeanListDao.update(bean);
            else
                resBeanListDao.create(bean);
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }

    public List<ResModelBean> getData() {

        try {
            return resBeanListDao.queryBuilder().where()
                    .eq("Hint", "2").query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResModelBean getDatById(String id) {

        try {
            return resBeanListDao.queryBuilder().where()
                    .eq("id", id).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<ResModelBean> getAllData() {

        try {
            return resBeanListDao.queryBuilder().query();
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
    public ResModelBean getData(String jqId ) {
        try {
           return resBeanListDao.queryBuilder().where()
                    .eq("jqId", jqId)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean deletAll(){
        try {
            resBeanListDao.deleteBuilder().delete();
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean deletById(String id){
        try {
            DeleteBuilder builder = resBeanListDao.deleteBuilder();
            builder.where().in("id", id);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}






















