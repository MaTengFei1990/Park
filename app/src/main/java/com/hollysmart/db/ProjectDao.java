package com.hollysmart.db;
import android.content.Context;

import com.hollysmart.formlib.beans.ProjectBean;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by cai on 2017/12/13
 */

public class ProjectDao {


    private Dao<ProjectBean, String> projectDao;
    private DatabaseHelper helper;

    public ProjectDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            projectDao = helper.getDao(ProjectBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改或增加数据 列表
     */
    public boolean addOrUpdate(List<?> beans) {
        for (ProjectBean bean : (List<ProjectBean>) beans) {
            try {
                if (projectDao.idExists(bean.getId())) {
                    projectDao.update(bean);
                } else {
                    projectDao.create(bean);
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
    public boolean addOrUpdate(ProjectBean bean) {
        try {
            if (projectDao.idExists(bean.getId()+""))
                projectDao.update(bean);
            else
                projectDao.create(bean);
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 修改或增加数据 对象
     */
    public ProjectBean getLastId() {
        try {
            ProjectBean jdInfo = projectDao.queryBuilder().orderBy("id", false).queryForFirst();
            return jdInfo;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据ID获取
     */
    public ProjectBean getDataByID(String id) {
        try {
            ProjectBean jdInfo = projectDao.queryBuilder().where().eq("id", id).queryForFirst();
            return jdInfo;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<ProjectBean> getAllData(String userId) {

        try {
            return projectDao.queryBuilder().orderBy("createDate",false).where().eq("userinfoid",userId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ProjectBean> searchDataByName(String name) {

        try {
            return projectDao.queryBuilder().where().like("fTaskname", "%"+name+"%").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }





    public List<ProjectBean> getListByState(int state,String userId) {

        try {
            return projectDao.queryBuilder().orderBy("createDate",false).where()
                    .eq("fState", state).and().eq("userinfoid",userId).query();
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
    public List<ProjectBean> getData(String jqId ) {
        try {
            return projectDao.queryBuilder().where()
                    .eq("id", jqId)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }





    public boolean deletAll(){
        try {
            projectDao.deleteBuilder().delete();
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean deletByProjectId(String projectId){
        try {
            projectDao.deleteById(projectId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}






















