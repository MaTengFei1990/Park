package com.hollysmart.popuwindow;

/**
 * Created by Lenovo on 2018/10/22.
 */

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.hollysmart.animtor.KickBackAnimator;
import com.hollysmart.park.EditPicActivity;
import com.hollysmart.park.EditTextActivity;
import com.hollysmart.park.R;
import com.hollysmart.park.VoiceCallingActivity;
import com.hollysmart.utils.fastBlur.FastBlur;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：弹出窗口
 * Created by AsiaLYF on 2018/3/22.
 */

public class MoreWindow extends PopupWindow implements View.OnClickListener {

    Activity mContext;
    private int mWidth;
    private int mHeight;
    private int statusBarHeight;
    private Bitmap mBitmap = null;
    private Bitmap overlay = null;
    private int  roleid;

    private Handler mHandler = new Handler();

    public MoreWindow(Activity context,int roleid) {
        mContext = context;
        this.roleid = roleid;
    }

    public void init() {
        Rect frame = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;

        setWidth(mWidth);
        setHeight(mHeight);
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    private Bitmap blur() {
        if (null != overlay) {
            return overlay;
        }
        long startMs = System.currentTimeMillis();

        View view = mContext.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        mBitmap = view.getDrawingCache();

        float scaleFactor = 8;//图片缩放比例
        float radius = 10;//模糊程度
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        overlay = Bitmap.createBitmap((int) (width / scaleFactor), (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        return overlay;
    }

    private  RelativeLayout layout;


    public void showMoreWindow(View anchor, int bottomMargin) {
         layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.activity_publish, null);
        setContentView(layout);

        ImageView close = layout.findViewById(R.id.iv_window_close);
//        android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.bottomMargin = bottomMargin;
//        params.addRule(RelativeLayout.BELOW, R.id.tv_photo);
////        params.addRule(RelativeLayout.RIGHT_OF, R.id.tv_text);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT, R.id.tv_text);
//        params.topMargin = 200;
//        params.leftMargin = 50;
//        close.setLayoutParams(params);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    closeAnimation(layout);
                }
            }

        });

        showAnimation(layout);
        setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
        setOutsideTouchable(true);
        setFocusable(true);
        showAtLocation(anchor, Gravity.BOTTOM, 0, statusBarHeight);
    }

    private void showAnimation(ViewGroup layout) {
        List<View> lsitView = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View childs = layout.getChildAt(i);

            if (childs instanceof ViewGroup) {
                for (int j = 0; j < ((ViewGroup) childs).getChildCount(); j++) {
                    final View childView = ((ViewGroup) childs).getChildAt(j);
                    lsitView.add(childView);
                }

            } else {
                lsitView.add(childs);
            }

        }


        for(int n=0;n<lsitView.size();n++) {
            final View child = lsitView.get(n);

            if (child.getId() == R.id.iv_window_close) {
                continue;
            }
            child.setOnClickListener(this);
            child.setVisibility(View.INVISIBLE);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
                    fadeAnim.setDuration(300);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(150);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                }
            }, n * 50);
        }

    }

    private void closeAnimation(ViewGroup layout) {
        List<View> lsitView = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View childs = layout.getChildAt(i);

            if (childs instanceof ViewGroup) {
                for (int j = 0; j < ((ViewGroup) childs).getChildCount(); j++) {
                    final View childView = ((ViewGroup) childs).getChildAt(j);
                    lsitView.add(childView);
                }

            } else {
                lsitView.add(childs);
            }

        }

        for(int n=0;n<lsitView.size();n++) {
           final View child = lsitView.get(n);

            if (child.getId() == R.id.iv_window_close) {
                continue;
            }
            child.setOnClickListener(this);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0, 600);
                    fadeAnim.setDuration(200);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(100);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                    fadeAnim.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            child.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            // TODO Auto-generated method stub

                        }
                    });
                }
            }, (layout.getChildCount() - n - 1) * 30);

            if (child.getId() == R.id.tv_text) {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        dismiss();
                    }
                }, (layout.getChildCount() - n) * 30 + 80);
            }
        }


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_text:
                intent = new Intent(mContext, EditTextActivity.class);
                intent.putExtra("roleid",roleid);
                mContext.startActivity(intent);
                dismiss();
                break;
            case R.id.tv_photo:
                intent = new Intent(mContext, EditPicActivity.class);
                intent.putExtra("roleid",roleid);
                mContext.startActivity(intent);
                dismiss();
                break;
//            case R.id.tv_chat:
////                intent = new Intent(mContext, ConferenceSendActivity.class);
//                intent = new Intent(mContext, VoiceCallingActivity.class);
//                intent.putExtra("roleid",roleid);
//                mContext.startActivity(intent);
//                dismiss();
//                break;
            default:
                break;
        }
    }

    public void destroy() {
        if (null != overlay) {
            overlay.recycle();
            overlay = null;
            System.gc();
        }
        if (null != mBitmap) {
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }
    }
}