//package com.hollysmart.popuwindows;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import com.hollysmart.beans.QunZuBean;
//import com.hollysmart.smart_jinrong.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Lenovo on 2018/3/16
// */
//
//public class LianXiRenFenZuWindow {
//    private int unitype;
//    private PopupWindow mPopupWindow;
//    private Context mContext;
//    private PopupAdapter popupAdapter;
//    private List<QunZuBean> dataList;
//
//    PopupQuyuIF popupIF;
//
//    public LianXiRenFenZuWindow(Context mContext, List<QunZuBean> list, int unitype) {
//        this.unitype = unitype;
//        this.mContext = mContext;
//        initPopupWindow();
//        initDate(list);
//    }
//
//    private ListView lv_popup1;
//
//    private void initPopupWindow() {
//        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.layout_popup_2, null);
//        lv_popup1 = view.findViewById(R.id.lv_popup1);
//        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismissWindow();
//            }
//        });
//
//        lv_popup1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                for (QunZuBean diaglogBean : dataList) {
//                    if (diaglogBean.isSelect())
//                        diaglogBean.setSelect(false);
//                }
//                dataList.get(position).setSelect(true);
//                popupIF.item(unitype, position, dataList.get(position));
//                dismissWindow();
//
//            }
//        });
//    }
//
//    private void initDate(List<QunZuBean> list) {
//        dataList = new ArrayList<>();
//        QunZuBean bean = new QunZuBean();
//        bean.setName("全部");
//        bean.setSelect(true);
//        dataList.add(0,bean);
//        dataList.addAll(list);
//
//    }
//
//    public void showPopuWindow(final View view) {
//        if (popupAdapter == null) {
//            popupAdapter = new PopupAdapter(dataList);
//            lv_popup1.setAdapter(popupAdapter);
//
//        } else {
//            popupAdapter.notifyDataSetChanged();
//        }
//
//        Drawable win_bg = mContext.getResources().getDrawable(R.color.baise);
//        mPopupWindow.setBackgroundDrawable(win_bg);
//        mPopupWindow.setOutsideTouchable(true);
//
//        mPopupWindow.showAsDropDown(view);
//        mPopupWindow.setFocusable(true);
//    }
//
//    public boolean isShowing() {
//        if (mPopupWindow != null) {
//            if (mPopupWindow.isShowing()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void dismissWindow() {
//        if (mPopupWindow != null && mPopupWindow.isShowing()) {
//            mPopupWindow.dismiss();
//        }
//    }
//
//
//    public void setPopupListener(final PopupQuyuIF if1) {
//        this.popupIF = if1;
//    }
//
//    private class PopupAdapter extends BaseAdapter {
//
//        private List<QunZuBean> quyuBeanList;
//
//        public PopupAdapter(List<QunZuBean> quyuBeanList) {
//            this.quyuBeanList = quyuBeanList;
//        }
//
//        @Override
//        public int getCount() {
//            return quyuBeanList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            final ViewHolder holder;
//
//            if (convertView != null && convertView.getTag() != null) {
//                holder = (ViewHolder) convertView.getTag();
//            } else {
//                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pupu2, null);
//                holder = new ViewHolder();
//                holder.tv_title = convertView.findViewById(R.id.tv_title);
//                holder.iv_view = convertView.findViewById(R.id.iv_view);
//                convertView.setTag(holder);
//            }
//
//
//            String title = quyuBeanList.get(position).getName();
//            holder.tv_title.setText(title);
//
//            if (quyuBeanList.get(position).isSelect()) {
//                holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.text_lan));
//                holder.iv_view.setImageResource(R.mipmap.icon_lanse_duihao);
//                holder.iv_view.setVisibility(View.VISIBLE);
//            } else {
//                holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.text_hei));
//                holder.iv_view.setVisibility(View.GONE);
//            }
//            return convertView;
//        }
//
//        private class ViewHolder {
//            TextView tv_title;
//            ImageView iv_view;
//        }
//    }
//
//
//    public interface PopupQuyuIF {
//        void item(int unitype, int position, QunZuBean bean);
//    }
//
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
