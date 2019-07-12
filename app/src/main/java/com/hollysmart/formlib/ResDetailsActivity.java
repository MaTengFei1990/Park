package com.hollysmart.formlib;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.GPS;
import com.hollysmart.formlib.adapters.BiaoGeRecyclerAdapter2;
import com.hollysmart.formlib.apis.ResDataGetAPI;
import com.hollysmart.formlib.beans.DongTaiFormBean;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.formlib.beans.FormModelBean;
import com.hollysmart.formlib.beans.ResDataBean;
import com.hollysmart.db.UserInfo;
import com.hollysmart.park.R;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.GPSConverterUtils;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResDetailsActivity extends StyleAnimActivity {

    @Override
    public int layoutResID() {
        return R.layout.activity_res_details;
    }

    @BindView(R.id.ib_back)
    ImageView ib_back;

    @BindView(R.id.iv_maplsit)
    ImageView iv_maplsit;

    @BindView(R.id.lv_jingdian)
    RecyclerView lv_jingdian;


    private List<JDPicInfo> picList; // 当前景点图片集
    private List<String> soundList; // 当前景点录音集


    private ResDataBean resDataBean;

    private List<DongTaiFormBean> formBeanList = new ArrayList<>();

    private HashMap<String, List<JDPicInfo>> formPicMap = new HashMap<>();


    @Override
    public void findView() {
        ButterKnife.bind(this);
        ib_back.setOnClickListener(this);
        iv_maplsit.setOnClickListener(this);
    }

    private BiaoGeRecyclerAdapter2 fromshowAdapter;


    @Override
    public void init() {
        isLogin();
        picList = new ArrayList<>();
        soundList = new ArrayList<>();
        resDataBean = (ResDataBean) getIntent().getSerializableExtra("resDataBean");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        lv_jingdian.setLayoutManager(layoutManager);
        fromshowAdapter = new BiaoGeRecyclerAdapter2(mContext, formBeanList,true);
        lv_jingdian.setAdapter(fromshowAdapter);
        selectDB();



    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
        }
    }


    // 查询
    private void selectDB() {

        if (resDataBean.getFormData() == null) {
            new ResDataGetAPI(userInfo.getAccess_token(), resDataBean, new ResDataGetAPI.ResDataDeleteIF() {
                @Override
                public void onResDataDeleteResult(boolean isOk, ResDataBean resDataBen) {

                    if (isOk) {
                        String formData = resDataBen.getFormData();

                        formBeanList.clear();
                        try {
                            if (!Utils.isEmpty(formData)) {

                                JSONObject jsonObject = null;
                                jsonObject = new JSONObject(formData);
                                Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                                List<DongTaiFormBean> dictList = mGson.fromJson(jsonObject.getString("cgformFieldList"),
                                        new TypeToken<List<DongTaiFormBean>>() {
                                        }.getType());

                                formBeanList.addAll(dictList);

                                getFormPicMap(formBeanList);

                                picAdd2From(formPicMap, formBeanList);

                                fromshowAdapter.notifyDataSetChanged();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }).request();

        } else {

            String formData = resDataBean.getFormData();

            formBeanList.clear();
            try {
                if (!Utils.isEmpty(formData)) {

                    JSONObject jsonObject = null;
                    jsonObject = new JSONObject(formData);
                    Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                    List<DongTaiFormBean> dictList = mGson.fromJson(jsonObject.getString("cgformFieldList"),
                            new TypeToken<List<DongTaiFormBean>>() {
                            }.getType());

                    formBeanList.addAll(dictList);
                    getwgps2bd(formBeanList);
                    getFormPicMap(formBeanList);
                    picAdd2From(formPicMap, formBeanList);
                    fromshowAdapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }


    private void getwgps2bd( List<DongTaiFormBean> formBeanList) {
        for (int i = 0; i < formBeanList.size(); i++) {

            DongTaiFormBean formBean = formBeanList.get(i);

            if (formBean.getJavaField().equals("location")) {

                String propertyLabel = formBean.getPropertyLabel();

                if (!Utils.isEmpty(propertyLabel)) {
                    String[] split = propertyLabel.split(",");

                    GPS gps = GPSConverterUtils.Gps84_To_bd09(Double.parseDouble(split[0]),
                            Double.parseDouble(split[1]));

                    formBean.setPropertyLabel(gps.getLat() + "," + gps.getLon());
                }


            }


        }


    }




    private void getFormPicMap(List<DongTaiFormBean> formBeans) {

        for (int i = 0; i < formBeans.size(); i++) {
            DongTaiFormBean formBean = formBeans.get(i);

            if (formBean.getPic() != null && formBean.getPic().size() > 0) {
                formPicMap.put(formBean.getJavaField(), formBean.getPic());

            }else {

                if (formBean.getShowType().equals("image")) {

                    if (!Utils.isEmpty(formBean.getPropertyLabel())) {
                        String[] split = formBean.getPropertyLabel().split(",");
                        List<JDPicInfo> picInfos = new ArrayList<>();

                        for (int k = 0; k < split.length; k++) {

                            JDPicInfo jdPicInfo = new JDPicInfo();

                            jdPicInfo.setImageUrl(split[k]);
                            jdPicInfo.setIsDownLoad("true");
                            jdPicInfo.setIsAddFlag(0);

                            picInfos.add(jdPicInfo);
                        }
                        if (picInfos != null && picInfos.size() > 0) {

                            formPicMap.put(formBean.getJavaField(), picInfos);
                        }


                    }


                }

            }

            if (formBean.getCgformFieldList() != null && formBean.getCgformFieldList().size() > 0) {

                List<DongTaiFormBean> childList = formBean.getCgformFieldList();

                for (int j = 0; j < childList.size(); j++) {

                    DongTaiFormBean childbean = childList.get(j);

                    if (childbean.getPic() != null && childbean.getPic().size() > 0) {
                        formPicMap.put(childbean.getJavaField(), childbean.getPic());

                    }else {

                        if (childbean.getShowType().equals("image")) {

                            if (!Utils.isEmpty(childbean.getPropertyLabel())) {
                                String[] split = childbean.getPropertyLabel().split(",");
                                List<JDPicInfo> picInfos = new ArrayList<>();

                                for (int k = 0; k < split.length; k++) {

                                    JDPicInfo jdPicInfo = new JDPicInfo();

                                    jdPicInfo.setImageUrl(split[k]);
                                    jdPicInfo.setIsDownLoad("true");
                                    jdPicInfo.setIsAddFlag(0);

                                    picInfos.add(jdPicInfo);
                                }
                                if (picInfos != null && picInfos.size() > 0) {

                                    formPicMap.put(childbean.getJavaField(), picInfos);
                                }


                            }


                        }


                    }



                }

            }

        }

    }


    private void picAdd2From(HashMap<String, List<JDPicInfo>> formPicMap, List<DongTaiFormBean> formBeans) {

        for (int i = 0; i < formBeans.size(); i++) {
            DongTaiFormBean formBean = formBeans.get(i);

            if (formBean.getShowType().equals("image")) {
                if (formPicMap != null && formPicMap.size() > 0) {
                    List<JDPicInfo> picInfos = formPicMap.get(formBean.getJavaField());
                    formBean.setPic(picInfos);

                }


            }

            if (formBean.getCgformFieldList() != null && formBean.getCgformFieldList().size() > 0) {

                List<DongTaiFormBean> childList = formBean.getCgformFieldList();

                for (int j = 0; j < childList.size(); j++) {

                    DongTaiFormBean childbean = childList.get(j);

                    if (childbean.getShowType().equals("image")) {

                        if (formPicMap != null && formPicMap.size() > 0) {
                            List<JDPicInfo> picInfos = formPicMap.get(childbean.getJavaField());
                            childbean.setPic(picInfos);

                        }


                    }


                }



            }

        }


    }


    /**
     * 判断用户登录状态，登录获取用户信息
     */
    private UserInfo userInfo;

    public boolean isLogin() {
        if (userInfo != null)
            return true;
        try {
            String userPath = Values.SDCARD_FILE(Values.SDCARD_CACHE) + Values.CACHE_USER;
            Object obj = ACache.get(new File(userPath)).getAsObject(Values.CACHE_USERINFO);
            if (obj != null) {
                userInfo = (UserInfo) obj;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */

            //Glide 加载图片简单用法
            JDPicInfo jdPicInfo = (JDPicInfo) path;

            Glide.with(context)
                    .load(Values.SERVICE_URL_ADMIN_FORM+jdPicInfo.getImageUrl())
                    .into(imageView);


        }

        //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
        @Override
        public ImageView createImageView(Context context) {
            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
            ImageView simpleDraweeView = new ImageView(context);
            return simpleDraweeView;
        }
    }


    private class FromshowAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private List<DongTaiFormBean> formBeanList;

        private Context context;

        public FromshowAdapter(Context context, List<DongTaiFormBean> formBeanList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.formBeanList = formBeanList;
        }

        @Override
        public int getCount() {
            return formBeanList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup arg2) {

            ViewHolder holder;
            if (convertView != null && convertView.getTag() != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = inflater.inflate(R.layout.item_showforms, null);
                holder = new ViewHolder();
                holder.tv_key = convertView
                        .findViewById(R.id.tv_key);
                holder.tv_value = convertView
                        .findViewById(R.id.tv_value);
                convertView.setTag(holder);
            }

            holder.tv_key.setText(formBeanList.get(position).getContent());
            holder.tv_value.setText(formBeanList.get(position).getPropertyLabel());
            return convertView;
        }

        class ViewHolder {
            TextView tv_key;
            TextView tv_value;
        }


    }

}
