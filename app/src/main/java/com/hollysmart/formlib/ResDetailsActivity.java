package com.hollysmart.formlib;

import android.content.Context;
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
import com.hollysmart.apis.ResDataGetAPI;
import com.hollysmart.beans.DongTaiFormBean;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.db.UserInfo;
import com.hollysmart.park.R;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
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
    ListView lv_jingdian;

    @BindView(R.id.banner)
    Banner banner;


    private List<JDPicInfo> picList; // 当前景点图片集
    private List<String> soundList; // 当前景点录音集


    private ResDataBean resDataBean;

    private List<DongTaiFormBean> formBeanList = new ArrayList<>();


    @Override
    public void findView() {
        ButterKnife.bind(this);
        ib_back.setOnClickListener(this);
        iv_maplsit.setOnClickListener(this);
    }

    private FromshowAdapter fromshowAdapter;


    @Override
    public void init() {
        isLogin();
        picList = new ArrayList<>();
        soundList = new ArrayList<>();
        resDataBean = (ResDataBean) getIntent().getSerializableExtra("resDataBean");

        fromshowAdapter = new FromshowAdapter(mContext, formBeanList);
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


                                for (int i = 0; i < dictList.size(); i++) {

                                    DongTaiFormBean formBean = dictList.get(i);

                                    if (formBean.getCgformFieldList() != null && formBean.getCgformFieldList().size() > 0) {

                                        if (formBean.getPropertyLabel() != null) {
                                            if (formBean.getPropertyLabel().equals("是") || formBean.getPropertyLabel().equals("1")) {

                                                List<DongTaiFormBean> cgformFieldList = formBean.getCgformFieldList();

                                                formBeanList.addAll(i+1, cgformFieldList);
                                            }

                                        }





                                    }
                                }

                                fromshowAdapter.notifyDataSetChanged();


                                banner.setImageLoader(new GlideImageLoader());

                                resDataBean.setPic(resDataBen.getPic());

                                List<JDPicInfo> piclist = resDataBean.getPic();

                                if (piclist != null && piclist.size() > 0) {
                                    //设置图片集合
                                    banner.setImages(piclist);
                                    //banner设置方法全部调用完毕时最后调用
                                    banner.start();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }).request();

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
