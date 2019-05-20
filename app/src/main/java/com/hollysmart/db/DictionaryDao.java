package com.hollysmart.db;

import android.content.Context;

import com.hollysmart.beans.DictionaryBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lenovo on 2019/5/6.
 */

public class DictionaryDao {


    private Dao<DictionaryBean, String> dictionnaryDao;
    private DatabaseHelper helper;

    public DictionaryDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            dictionnaryDao = helper.getDao(DictionaryBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改或增加数据 列表
     */
    public boolean addOrUpdate(List<?> beans) {
        for (DictionaryBean bean : (List<DictionaryBean>) beans) {
            try {
                if (dictionnaryDao.idExists(bean.getId()+""))
                    dictionnaryDao.update(bean);
                else
                    dictionnaryDao.create(bean);
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
    public boolean addOrUpdate(DictionaryBean bean) {
        try {
            if (dictionnaryDao.idExists(bean.getId()+""))
                dictionnaryDao.update(bean);
            else
                dictionnaryDao.create(bean);
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }




    public List<DictionaryBean> getData() {

        try {
            return dictionnaryDao.queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<DictionaryBean> getDataType(String type) {

        try {
            return dictionnaryDao.queryBuilder().where().eq("type",type).query();
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
    public List<DictionaryBean> getData(String jqId ) {
        try {
            return dictionnaryDao.queryBuilder().where()
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
    public List<DictionaryBean> getDataByJDId(String jdId ) {
        try {
            return dictionnaryDao.queryBuilder().where()
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
            DeleteBuilder builder = dictionnaryDao.deleteBuilder();
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
            DeleteBuilder builder = dictionnaryDao.deleteBuilder();
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
            DeleteBuilder builder = dictionnaryDao.deleteBuilder();
            builder.where().in("id", PicId);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
