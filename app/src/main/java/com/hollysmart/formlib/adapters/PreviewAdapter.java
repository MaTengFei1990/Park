package com.hollysmart.formlib.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hollysmart.formlib.beans.ResDataBean;
import com.hollysmart.park.R;

import java.util.List;

/**
 * Created by Lenovo on 2019/3/15.
 */

public class PreviewAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private List<ResDataBean> jinquList;

        public PreviewAdapter(Context context, List<ResDataBean> jinquList) {
            inflater = LayoutInflater.from(context);
            this.jinquList = jinquList;
        }

        @Override
        public int getCount() {
            if (jinquList != null) {

                return jinquList.size();
            } else {
                return 0;
            }
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
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            arg1 = inflater.inflate(R.layout.item_preview, null);


            ResDataBean jqInfo = jinquList.get(arg0);

            TextView tv_name = (TextView) arg1.findViewById(R.id.tv_name);
            ImageView iv_pingji1 = (ImageView) arg1
                    .findViewById(R.id.iv_pingji1);
            ImageView iv_pingji2 = (ImageView) arg1
                    .findViewById(R.id.iv_pingji2);
            ImageView iv_pingji3 = (ImageView) arg1
                    .findViewById(R.id.iv_pingji3);
            ImageView iv_pingji4 = (ImageView) arg1
                    .findViewById(R.id.iv_pingji4);
            ImageView iv_pingji5 = (ImageView) arg1
                    .findViewById(R.id.iv_pingji5);
            TextView tv_dianzan = (TextView) arg1.findViewById(R.id.tv_dianzan);
            LinearLayout ll_detail = (LinearLayout) arg1
                    .findViewById(R.id.ll_detail);
            LinearLayout ll_daohang = (LinearLayout) arg1.findViewById(R.id.ll_daohang);


            tv_name.setText(jqInfo.getFd_resname());


            return arg1;
        }
}
