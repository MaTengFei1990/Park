package com.hollysmart.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.park.BigPicActivity;
import com.hollysmart.park.Cai_AddPicActivity;
import com.hollysmart.park.R;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;
import com.hollysmart.views.linearlayoutforlistview.LinearLayoutBaseAdapter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemPicAdater extends LinearLayoutBaseAdapter {

    private List<JDPicInfo> jdPicslist;
    private Context contextlist;

    private Context context;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private final int MAXNUM = 9;

    private List<JDPicInfo> deletPicList = new ArrayList<>();

    private static JDPicInfo picBeannull = new JDPicInfo(0, null, null, null, 1, "false");

    public ItemPicAdater(Context context, List<JDPicInfo> list) {
        super(context, list);
        this.context = context;
        this.jdPicslist = list;
        this.contextlist = context;
    }

    @Override
    public View getView(final int position) {
        final JDPicInfo jdPicInfo = jdPicslist.get(position);

        View convertView = View.inflate(contextlist, R.layout.item_jingdian_pic, null);
        ImageView imageView = convertView.findViewById(R.id.photo);
        ImageView iv_del = convertView.findViewById(R.id.iv_del);

        //当前item要加载的图片路径
        //使用谷歌官方提供的Glide加载图片
        if (jdPicInfo.getIsAddFlag() == 1) {
            iv_del.setVisibility(View.GONE);
            if (contextlist != null && imageView != null) {
                Glide.with(contextlist)
                        .load(R.mipmap.takepic)
                        .centerCrop().into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            Activity activity = (Activity) context;
                            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
                        } else {
                            Activity activity = (Activity) context;
                            Intent intent = new Intent(contextlist, Cai_AddPicActivity.class);
                            intent.putExtra("num", MAXNUM + 1 - jdPicslist.size());
                            activity.startActivityForResult(intent, 1);
                        }

                    }
                });
            }
        } else {
            if (!Utils.isEmpty(jdPicInfo.getImageUrl())) {
                Glide.with(contextlist)
                        .load(Values.SERVICE_URL_ADMIN_FORM + jdPicInfo.getImageUrl())
                        .centerCrop().into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(contextlist, BigPicActivity.class);
                        intent.putExtra("infos", (Serializable) jdPicslist);
                        intent.putExtra("index", position);
                        context.startActivity(intent);
                    }
                });

            } else {
                Glide.with(contextlist)
                        .load(new File(jdPicInfo.getFilePath()))
                        .centerCrop().into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(contextlist, BigPicActivity.class);
                        intent.putExtra("infos", (Serializable) jdPicslist);
                        intent.putExtra("index", position);
                        context.startActivity(intent);
                    }
                });

            }

        }
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deletPicList.add(jdPicslist.get(position));


                jdPicslist.remove(position);
                if (!jdPicslist.contains(picBeannull)) {
                    jdPicslist.add(picBeannull);

                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
