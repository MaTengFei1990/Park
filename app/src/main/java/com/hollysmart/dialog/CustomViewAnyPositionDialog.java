//package com.hollysmart.dialog;
//
//import android.animation.Animator;
//import android.animation.ObjectAnimator;
//import android.animation.ValueAnimator;
//import android.app.DialogFragment;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.animation.BounceInterpolator;
//import android.view.animation.TranslateAnimation;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import com.hollysmart.animtor.KickBackAnimator;
//import com.hollysmart.formlib.activitys.EditPicActivity;
//import com.hollysmart.park.EditTextActivity;
//import com.hollysmart.park.R;
//
///**
// * Created by Lenovo on 2018/5/28.
// */
//public class CustomViewAnyPositionDialog extends DialogFragment implements View.OnClickListener {
//    private static CustomViewAnyPositionDialog customViewAnyPositionDialog;
//    private static Context context;
//
//    public static CustomViewAnyPositionDialog getInstance(Context mcontext) {
//        context = mcontext;
//        if (customViewAnyPositionDialog == null) {
//            synchronized (CustomViewAnyPositionDialog.class) {
//                if (customViewAnyPositionDialog == null) {
//                     customViewAnyPositionDialog = new CustomViewAnyPositionDialog();
//                }
//            }
//        }
//        return customViewAnyPositionDialog;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //1 通过样式定义,DialogFragment.STYLE_NORMAL这个很关键的
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog1);
//
//        //2代码设置 无标题 无边框  这个就很坑爹，这么设置很多系统效果就都没有了
//        //setStyle(DialogFragment.STYLE_NO_TITLE|DialogFragment.STYLE_NO_FRAME,0);
//
//    }
//
//    private RelativeLayout rl_all;
//
//    private ImageView iv_wenZi;
//    private ImageView rv_pic;
//    private ImageView rv_qianDao;
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.activity_publish, container, false);
////        view.findViewById(R.id.ll_editText).setOnClickListener(this);
////        view.findViewById(R.id.ll_pic).setOnClickListener(this);
////        view.findViewById(R.id.ll_qianDao).setOnClickListener(this);
////        view.findViewById(R.id.ll_finish).setOnClickListener(this);
//        rl_all = view.findViewById(R.id.rl_all);
//
////        iv_wenZi = view.findViewById(R.id.iv_wenZi);
////        rv_pic = view.findViewById(R.id.rv_pic);
////        rv_qianDao = view.findViewById(R.id.rv_qianDao);
//        byte[] bitmaps = getArguments().getByteArray("bitmap");
//
//        Bitmap bitmap= BitmapFactory.decodeByteArray(bitmaps, 0, bitmaps.length);
//        Drawable drawable = new BitmapDrawable(bitmap);
//        rl_all.setBackground(drawable);
//
//        final RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.activity_publish, null);
//        showAnimation(layout);
//
//         return view;
//
//
//    }
//
//
//
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        //注意下面这个方法会将布局的根部局忽略掉，所以需要嵌套一个布局
//        Window dialogWindow = getDialog().getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.windowAnimations = R.style.BottomDialogAnimation;
//        dialogWindow.setAttributes(lp);
//
//        getDialog().setCancelable(false);
//        getDialog().setCanceledOnTouchOutside(false);
//        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {//可以在这拦截返回键啊home键啊事件
//                dialog.dismiss();
//                return false;
//            }
//        });
//    }
//
//    @Override
//    public void onClick(View v) {
//        Intent intent;
//        switch (v.getId()) {
//            case R.id.tv_text:
//                intent = new Intent(getActivity(), EditTextActivity.class);
//                startActivity(intent);
//                dismiss();
//                break;
//            case R.id.tv_lbs:
//                intent = new Intent(getActivity(), EditPicActivity.class);
//                startActivity(intent);
//                dismiss();
//                break;
//            case R.id.tv_camera:
//                break;
//            case R.id.tv_review:
//                dismiss();
//                break;
//        }
//    }
//
//    private View child;
//    private void showAnimation(ViewGroup layout) {
//        int childcount=layout.getChildCount();
//        for (int i = 0; i < childcount; i++) {
//             child = layout.getChildAt(i);
//            if (child.getId() == R.id.iv_window_close) {
//                continue;
//            }
//            child.setOnClickListener(this);
//            child.setVisibility(View.INVISIBLE);
//            mHandler.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    child.setVisibility(View.VISIBLE);
//                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 900, 0);
//                    fadeAnim.setDuration(600);
//                    KickBackAnimator kickAnimator = new KickBackAnimator();
//                    kickAnimator.setDuration(150);
//                    fadeAnim.setEvaluator(kickAnimator);
//                    fadeAnim.start();
//                }
//            }, i * 50);
//        }
//
//    }
//
//
//    private Handler mHandler = new Handler();
//
//    private void closeAnimation(ViewGroup layout) {
//        for (int i = 0; i < layout.getChildCount(); i++) {
//            final View child = layout.getChildAt(i);
//            if (child.getId() == R.id.iv_window_close) {
//                continue;
//            }
//            child.setOnClickListener(this);
//            mHandler.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    child.setVisibility(View.VISIBLE);
//                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0, 600);
//                    fadeAnim.setDuration(200);
//                    KickBackAnimator kickAnimator = new KickBackAnimator();
//                    kickAnimator.setDuration(100);
//                    fadeAnim.setEvaluator(kickAnimator);
//                    fadeAnim.start();
//                    fadeAnim.addListener(new Animator.AnimatorListener() {
//
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            // TODO Auto-generated method stub
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animation) {
//                            // TODO Auto-generated method stub
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            child.setVisibility(View.INVISIBLE);
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animation) {
//                            // TODO Auto-generated method stub
//
//                        }
//                    });
//                }
//            }, (layout.getChildCount() - i - 1) * 30);
//
////            if (child.getId() == R.id.tv_text) {
////                mHandler.postDelayed(new Runnable() {
////
////                    @Override
////                    public void run() {
////                        dismiss();
////                    }
////                }, (layout.getChildCount() - i) * 30 + 80);
////            }
//        }
//
//    }
//}
