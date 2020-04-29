package com.hollysmart.park;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

public class WebviewActivity extends StyleAnimActivity {


    @Override
    public int layoutResID() {
        return R.layout.activity_webview;
    }

    private WebView webView;
    private TextView tv_title;

    private String Url;

    @Override
    public void findView() {
        webView = findViewById(R.id.webview);
        tv_title = findViewById(R.id.tv_title);
        findViewById(R.id.iv_fanhui).setOnClickListener(this);

        String type = getIntent().getStringExtra("type");

        if (!Utils.isEmpty(type)) {

            if (type.equals("fuWu")) { //服务协议
                Url = Values.SERVICE_URL_XIEYI + "xieyi.html?nohead=1";
                tv_title.setText("服务协议");
            }
            if (type.equals("yinSi")) { //隐私政策
                Url = Values.SERVICE_URL_XIEYI + "ysxy.html?nohead=1";
                tv_title.setText("隐私政策");
            }

        }

    }

    @Override
    public void init() {


        webView.getSettings().setDomStorageEnabled(true);
        //设置编码
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        // 设置与Js交互的权限
        webView.getSettings().setJavaScriptEnabled(true);
//            webView.getSettings().setTextZoom(100);

        webView.clearCache(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.loadUrl(Url);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_fanhui:
                finish();
                break;
        }

    }



}
