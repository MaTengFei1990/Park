package com.hollysmart.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hollysmart.apis.SaveResDataAPI;
import com.hollysmart.apis.UpLoadResPicAPI;
import com.hollysmart.apis.UpLoadSoundAPI;
import com.hollysmart.apis.UserLoginAPI;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.beans.SoundInfo;
import com.hollysmart.db.JDPicDao;
import com.hollysmart.db.JDSoundDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.utils.taskpool.TaskPool;
import com.hollysmart.value.Values;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cai on 2018/1/5
 */

public class SubmitFormService extends Service implements OnNetRequestListener, UserLoginAPI.LoginInfoIF {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static int TYPE_XINZENG = 1;  //新增
    public static int TYPE_BIANLI = 2;

    private ResDataDao formDao;
    private JDPicDao jdPicDao;
    private JDSoundDao jdSoundDao;

    private TaskPool taskPool;
    private UserInfo userInfoBean;

    @Override
    public void onCreate() {
        super.onCreate();
        formDao = new ResDataDao(this);
        jdPicDao = new JDPicDao(this);
        jdPicDao = new JDPicDao(this);
        jdSoundDao = new JDSoundDao(this);
        taskPool = new TaskPool();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int intentType = intent.getIntExtra("type", 0);
        String userPath = Values.SDCARD_FILE(Values.SDCARD_CACHE) + Values.CACHE_USER;
        userInfoBean = (UserInfo) ACache.get(new File(userPath)).getAsObject(Values.CACHE_USERINFO);

        if (intentType == TYPE_XINZENG) {
            String uuid = intent.getStringExtra("uuid");
            xinZeng(uuid);
        } else if (intentType == TYPE_BIANLI) {
            bianLi();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 核查完成后提交新数据
     *
     * @param uuid 新增数据id
     */
    private void xinZeng(String uuid) {
        List<ResDataBean> formBeens = formDao.getUuidDate(uuid);
        for (ResDataBean bean : formBeens) {

            List<JDPicInfo> picList = jdPicDao.getDataByJDId(bean.getId());

            for (JDPicInfo jdPicInfo : picList) {

                if (!Utils.isEmpty(jdPicInfo.getFilePath()) && jdPicInfo.getIsAddFlag() != 1) {

                    taskPool.addTask(new UpLoadResPicAPI(userInfoBean.getAccess_token(), jdPicInfo, this));
                }
            }
            List<SoundInfo> soundInfoList = jdSoundDao.getDataByJDId(bean.getId());

            for (SoundInfo soundInfo : soundInfoList) {

                if (!Utils.isEmpty(soundInfo.getFilePath()))
                    taskPool.addTask(new UpLoadSoundAPI(userInfoBean.getAccess_token(),soundInfo,this));
            }

            bean.setPic(picList);
            bean.setAudio(soundInfoList);




            taskPool.addTask(new SaveResDataAPI(Values.TOKEN,bean,this));



        }
        taskPool.execute(this);
    }



    private void bianLi() {
        List<ResDataBean> formBeens = formDao.getUnUpLoadDataList();
        for (ResDataBean bean : formBeens) {

            List<JDPicInfo> picList = jdPicDao.getDataByJDId(bean.getId());

            for (JDPicInfo jdPicInfo : picList) {

                if (!Utils.isEmpty(jdPicInfo.getFilePath()))
                    taskPool.addTask(new UpLoadResPicAPI(userInfoBean.getAccess_token(),jdPicInfo,this));
            }
            List<SoundInfo> soundInfoList = jdSoundDao.getDataByJDId(bean.getId());

            for (SoundInfo soundInfo : soundInfoList) {

                if (!Utils.isEmpty(soundInfo.getFilePath()))
                    taskPool.addTask(new UpLoadSoundAPI(userInfoBean.getAccess_token(),soundInfo,this));
            }

            bean.setPic(picList);
            bean.setAudio(soundInfoList);




            taskPool.addTask(new SaveResDataAPI(Values.TOKEN,bean,this));



        }
        taskPool.execute(this);
    }





    public static String getFileName(String url) {
        String filename = "";
        boolean isok = false;
        // 从UrlConnection中获取文件名称
        try {
            URL myURL = new URL(url);

            URLConnection conn = myURL.openConnection();
            if (conn == null) {
                return null;
            }
            Map<String, List<String>> hf = conn.getHeaderFields();
            if (hf == null) {
                return null;
            }
            Set<String> key = hf.keySet();
            if (key == null) {
                return null;
            }

            for (String skey : key) {
                List<String> values = hf.get(skey);
                for (String value : values) {
                    String result;
                    try {
                        result = new String(value.getBytes("ISO-8859-1"), "GBK");
                        int location = result.indexOf("filename");
                        if (location >= 0) {
                            result = result.substring(location
                                    + "filename".length());
                            filename = result
                                    .substring(result.indexOf("=") + 1);
                            isok = true;
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }// ISO-8859-1 UTF-8 gb2312
                }
                if (isok) {
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 从路径中获取
        if (filename == null || "".equals(filename)) {
            filename = url.substring(url.lastIndexOf("/") + 1);
        }
        return filename;
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void OnNext() {
        taskPool.execute(this);
    }

    @Override
    public void OnResult(boolean isOk, String msg, Object object) {
        if (isOk) {
            ResDataBean bean = (ResDataBean) object;
            if (bean != null) {
                formDao.addOrUpdate(bean);
                taskPool.execute(this);

            }
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void loginResult(boolean isOk, String msg, String access_token, String token_type) {

    }
}































