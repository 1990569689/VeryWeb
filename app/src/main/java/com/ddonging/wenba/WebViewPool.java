package com.ddonging.wenba;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.view.ViewGroup;

import com.ddonging.wenba.run.WebViewGm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebViewPool {

    private List<WebViewGm> mIdleWebViewList;
    private List<WebViewGm> mUsingWebViewList;

    private static class Holder {
        private static WebViewPool sInstance = new WebViewPool();
    }

    public static WebViewPool getInstance() {
        return Holder.sInstance;
    }

    private WebViewPool() {
    }

    /**
     * 在 APP 启动时调用, 直接新建一个备用的WebView
     */
    public void init(Context context) {
        mIdleWebViewList = new CopyOnWriteArrayList<>();
        mUsingWebViewList = new ArrayList<>();
        MutableContextWrapper contextWrapper = new MutableContextWrapper(context);
        WebViewGm WebViewGm = new WebViewGm(contextWrapper);
        mIdleWebViewList.add(WebViewGm);
    }

    public WebViewGm acquireWebView(Context context) {
        if (mIdleWebViewList != null && mIdleWebViewList.size() > 0) {
            WebViewGm webView = mIdleWebViewList.remove(0);
            MutableContextWrapper contextWrapper = (MutableContextWrapper) webView.getContext();
            contextWrapper.setBaseContext(context);
            mUsingWebViewList.add(webView);
            return webView;
        } else {
            MutableContextWrapper contextWrapper = new MutableContextWrapper(context);
            WebViewGm webView = new WebViewGm(contextWrapper);
            mUsingWebViewList.add(webView);
            return webView;
        }
    }

    public void recycleWebView(WebViewGm webView,Context context) {
        if (webView == null) {
            return;
        }
        ViewGroup viewParent = (ViewGroup) webView.getParent();
        if (viewParent != null) {
            viewParent.removeView(webView);
        }
        webView.loadUrl("about:blank");
        if (mUsingWebViewList != null && mUsingWebViewList.contains(webView)) {
            mUsingWebViewList.remove(webView);
            MutableContextWrapper contextWrapper = (MutableContextWrapper) webView.getContext();
            contextWrapper.setBaseContext(context);
            webView.setWebViewClient(null);
            webView.setWebChromeClient(null);
            mIdleWebViewList.add(webView);
        } else {
            webView.clearHistory();
            webView.destroy();
        }
    }
}
