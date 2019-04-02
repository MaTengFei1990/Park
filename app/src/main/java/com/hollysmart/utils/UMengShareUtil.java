package com.hollysmart.utils;

import android.app.Activity;
import android.content.Intent;

import com.hollysmart.park.R;
import com.hollysmart.value.Values;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.ShareBoardConfig;

/**
 * Created by cai on 16/3/14.
 */
public class UMengShareUtil {

    private Activity activity;

    private ShareAction mShareAction;

    public UMengShareUtil(Activity activity) {
        this.activity = activity;
        mShareAction = new ShareAction(activity);

        boolean qq = activity.getResources().getBoolean(R.bool.qq);
        boolean weixin = activity.getResources().getBoolean(R.bool.weixin);

        if (qq && weixin){
            mShareAction.setDisplayList( SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
        }else if (qq){
            mShareAction.setDisplayList( SHARE_MEDIA.QQ);
        }else if (weixin){
            mShareAction.setDisplayList( SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
        }
    }

    public void openShare(String picUrl) {
        ShareBoardConfig config = new ShareBoardConfig();
        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);
        mShareAction.open(config);
        if (!Utils.isEmpty(picUrl)){
            mShareAction.withMedia(new UMImage(activity, picUrl));
        }
    }

    public void setShareContent( String content, String picUrl) {
        if (content != null){
            if(content.length() > 30)
                content = content.substring(0, 30);
            mShareAction.withText(content);
        }
        String s = ACache.get(activity).getAsString(Values.QRCODEURL);

        if (s != null){
            mShareAction.withMedia(new UMImage(activity, s));
        }


        if (!Utils.isEmpty(picUrl))
            mShareAction.withMedia(new UMImage(activity, picUrl));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(activity).onActivityResult(requestCode, resultCode, data);
    }
}
