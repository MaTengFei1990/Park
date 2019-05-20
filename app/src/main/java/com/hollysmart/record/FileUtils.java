package com.hollysmart.record;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Lenovo on 2019/3/26.
 */

public class FileUtils {

    //存放的目录路径名称
    private static final String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/AudiioRecordFile";
    //保存的音频文件名


    private File mRecordingFile;//储存AudioRecord录下来的文件


    private File mFileRoot=null;//文件目录

    public static String getPcmFileAbsolutePath(String fileName) {
        File mRecordingFile,mFileRoot;

        mFileRoot   = new File(mPathName);
        if (!mFileRoot.exists()) {
            mFileRoot.mkdirs();//创建文件夹

        }


        //创建一个流，存放从AudioRecord读取的数据
        mRecordingFile = new File(mFileRoot, fileName+".pcm");
        if (mRecordingFile.exists()) {//音频文件保存过了删除
            mRecordingFile.delete();
        }
        try {
            mRecordingFile.createNewFile();//创建新文件
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ma", "创建储存音频文件出错");
        }






        return mRecordingFile.getAbsolutePath();

    }



    public static String getWavFileAbsolutePath(String fileName) {

        String pcmFileAbsolutePath = getPcmFileAbsolutePath(fileName);
        boolean b = PcmToWav.makePCMFileToWAVFile(pcmFileAbsolutePath, mPathName+"/"+fileName+".wav", false);


        if (b) {

            return mPathName+"/"+fileName+".wav";

        } else {
            return null;
        }

    }
}
