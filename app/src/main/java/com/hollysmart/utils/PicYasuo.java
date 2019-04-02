//package com.hollysmart.utils;
//
//import android.os.AsyncTask;
//
//import com.hollysmart.beans.LianXiRenBean;
//import com.hollysmart.value.Values;
//
//
///**
// * Created by sunpengfei on 2017/11/6.
// */
//
//public class PicYasuo extends AsyncTask<Void,Void,Boolean> {
//
//    private LianXiRenBean info;
//    private String filePath;
//    private YaSuoIF yaSuoIF;
//
//    public PicYasuo(LianXiRenBean info, YaSuoIF yaSuoIF) {
//        this.info = info;
//        this.yaSuoIF = yaSuoIF;
//    }
//
//    @Override
//    protected Boolean doInBackground(Void... voids) {
//        try {
//            String str[] = info.getPicPath().split("/");
//            String picName = str[str.length-1];
//
//            CCM_Bitmap.getBitmapToFile(CCM_Bitmap.ratio(info.getPicPath(), 600f, 600f), Values.SDCARD_FILE(Values.SDCARD_PIC) + picName);
//            filePath = Values.SDCARD_FILE(Values.SDCARD_PIC) + picName;
//            info.setPicPath(filePath);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//    @Override
//    protected void onPostExecute(Boolean b) {
//        super.onPostExecute(b);
//        if (b) {
//            yaSuoIF.YaSuo(info);
//        }else{
//        }
//    }
//
//    public interface YaSuoIF{
//        void YaSuo(LianXiRenBean picBean);
//    }
//
//}
