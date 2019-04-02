package com.hollysmart.dialog;

/**
 * Created by Lenovo on 2019/1/9.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hollysmart.park.R;

/**
 * 自定义底部弹出对话框
 * Created by Ma on 2017/9/8.
 */

public class ButtomDialogView extends Dialog {

    private boolean iscancelable;//控制点击dialog外部是否dismiss
    private boolean isBackCancelable;//控制返回键是否dismiss
    private View view;
    private Context context;

    private shareListener shareListener;
    private SaveLocalListener saveLocalListener;
    //这里的view其实可以替换直接传layout过来的 因为各种原因没传(lan)
    public ButtomDialogView(Context context, View view, boolean isCancelable,boolean isBackCancelable,shareListener shareListener,SaveLocalListener saveLocalListener) {
        super(context, R.style.MyDialog);

        this.context = context;
        this.view = view;
        this.iscancelable = isCancelable;
        this.isBackCancelable = isBackCancelable;

        this.shareListener = shareListener;
        this.saveLocalListener = saveLocalListener;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(view);//这行一定要写在前面
        setCancelable(iscancelable);//点击外部不可dismiss
        setCanceledOnTouchOutside(isBackCancelable);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 500;
        window.setAttributes(params);

        view.findViewById(R.id.tv_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareListener.share();

            }
        });
        view.findViewById(R.id.tv_save_local).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveLocalListener.saveLocal();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
    }


   public interface  shareListener{
       void share();
    }

    public interface  SaveLocalListener{
       void saveLocal();
    }
}
