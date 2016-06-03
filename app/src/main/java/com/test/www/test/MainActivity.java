package com.test.www.test;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        webView = (WebView) findViewById(R.id.web_view);
        // getSettings()方法可以去设置一些浏览器的属性
        webView.getSettings().setJavaScriptEnabled(true);//让 WebView 支持 JavaScript 脚本
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //这就表明当需要从一个网页跳转到另一个网页时，我们希望目标网页仍然在当前 WebView 中显示，而不是打开系统浏览器。
                view.loadUrl(url); // 根据传入的参数再去加载新的网页
                return true; // 表示当前WebView可以处理打开新网页的请求，不用借助系统浏览器
            }
        });
        webView.loadUrl("http://www.baidu.com");
    }

}
