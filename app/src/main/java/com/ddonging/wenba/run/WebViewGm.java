/*
 *    Copyright 2012 Werner Bayer
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ddonging.wenba.run;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ddonging.wenba.IndexActivity;
import com.ddonging.wenba.store.ScriptStore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A user script enabled WebView.
 * 
 * Initializes the WebView with a WebViewClientGm. If this object is inflated by
 * XML run setScriptStore to enable script support.
 */
public class WebViewGm extends WebView {


	private static final String JSBRIDGENAME = "WebViewGM";

	private ScriptStore scriptStore;

	private WebViewClientGm webViewClient;

	List<String> mStayMenuList;
	private List<String> mCustomMenuList; //自定义添加的选项
	ActionMode mActionMode;

	public int markTop = 0;
	public int markLeft = 0;
	public boolean isFirst = true;
	private OnTextSelectedListener textSelectedListener;
	List<String> mActionList = new ArrayList<>();

	static ActionSelectListener mActionSelectListener;




	/**
	 * Constructs a new WebViewGm initializing it with a ScriptStore.
	 * 
	 * @param context
	 *            the application's context
	 * @param scriptStore
	 *            the script database to use
	 */
	public WebViewGm(Context context, ScriptStore scriptStore) {
		super(context);
		init();
		setScriptStore(scriptStore);
	}

	/**
	 * Constructs a new WebViewGm with a Context object.
	 * 
	 * @param context
	 *            the application's context
	 */
	public WebViewGm(Context context) {
		super(context);
		init();
	}


	/**
	 * Constructs a new WebViewGm with layout parameters.
	 * 
	 * @param context
	 *            the application's context
	 * @param attrs
	 *            layout parameters
	 */
	public WebViewGm(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * Constructs a new WebViewGm with layout parameters and a default style.
	 * 
	 * @param context
	 *            the application's context
	 * @param attrs
	 *            layout parameters
	 * @param defStyle
	 *            default style resource ID
	 */
	public WebViewGm(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * Called by the constructors to set up the WebView to enable user scripts.
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		WebSettings settings = getSettings();
		settings.setJavaScriptEnabled(true);
		webViewClient = new WebViewClientGm(scriptStore, JSBRIDGENAME, generateSecret());
		setWebViewClient(webViewClient);

	}




	/**
	 * @return the scriptStore
	 */
	public ScriptStore getScriptStore() {
		return scriptStore;
	}

	/**
	 * @param scriptStore
	 *            the scriptStore to set
	 */
    @SuppressLint("AddJavascriptInterface")
    public void setScriptStore(ScriptStore scriptStore) {
		this.scriptStore = scriptStore;
		addJavascriptInterface(new WebViewGmApi(this, scriptStore, webViewClient.getSecret()), JSBRIDGENAME);
		webViewClient.setScriptStore(scriptStore);
	}

	/**
	 * @return the webViewClient
	 */
	public WebViewClientGm getWebViewClient() {
		return webViewClient;
	}

	/**
	 * @param webViewClient
	 *    the WebViewClientGm to set as WebViewClient
	 */
	public void setWebViewClient(WebViewClientGm webViewClient) {
		this.webViewClient = webViewClient;
		super.setWebViewClient(webViewClient);
	}

	/**
	 * @return a random string to use in GM API calls
	 */
	private static String generateSecret() {
		return UUID.randomUUID().toString();
	}



	@Override
	public ActionMode startActionMode(ActionMode.Callback callback) {
		ActionMode actionMode = super.startActionMode(callback);
		actionMode.getMenu().clear();
		return actionMode(actionMode);
	}

	@Override
	public ActionMode startActionMode(ActionMode.Callback callback, int type) {
		ActionMode actionMode = super.startActionMode(callback, type);
		actionMode.getMenu().clear();
		return actionMode(actionMode);
	}

	private void releaseAction() {
		if (mActionMode != null) {
			mActionMode.finish();
			mActionMode = null;
		}
	}





	private ActionMode actionMode(ActionMode actionMode) {
		if (actionMode != null) {
			final Menu menu = actionMode.getMenu();
			mActionMode = actionMode;
			menu.clear();
			for (int i = 0; i < mActionList.size(); i++) {
				menu.add(mActionList.get(i));
			}
			for (int i = 0; i < menu.size(); i++) {
				MenuItem menuItem = menu.getItem(i);
				menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						getSelectedData((String) item.getTitle());
						releaseAction();
						return true;
					}
				});
			}
		}
		mActionMode = actionMode;
		return actionMode;
	}
	/**
	 * 点击的时候，获取网页中选择的文本，回掉到原生中的js接口
	 * @param title 传入点击的item文本，一起通过js返回给原生接口
	 */
	private void getSelectedData(String title) {

		String js = "(function getSelectedText() {" +
				"var txt;" +
				"var title = \"" + title + "\";" +
				"if (window.getSelection) {" +
				"txt = window.getSelection().toString();" +
				"} else if (window.document.getSelection) {" +
				"txt = window.document.getSelection().toString();" +
				"} else if (window.document.selection) {" +
				"txt = window.document.selection.createRange().text;" +
				"}" +
				"JSInterface.callback(txt,title);" +
				"})()";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			evaluateJavascript("javascript:" + js, null);
		} else {
			loadUrl("javascript:" + js);
		}
	}

	public void linkJSInterface() {
		addJavascriptInterface(new ActionSelectInterface(this), "JSInterface");
	}

	/**
	 * 设置弹出action列表
	 * @param actionList
	 */
	public void setActionList(List<String> actionList) {
		mActionList = actionList;
	}

	/**
	 * 设置点击回掉
	 * @param actionSelectListener
	 */
	public void setActionSelectListener(ActionSelectListener actionSelectListener) {
		mActionSelectListener = actionSelectListener;
	}

	/**
	 * 隐藏消失Action
	 */
	public void dismissAction() {
		releaseAction();
	}

	public void setTextSelectedListener(OnTextSelectedListener textSelectedListener) {
		this.textSelectedListener = textSelectedListener;
	}

	public interface OnTextSelectedListener {
		void OnTextSelected(int top, int left);
	}
	/**
	 * js选中的回掉接口
	 */
	private class ActionSelectInterface {

		WebViewGm mContext;

		ActionSelectInterface(WebViewGm c) {
			mContext = c;
		}

		@JavascriptInterface
		public void callback(final String value, final String title) {
			if(mActionSelectListener != null) {
				mActionSelectListener.onClick(title, value);
			}
		}
	}

    public void resetAllState()
	{
		IndexActivity.touch=false;
	}
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				IndexActivity.touch=true;
				break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
	IndexActivity.downX = (int) event.getRawX();
	IndexActivity.downY = (int) event.getRawY();
		return super.onInterceptTouchEvent(event);
	}


}

