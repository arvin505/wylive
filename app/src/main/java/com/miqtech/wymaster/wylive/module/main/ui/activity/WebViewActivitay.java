package com.miqtech.wymaster.wylive.module.main.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;


import butterknife.BindView;

/**
 * Created by admin on 2016/9/13.
 */
@LayoutId(R.layout.activity_webview_layout)
public class WebViewActivitay extends BaseAppCompatActivity {
    @BindView(R.id.subWebview)
    WebView webView;
    private String url;
    @Override
    protected void initViews(Bundle savedInstanceState) {
        url = getIntent().getStringExtra("url");
        loadWebView(url);
    }
    private void loadWebView(String url){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webView.loadUrl(url);
        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                // TODO Auto-generated method stub
                super.onReceivedTitle(view, title);
               setTitle(title);
            }
        });
        webView.loadUrl(url); // 加载指定网址
    }
}
