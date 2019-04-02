package com.hollysmart.interfaces;


import android.view.View;


public class MyInterfaces{
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface PopupIF {
        void onListener();
        void item(int position);
    }

    public interface IUpdateModel {
        void commit();
    }

    public interface OnUpDateListener {
        void onSuccessUpdatePic();
        void onFailed(String msg);
    }


}
