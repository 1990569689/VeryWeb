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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ddonging.wenba.MyDatabase;
import com.ddonging.wenba.model.Script;
import com.ddonging.wenba.model.ScriptRequire;
import com.ddonging.wenba.store.ScriptStore;

import java.util.UUID;

/**
 * A user script enabled WebViewClient to be used by WebViewGm.
 */

public class WebViewClientGm extends WebViewClient {

	public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

		return null;

	}



	private static final String TAG = WebViewClientGm.class.getName();

	private static final String JSCONTAINERSTART = "(function() {\n";

	private static final String JSCONTAINEREND = "\n})()";

	private static final String JSUNSAFEWINDOW = "unsafeWindow = (function() { var el = document.createElement('p'); el.setAttribute('onclick', 'return window;'); return el.onclick(); }()); window.wrappedJSObject = unsafeWindow;\n";

	private static final String JSMISSINGFUNCTION = "function() { GM_log(\"Called function not yet implemented\"); };\n";

	private static final String JSMISSINGFUNCTIONS = "var GM_info = " + JSMISSINGFUNCTION + "var GM_registerMenuCommand = " + JSMISSINGFUNCTION;

	private ScriptStore scriptStore;

	private String jsBridgeName;

	private String secret;

	private MyDatabase db;

	private boolean is;

	/**
	 * Constructs a new WebViewClientGm with a ScriptStore.
	 * 
	 * @param scriptStore
	 *            the script database to query for scripts to run when a page
	 *            starts/finishes loading
	 * @param jsBridgeName
	 *            the variable name to access the webview GM functions from
	 *            javascript code
	 * @param secret
	 *            a random string that is added to calls of the GM API
	 */
	public WebViewClientGm(ScriptStore scriptStore, String jsBridgeName, String secret) {
		this.scriptStore = scriptStore;
		this.jsBridgeName = jsBridgeName;
		this.secret = secret;
	}

	/**
	 * Runs user scripts enabled for a given URL.
	 * 
	 * Unless a script specifies unwrap it is executed inside an anonymous
	 * function to hide it from access from the loaded page. Calls to the global
	 * JavaScript bridge methods require a secret that is set inside of each
	 * user script's anonymous function.
	 * 
	 * @param view
	 *            the view to load scripts in
	 * @param url
	 *            the current address
	 * @param pageFinished
	 *            true if scripts with runAt property set to document-end or
	 *            null should be run, false if set to document-start
	 * @param jsBeforeScript
	 *            JavaScript code to add between the GM API and the start of the
	 *            user script code (may be null)
	 * @param jsAfterScript
	 *            JavaScript code to add after the end of the user script code
	 *            (may be null)
	 */
	@SuppressLint("Range")
	protected void runMatchingScripts(WebView view, String url, boolean pageFinished, String jsBeforeScript, String jsAfterScript) {
		if (scriptStore == null) {
			Log.w(TAG, "Property scriptStore is null - not running any scripts");
			return;
		}
		Script[] matchingScripts = scriptStore.get(url);
		if (matchingScripts == null) {
			return;
		}
		if (jsBeforeScript == null) {
			jsBeforeScript = "";
		}
		if (jsAfterScript == null) {
			jsAfterScript = "";
		}
		SQLiteOpenHelper help= MyDatabase.getInstance(view.getContext());
		SQLiteDatabase db=help.getWritableDatabase();
		for(int i=0; i<matchingScripts.length; i++)
		{
			String sql = "select * from UserScript where name='" + matchingScripts[i].getName() + "'";
			if(db.isOpen()) {
				Cursor cursor = db.rawQuery(sql, null);
				while (cursor.moveToNext())
				{
					is= Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("user")));
             if(is) {
				 Script	script=matchingScripts[i];
				 if ((!pageFinished && Script.RUNATSTART.equals(script.getRunAt())) || (pageFinished && (script.getRunAt() == null || Script.RUNATEND.equals(script.getRunAt())))) {
					 Log.i(TAG, "Running script \"" + script + "\" on " + url);
					 String defaultSignature = "\""
							 + script.getName().replace("\"", "\\\"") + "\", \""
							 + script.getNamespace().replace("\"", "\\\"")
							 + "\", \"" + secret + "\"";
					 String callbackPrefix = ("GM_" + script.getName() + script.getNamespace() + UUID.randomUUID().toString()).replaceAll("[^0-9a-zA-Z_]", "");
					 String jsApi = JSUNSAFEWINDOW;
					 jsApi += "var GM_listValues = function() { return "
							 + jsBridgeName + ".listValues(" + defaultSignature
							 + ").split(\",\"); };\n";
					 jsApi += "var GM_getValue = function(name, defaultValue) { return "
							 + jsBridgeName
							 + ".getValue("
							 + defaultSignature
							 + ", name, defaultValue); };\n";
					 jsApi += "var GM_setValue = function(name, value) { "
							 + jsBridgeName + ".setValue(" + defaultSignature
							 + ", name, value); };\n";
					 jsApi += "var GM_deleteValue = function(name) { "
							 + jsBridgeName + ".deleteValue(" + defaultSignature
							 + ", name); };\n";
					 jsApi += "var GM_addStyle = function(css) { "
							 + "var style = document.createElement(\"style\"); "
							 + "style.type = \"text/css\"; style.innerHTML = css; "
							 + "document.getElementsByTagName('head')[0].appendChild(style); };\n";
					 jsApi += "var GM_log = function(message) { " + jsBridgeName
							 + ".log(" + defaultSignature + ", message); };\n";
					 jsApi += "var GM_getResourceURL = function(resourceName) { return "
							 + jsBridgeName
							 + ".getResourceURL("
							 + defaultSignature
							 + ", resourceName); };\n";
					 jsApi += "var GM_getResourceText = function(resourceName) { return "
							 + jsBridgeName
							 + ".getResourceText("
							 + defaultSignature
							 + ", resourceName); };\n";
					 jsApi+="var GM_openInTab = function(url, options){ window.androidObject.call_api(url);};";
					 jsApi+="var GM_setClipboard = function GM_setClipboard(b) {\n" +
							 "    var a = b.content,\n" +
							 "        d = b.info,\n" +
							 "        e = typeof d,\n" +
							 "        l, f;\n" +
							 "    \"object\" === e ? (d.type && (l = d.type), d.mimetype && (f = d.mimetype)) : \"string\" === e && (l = d);\n" +
							 "    var g = function(b) {\n" +
							 "        document.removeEventListener(\"copy\", g, !0);\n" +
							 "        b.stopImmediatePropagation();\n" +
							 "        b.preventDefault();\n" +
							 "        b.clipboardData.setData(f || (\"html\" === l ? \"text/html\": \"text/plain\"), a)\n" +
							 "    };\n" +
							 "    document.addEventListener(\"copy\", g, !0);\n" +
							 "    document.execCommand(\"copy\");\n" +
							 "}\n" +
							 "\n";
					 jsApi += "var GM_xmlhttpRequest = function(details) { \n"
							 + "if (details.onabort) { unsafeWindow."
							 + callbackPrefix
							 + "GM_onAbortCallback = details.onabort;\n"
							 + "details.onabort = '"
							 + callbackPrefix
							 + "GM_onAbortCallback'; }\n"
							 + "if (details.onerror) { unsafeWindow."
							 + callbackPrefix
							 + "GM_onErrorCallback = details.onerror;\n"
							 + "details.onerror = '"
							 + callbackPrefix
							 + "GM_onErrorCallback'; }\n"
							 + "if (details.onload) { unsafeWindow."
							 + callbackPrefix
							 + "GM_onLoadCallback = details.onload;\n"
							 + "details.onload = '"
							 + callbackPrefix
							 + "GM_onLoadCallback'; }\n"
							 + "if (details.onprogress) { unsafeWindow."
							 + callbackPrefix
							 + "GM_onProgressCallback = details.onprogress;\n"
							 + "details.onprogress = '"
							 + callbackPrefix
							 + "GM_onProgressCallback'; }\n"
							 + "if (details.onreadystatechange) { unsafeWindow."
							 + callbackPrefix
							 + "GM_onReadyStateChange = details.onreadystatechange;\n"
							 + "details.onreadystatechange = '"
							 + callbackPrefix
							 + "GM_onReadyStateChange'; }\n"
							 + "if (details.ontimeout) { unsafeWindow."
							 + callbackPrefix
							 + "GM_onTimeoutCallback = details.ontimeout;\n"
							 + "details.ontimeout = '"
							 + callbackPrefix
							 + "GM_onTimeoutCallback'; }\n"
							 + "if (details.upload) {\n"
							 + "if (details.upload.onabort) { unsafeWindow."
							 + callbackPrefix
							 + "GM_uploadOnAbortCallback = details.upload.onabort;\n"
							 + "details.upload.onabort = '"
							 + callbackPrefix
							 + "GM_uploadOnAbortCallback'; }\n"
							 + "if (details.upload.onerror) { unsafeWindow."
							 + callbackPrefix
							 + "GM_uploadOnErrorCallback = details.upload.onerror;\n"
							 + "details.upload.onerror = '"
							 + callbackPrefix
							 + "GM_uploadOnErrorCallback'; }\n"
							 + "if (details.upload.onload) { unsafeWindow."
							 + callbackPrefix
							 + "GM_uploadOnLoadCallback = details.upload.onload;\n"
							 + "details.upload.onload = '"
							 + callbackPrefix
							 + "GM_uploadOnLoadCallback'; }\n"
							 + "if (details.upload.onprogress) { unsafeWindow."
							 + callbackPrefix
							 + "GM_uploadOnProgressCallback = details.upload.onprogress;\n"
							 + "details.upload.onprogress = '"
							 + callbackPrefix
							 + "GM_uploadOnProgressCallback'; }\n"
							 + "}\n"
							 + "return JSON.parse("
							 + jsBridgeName
							 + ".xmlHttpRequest("
							 + defaultSignature
							 + ", JSON.stringify(details))); };\n";
					 // TODO implement missing functions
					 jsApi += JSMISSINGFUNCTIONS;

					 // Get @require'd scripts to inject for this script.
					 String jsAllRequires = "";
					 ScriptRequire[] requires = script.getRequires();
					 if (requires != null) {
						 for (ScriptRequire currentRequire : requires) {
							 jsAllRequires += (currentRequire.getContent() + "\n");
						 }
					 }

					 String jsCode = jsApi + jsAllRequires + jsBeforeScript + script.getContent() + jsAfterScript;
					 if (!script.isUnwrap()) {
						 jsCode = JSCONTAINERSTART + jsCode + JSCONTAINEREND;
					 }
					 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						 view.evaluateJavascript(jsCode, null);
					 } else {
						 view.loadUrl("javascript:" + jsCode);
					 }
				 }
			 }


				}
			}
		}
		db.close();
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		//runMatchingScripts(view, url, true, null, null);
	}


	@Override
	public void onPageFinished(WebView view, String url) {
		runMatchingScripts(view, url, true, null, null);

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
	public void setScriptStore(ScriptStore scriptStore) {
		this.scriptStore = scriptStore;
	}

	/**
	 * @return the jsBridgeName
	 */
	public String getJsBridgeName() {
		return jsBridgeName;
	}

	/**
	 * @param jsBridgeName
	 *            the jsBridgeName to set
	 */
	public void setJsBridgeName(String jsBridgeName) {
		this.jsBridgeName = jsBridgeName;
	}

	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @param secret
	 *            the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

}
