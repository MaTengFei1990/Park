package com.hollysmart.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.hollysmart.beans.DictionaryBean;
import com.hollysmart.interfaces.SelectIF;

import java.util.List;

/**
 * Created by cai on 2017/9/20
 */

public class BsSelectDialog {

    private BsSelectIF bsSelectIF;
    private PositiveIF positiveIF;
    public BsSelectDialog(BsSelectIF bsSelectIF) {
        this.bsSelectIF = bsSelectIF;
    }

    public void showPopuWindow(Context mContext, final int type, String title, List<SelectIF> beanList) {
        String strs[] = new String[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            strs[i] = beanList.get(i).showInfo();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);

        builder.setItems(strs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bsSelectIF.onBsSelect(type, which);
            }
        }).show();
    }

    public interface BsSelectIF {
        void onBsSelect(int type, int index);
    }
    public interface PositiveIF {
        void queDing();
    }


    public void showPopuWindow_DictListData(Context mContext, final int type, String title, List<DictionaryBean> beanList) {
        String strs[] = new String[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            strs[i] = beanList.get(i).getLabel();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);

        builder.setItems(strs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bsSelectIF.onBsSelect(type, which);
            }
        }).show();
    }


}























