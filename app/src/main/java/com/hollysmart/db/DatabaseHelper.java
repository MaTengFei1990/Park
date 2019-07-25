package com.hollysmart.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hollysmart.beans.DictionaryBean;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.LuXianInfo;
import com.hollysmart.beans.VoiceInfoBean;
import com.hollysmart.formlib.beans.ProjectBean;
import com.hollysmart.formlib.beans.ResDataBean;
import com.hollysmart.beans.ResModelBean;
import com.hollysmart.beans.SoundInfo;
import com.hollysmart.utils.Mlog;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cai on 16/6/7
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final int VERSION = 1;
    private static final String TABLE_NAME = "data.db";
    public static final String LUXIAN_NAME="luxian";

    private Map<String, Dao> daos = new HashMap<>();


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Mlog.d("创建表  onCreate");
        try {
            TableUtils.createTable(connectionSource, ProjectBean.class);
            TableUtils.createTable(connectionSource, ResDataBean.class);
            TableUtils.createTable(connectionSource, JDPicInfo.class);
            TableUtils.createTable(connectionSource, LuXianInfo.class);
            TableUtils.createTable(connectionSource, ResModelBean.class);
            TableUtils.createTable(connectionSource, SoundInfo.class);
            TableUtils.createTable(connectionSource, DictionaryBean.class);
            TableUtils.createTable(connectionSource, VoiceInfoBean.class);
            Mlog.d("创建表完成");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        String sql1 = "";
        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                case 1://数据库版本1 升级到 版本2
                    //对table 增加字段
                    sql1 = "alter table tb_form add code text";
//                        getDao(FormBean.class).executeRawNoArgs(sql1);
                    break;
                case 2://数据库版本2 升级到 版本3
                    Mlog.d("数据库字段升级  2  -  3");
                    break;

            }
        }
    }

    private static DatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();

        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }





}
