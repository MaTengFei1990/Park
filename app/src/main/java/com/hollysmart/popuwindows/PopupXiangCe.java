package com.hollysmart.popuwindows;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hollysmart.interfaces.MyInterfaces;
import com.hollysmart.park.R;
import com.hollysmart.utils.loctionpic.ImageBucket;

import java.util.List;


/**
 * Created by cai on 16/10/10
 */

public class PopupXiangCe {
    private PopupWindow mPopupWindow;
    private Context mContext;
    private PopupAdapter popupAdapter;

    public PopupXiangCe(Context mContext) {
        this.mContext = mContext;
        initPopupWindow();
    }

    private ListView lv_popup;
    public void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_popup, null);
        lv_popup = (ListView) view.findViewById(R.id.lv_popup);
        mPopupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissWindow();
            }
        });

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popupIF != null)
                    popupIF.onListener();
                if (popup2IF != null)
                    popup2IF.onListener();
            }
        });

        lv_popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (popupIF != null) {
                    popupIF.item(position);
                    dismissWindow();
                }
                if (popup2IF != null) {
                    popup2IF.item(position);
                    dismissWindow();
                }
            }
        });
    }

    public void showPopuWindow(final View view , List<ImageBucket> popupInfoList) {
        if (popupAdapter == null){
            popupAdapter = new PopupAdapter(popupInfoList);
            lv_popup.setAdapter(popupAdapter);
        }else {
            popupAdapter.notifyDataSetChanged();
        }

        Drawable win_bg = mContext.getResources().getDrawable(R.color.heise_b_80);
        mPopupWindow.setBackgroundDrawable(win_bg);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAsDropDown(view);
        mPopupWindow.setFocusable(true);
    }

    public boolean isShowing() {
        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing()) {
                return true;
            }
        }
        return false;
    }

    public void dismissWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }


    private MyInterfaces.PopupIF popupIF;
    public void setPopupListener(final MyInterfaces.PopupIF if1) {
        this.popupIF = if1;
    }

    private MyInterfaces.PopupIF popup2IF;
    public void setPopup2Listener(final MyInterfaces.PopupIF if2) {
        this.popup2IF = if2;
    }

    private class PopupAdapter extends BaseAdapter {
        private List<ImageBucket> popupInfos;
        public PopupAdapter(List<ImageBucket> popupInfos) {
            this.popupInfos = popupInfos;
        }
        @Override
        public int getCount() {
            return popupInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView != null && convertView.getTag() != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popup, null);
                holder = new ViewHolder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            }

            String title = popupInfos.get(position).bucketName;
            title += "(" + popupInfos.get(position).count +")";

            holder.tv_title.setText(title);
            if (popupInfos.get(position).isTag){
                holder.tv_title.setBackgroundResource(R.color.baise);
                holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.text_juhuang));
            }else {
                holder.tv_title.setBackgroundResource( R.color.bg_hui);
                holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.huise_hint));
            }
            return convertView;
        }
        private class ViewHolder{
            TextView tv_title;
        }
    }

}
















































