package com.ddonging.wenba;

import static android.content.Context.MODE_PRIVATE;
import static com.ddonging.wenba.download.DownloadState.STATE_FAILED;
import static com.ddonging.wenba.download.DownloadState.STATE_FINISHED;
import static com.ddonging.wenba.download.DownloadState.STATE_PAUSED;
import static com.ddonging.wenba.download.DownloadState.STATE_PREPARED;
import static com.ddonging.wenba.download.DownloadState.STATE_RUNNING;
import static com.ddonging.wenba.download.DownloadState.STATE_WAITING;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ddonging.wenba.download.DefaultNotifier;
import com.ddonging.wenba.download.DownloadInfo;
import com.ddonging.wenba.download.DownloadJobListener;
import com.ddonging.wenba.download.DownloadManager;
import com.ddonging.wenba.download.DownloadProvider;
import com.ddonging.wenba.download.DownloadTask;
import com.ddonging.wenba.model.Script;
import com.ddonging.wenba.run.ActionSelectListener;
import com.ddonging.wenba.run.WebViewClientGm;
import com.ddonging.wenba.run.WebViewGm;
import com.ddonging.wenba.store.ScriptStore;
import com.ddonging.wenba.store.ScriptStoreSQLite;
import com.ddonging.wenba.util.DownloadHelper;
import com.ddonging.wenba.util.FileManager;
import com.ddonging.wenba.util.FileUtil;
import com.ddonging.wenba.util.HttpUtil;
import com.ddonging.wenba.util.ScreenUtils;
import com.ddonging.wenba.util.SizeUtil;
import com.ddonging.wenba.util.ToastUtil;
import com.ddonging.wenba.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.Result;
import com.zhangke.qrcodeview.QRCodeView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.TimeZone;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class IndexActivity extends FragmentActivity {

    private static final String APP_CACAHE_DIRNAME = "/WebCache";
    private static final List<DownloadTask> tasks = new ArrayList<>();
    public static NoSwipeViewPager viewpager;
    public static NoSwipeViewPager pager;
    public static NoSwipeViewPager[] itempager = new NoSwipeViewPager[200];
    public static int count = -1, addcount = -1;
    public static int downX, downY,moreX,moreY;
    public static String type, editType, Cookie;
    public static ArrayList<HashMap<String, String>> resources = new ArrayList<>();
    public static ArrayList<Integer> countlist = new ArrayList<Integer>();
    public static boolean touch;
    static boolean isExit;
    static SharedPreferences sp;
    static String bookmarkpath;
    static List<WebViewGm>[] itemlistView = new ArrayList[200];
    //List<List<View>> itemlistViewAll=new ArrayList<List<View>>();
    static List<String>[] WebPageList = new ArrayList[200];
    static List<String>[] WebPageTitle = new ArrayList[200];
    static List<Bitmap>[] WebPageIcon = new ArrayList[200];
    static int xDown, yDown;
    private final Stack<String> mUrls = new Stack<>();
    private final ArrayList array = new ArrayList();
    private final List<String> FilePath = new ArrayList<>();
    public FrameLayout frameLayout, frame, webviewgm, framewebview, script, edit_script; //声明View   容器
    public List<String> urlall;
    public ViewPagerAdapter PagerAdapter;
    public WebAdpter[] viewPagerAdapter = new WebAdpter[200];
    RelativeLayout main, bottomView;
    ConstraintLayout index;
    WebViewGm[] mWebView = new WebViewGm[200], WebList = new WebViewGm[200], SettingView = new WebViewGm[200];
    FragmentManager manager;
    WebView web;
    SeekBar size_seekbar;
    ImageView goBack, goForward, Home, More, find, add, table_add, add_file, more_add, api, pscode, new_script, adblock_add, search;
    ImageView back_setting, back_script, back_more, back_editor, back_download, back_downloaded, back_bookmark, back_code, back_about, back_websetting, back_sizesetting;
    LinearLayout about, about_updata, about_git, about_addtg, about_qun, open, about_agree, about_donation, about_share, about_back, top;
    LinearLayout view_image, in_to_view, save_web, toast_view, SizeSettings, video_view, person, night, source, javascript, set, full, add_bookmark, bookmark, viewlist, history, downloads, downloaded, downloadsList, more, adblock, WebConfig, resource, QR, useragent, webSettings, setting_img, setting_javascript, setting_cookies, setting_apk, setting_position, setting_mic, setting_camera;
    static AutoCompleteTextView webtitle;
    WebBackForwardList weblist;
    static String WebTitle;
    String apiTitle;
    String url;
    String starturl;
    String lasturl;
    ScriptStore scriptStore;
    WebProgress ps;
    TextView edit_name, in_to, in_to_cancel, downloads_ok, goto_desktop, save_pdf, size_progress, toast_sure, toast_cancel, toast_content, title, settitle, text_bookmark, text_collect, text_history, script_save, go_back, config_log, config_tool, config_cookie, webdownload, downloadweb, setting_img_text, setting_javascript_text, setting_cookies_text, setting_apk_text, setting_position_text, setting_mic_text, setting_camera_text;
    Script[] scripts;
    ArrayList<HashMap<String, Object>> arrayList;
    RecyclerView home_list, script_list, more_list, mRecyclerView = null;
    List<View> ListViews = new ArrayList<View>();
    LinearLayout table;
    TextView table_text, more_title, more_text;
    int listview = 0, final_count = 0;
    int progress;
    Bitmap icon;
    List<Bitmap> List = new ArrayList<>();
    boolean isOn;
    ListView settinglist3, adblock_list;
    MyListView settinglist, settinglist2,settinglist4;
    boolean if_exsit;
    EditText script_edit;
    static MyRecyclerView downloaded_recyclerview;
    ScriptAdpter ScriptAdpter;
    MoreAdpter moreAdpter;
    String downloadScriptUrl;
    boolean isNewScript, isNight = false, isTool = false;
    Map<Integer, List<WebViewGm>> itemlistViewAll = new HashMap<Integer, List<WebViewGm>>();
    Map<Integer, List<String>> WebPageAll = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> WebPageTitleAll = new HashMap<Integer, List<String>>();
    Map<Integer, List<Bitmap>> WebPageIconAll = new HashMap<Integer, List<Bitmap>>();
    ArrayList<Integer> listviewAll = new ArrayList<Integer>();
    static Bitmap bitmap;
    boolean isEditMode = false;
    DownloadedAdapter Downadapter;
    FileManager fileManager;
    static DownloadedAdapter DownloadedAdapter;
    File fromFile;
    String fromFilePath, UserAgent, Engines, Homepage, Webapk;
    Boolean Webcamera, Webmic, Webjavascript, Webimg, Webposition, isWindow = false, isEnd = true, isStart = true, isQR;
    float fristversion, lastversion;
    private MyPagerAdapter myAdapter;
    private ArrayList<String> list;    //RecyclerView要显示的 列表数据，在此为一组字符串。
    private MyAdapter adapter;
    private GestureDetector gestureDetector;
    private int get_id; //暂存从数据库得到的id
    private QRCodeView qrCodeView;
    private ValueCallback<Uri[]> mFilePathCallback;
    private static DownloadAdapter DownloadAdapter;
    private List<DownloadInfo> downloadss;
    private final DownloadJobListener jobListener = new DownloadJobListener() {
        @Override
        public void onCreated(DownloadInfo info) {
            //nothing to do
        }

        @Override
        public void onStarted(DownloadInfo info) {
            //nothing to do
        }

        @Override
        public void onCompleted(boolean finished, DownloadInfo info) {
            if (finished) {
                downloadss.add(0, info);

            }
        }
    };
    private DownloadManager managerr;

    /*
     @Override
    public Resources getResources() {
         Resources resources=super.getResources();
         Configuration configuration=new Configuration();
         configuration.setToDefaults();
         resources. updateConfiguration(configuration,resources.getDisplayMetrics());
        return resources;
    }

     */

    public static void setWindowStatusBarColor(Activity activity, int var1) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Window var3 = activity.getWindow();
                var3.addFlags(Integer.MIN_VALUE);
                var3.setStatusBarColor(var1);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    //状态栏变色
    public static Bitmap getBitmapFromView(View var1) {
        Bitmap var2 = Bitmap.createBitmap(var1.getWidth(), var1.getHeight(), Bitmap.Config.RGB_565);
        Canvas var3 = new Canvas(var2);
        var1.layout(var1.getLeft(), var1.getTop(), var1.getRight(), var1.getBottom());
        Drawable var4 = var1.getBackground();
        if (var4 != null) {
            var4.draw(var3);
        } else {
            var3.drawColor(-1);
        }
        var1.draw(var3);
        return var2;
    }

    private static int getStatusBarHeight(Activity activity) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return activity.getResources().getDimensionPixelSize(resourceId);
    }

    public static int getNavBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 关闭软键盘
     *
     * @param view
     */

    public static void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }



    public void download(Context context, String url, String filename, String mimeType) {
        SharedPreferences sharedPreferencess = getSharedPreferences("data", MODE_PRIVATE);
        String home = sharedPreferencess.getString("DownloadBy", "内置下载器");
        if (Objects.equals(home, "内置下载器")) {
            SharedPreferences sharedPreferences = getSharedPreferences("Download", MODE_PRIVATE);
            int i = sharedPreferences.getInt("Download", 0);
            DownloadManager.getInstance().initialize(context, 10);
            DownloadManager controller = DownloadManager.getInstance();
            tasks.add(controller.newTask(i, url, "("+i+")无尽："+filename).extras("http://").create());
            DownloadAdapter.notifyDataSetChanged();
            DefaultNotifier notifier=new DefaultNotifier(context);
            notifier.notify(controller.getAllInfo());
            for (DownloadTask task : tasks) {
                task.start();
            }
            ToastUtil.Toast(IndexActivity.this, "开始下载");
            i++;
            boolean editor = getSharedPreferences("Download", MODE_PRIVATE).edit().putInt("Download", i).commit();
        } else if (Objects.equals(home, "ADM")) {
            Util.downloadByADM(IndexActivity.this, url, mimeType);
        } else if(Objects.equals(home, "手机内置下载器"))
        {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (DownloadTask task : tasks) {
            task.resumeListener();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (DownloadTask task : tasks) {

            task.pauseListener();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //System.exit(0);
        for (DownloadTask task : tasks) {
            task.clear();
        }
    }


    public boolean Download(final String url, String contentDisposition, String mimeType) {
        Download_Dialog(url, contentDisposition, mimeType);
        return false;
    }

    public boolean checkDownload(final String url) {
        if (url.endsWith(".user.js")) {
            new Thread() {
                public void run() {
                    installScript(url);
                }
            }.start();
            return true;
        }
        return false;
    }

    /*
     *安装脚本
     *
     */
    protected void installScript(String url) {
        FileUtil file = new FileUtil();
        String scriptStr = DownloadHelper.downloadScript(url);
        Script script = Script.parse(scriptStr, url);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil t = new ToastUtil();
                ToastUtil.Toast(IndexActivity.this, "正在解析tampermonkey脚本" + script.getName());
            }
        });
        if (script == null) {

        } else if (script.getName().contains("VIP") || script.getName().contains("vip") || script.getName().contains("视频解析")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.Toast(IndexActivity.this, "此脚本在白名单中，无法安装");
                }
            });

        } else {
            scriptStore.add(script);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SQLiteOpenHelper help = MyDatabase.getInstance(IndexActivity.this);
                    SQLiteDatabase db = help.getWritableDatabase();
                    if (db.isOpen()) {
                        String sql = "insert into UserScript(name,user) values(\"" + script.getName() + "\",\"" + true + "\")";
                        db.execSQL(sql);
                        db.close();
                    }
                    ToastUtil t = new ToastUtil();
                    ToastUtil.Toast(IndexActivity.this, "安装成功");
                }
            });
        }
    }

    protected void saveScript(String scriptStr, String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Script script = Script.parse(scriptStr, url);
                if (script == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil t = new ToastUtil();
                            ToastUtil.Toast(IndexActivity.this, "保存失败,缺少关键信息");
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil t = new ToastUtil();
                            ToastUtil.Toast(IndexActivity.this, "正在解析脚本" + script.getName());
                        }
                    });
                    scriptStore.add(script);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteOpenHelper help = MyDatabase.getInstance(IndexActivity.this);
                            SQLiteDatabase db = help.getWritableDatabase();
                            if (db.isOpen()) {
                                String sql = "insert into UserScript(name,user) values(\"" + script.getName() + "\",\"" + true + "\")";
                                db.execSQL(sql);
                                db.close();
                            }
                            ToastUtil.Toast(IndexActivity.this, "保存成功");
                        }
                    });
                }
            }

        }).start();
    }



    private void openFileChooseProcess(boolean isMulti) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("*/*");
        if (isMulti) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(intent, "FileChooser"), 66);
    }

    public void updateStatusColor(WebView view) {
        if (itempager[countlist.get(count)].getCurrentItem() == 0&&(Objects.equals(view.getUrl(), "file:///android_asset/index.html") || Objects.equals(view.getUrl(), "about:blank"))) {
            initView();
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                Bitmap var1 = getBitmapFromView(view);
                if (var1 != null) {
                    int var2 = var1.getPixel(getWindowManager().getDefaultDisplay().getWidth() / 2, 10);
                    int var3 = Color.red(var2);
                    int var4 = Color.green(var2);
                    int var5 = Color.blue(var2);
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (var3 > 200 && var4 > 200 && var5 > 200) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setWindowStatusBarColor(IndexActivity.this, var2);
                                    getWindow().setStatusBarColor(var2);
                                    bottomView.setBackgroundColor(var2);
                                    getWindow().setNavigationBarColor(var2);
                                    main.setBackgroundColor(var2);

                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setWindowStatusBarColor(IndexActivity.this, var2);
                                    getWindow().setStatusBarColor(var2);
                                    bottomView.setBackgroundColor(var2);
                                    main.setBackgroundColor(var2);
                                    getWindow().setNavigationBarColor(var2);
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goBack.setColorFilter(getResources().getColor(R.color.black));
                                goForward.setColorFilter(getResources().getColor(R.color.black));
                                Home.setColorFilter(getResources().getColor(R.color.black));
                                More.setColorFilter(getResources().getColor(R.color.black));
                                add.setColorFilter(getResources().getColor(R.color.black));
                                search.setColorFilter(getResources().getColor(R.color.black));
                                find.setColorFilter(getResources().getColor(R.color.black));
                                api.setColorFilter(getResources().getColor(R.color.black));
                                webtitle.setTextColor(getResources().getColor(R.color.black));
                                table_text.setTextColor(getResources().getColor(R.color.black));
                                pscode.setColorFilter(getResources().getColor(R.color.black));
                            }
                        });
                    }
                    var1.recycle();
                }
            }
        }
    }

    public void initWeb() {
        SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (Homepage.equals("本地主页：主页")) {
            addView(ListViews, "file:///android_asset/index.html", 0, null);
        } else if (Homepage.startsWith("本地主页：")) {

            if (db.isOpen()) {
                Cursor cursorr = db.rawQuery("SELECT content FROM Homepage WHERE title='" + Homepage + "'", null);
                if (cursorr.getCount() > 0) {
                    while (cursorr.moveToNext()) {
                        @SuppressLint("Range")
                        String content = cursorr.getString(cursorr.getColumnIndex("content"));
                        String toresult = new String(Base64.decode(content.getBytes(), Base64.DEFAULT));
                        addView(ListViews, "file:///android_asset/index.html", 1, toresult);
                    }
                }
            }
        } else {
            if (db.isOpen()) {
                Cursor cursorr = db.rawQuery("SELECT content FROM Homepage WHERE title='" + Homepage + "'", null);
                if (cursorr.getCount() > 0) {
                    while (cursorr.moveToNext()) {
                        @SuppressLint("Range")
                        String content = cursorr.getString(cursorr.getColumnIndex("content"));
                        addView(ListViews, content, 2, null);
                    }
                }
            }
        }
    }

    public void destory()
    {
        for (int i = WebPageAll.get(countlist.get(count)).size() - 1; i > itempager[countlist.get(count)].getCurrentItem(); i--) {
            itemlistViewAll.get(countlist.get(count)).get(i).destroy();
            viewPagerAdapter[countlist.get(count)].destroyItem(itempager[countlist.get(count)], i, "");
            WebPageAll.get(countlist.get(count)).remove(i);
            itemlistViewAll.get(countlist.get(count)).remove(i);
            WebPageTitleAll.get(countlist.get(count)).remove(i);
            WebPageIconAll.get(countlist.get(count)).remove(i);
        }
    }
    public void addWebView(String url) {  // 重点在这里，每次都新的URL都会创建一个WebView实例，添加到容器中
        listview++;
        webtitle.setText(url);
        webtitle.clearFocus();
        isOn = false;
        WebList[listview] = new WebViewGm(IndexActivity.this);
        WebList[listview].setScriptStore(scriptStore);
        itemlistView[countlist.get(count)].add(WebList[listview]);
        viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
        itempager[countlist.get(count)].setCurrentItem(listview, false);
        WebPageList[countlist.get(count)].add(url);
        WebPageAll.put(countlist.get(count), WebPageList[countlist.get(count)]);
        WebPageTitle[countlist.get(count)].add(url);
        WebPageTitleAll.put(countlist.get(count), WebPageTitle[countlist.get(count)]);
        WebPageIcon[countlist.get(count)].add(Util.drawableToBitamp(getDrawable(R.drawable.ic_home)));
        WebPageIconAll.put(countlist.get(count), WebPageIcon[countlist.get(count)]);
        itemlistViewAll.get(countlist.get(count)).get(listview).getSettings().setBlockNetworkImage(true);
        itemlistViewAll.get(countlist.get(count)).get(listview).setWebViewClient(new ScriptBrowserWebViewClientGm(scriptStore, itemlistViewAll.get(countlist.get(count)).get(listview).getWebViewClient().getJsBridgeName(), itemlistViewAll.get(countlist.get(count)).get(listview).getWebViewClient().getSecret(), this));
        itemlistViewAll.get(countlist.get(count)).get(listview).setDownloadListener(new ScriptBrowserDownloadListener(this));
        List<String> list = new ArrayList<>();
        list.add("复制");
        list.add("添加到纸条");
        list.add("搜索");
        itemlistViewAll.get(countlist.get(count)).get(listview).setActionList(list);
        itemlistViewAll.get(countlist.get(count)).get(listview).linkJSInterface();
        itemlistViewAll.get(countlist.get(count)).get(listview).setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String title, String selectText) {
                if (title.equals("复制")) {
                    ClipboardManager cmb = (ClipboardManager) IndexActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(selectText.trim());
                    ToastUtil.Toast(IndexActivity.this, "复制成功");

                } else if (title.equals("添加到纸条")) {
                    if (!selectText.equals("")) {
                        SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        if (db.isOpen()) {
                            String sql = "insert into Card(title,content) values(\"" + "[" + webtitle.getText().toString() + "]\n" + selectText + "\",\"" + url + "\")";
                            db.execSQL(sql);
                        }
                        ToastUtil.Toast(IndexActivity.this, "添加成功");
                    } else {
                        ToastUtil.Toast(IndexActivity.this, "添加失败，请重新选择");
                    }

                } else if (title.equals("搜索")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            destory();
                            viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                            SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            if (db.isOpen()) {
                                Cursor cursorr = db.rawQuery("SELECT content FROM Engines WHERE title='" + Engines + "'", null);
                                if (cursorr.getCount() > 0) {
                                    while (cursorr.moveToNext()) {
                                        String Engines = cursorr.getString(cursorr.getColumnIndex("content"));
                                        addWebView(Engines + selectText);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
        itemlistViewAll.get(countlist.get(count)).get(listview).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ItemLongClickedPopWindow itemLongClickedPopWindow = new ItemLongClickedPopWindow(IndexActivity.this, ItemLongClickedPopWindow.IMAGE_VIEW_POPUPWINDOW, SizeUtil.dp2px(IndexActivity.this, 120), SizeUtil.dp2px(IndexActivity.this, 220));
                WebView.HitTestResult result = itemlistViewAll.get(countlist.get(count)).get(listview).getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                switch (type) {
                    case WebView.HitTestResult.EDIT_TEXT_TYPE: // 选中的文字类型
                        break;
                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE: // 　地图类型
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        String herf = result.getExtra();
                        itemLongClickedPopWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, downX, downY);
                        itemLongClickedPopWindow.getView(R.id.item_longclicked_viewImage).setVisibility(View.GONE);
                        itemLongClickedPopWindow.getView(R.id.item_longclicked_saveImage).setVisibility(View.GONE);
                        itemLongClickedPopWindow.getView(R.id.copy_url).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ClipboardManager cmb = (ClipboardManager) IndexActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                cmb.setText(herf.trim());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.Toast(IndexActivity.this, "复制成功");
                                    }
                                });
                                itemLongClickedPopWindow.dismiss();
                            }
                        });
                        itemLongClickedPopWindow.getView(R.id.in_table).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                table_text.setText(String.valueOf(final_count + 1));
                                adapter.notifyItemInserted(list.size());
                                listviewAll.add(listview);
                                listview = 0;
                                isWindow = true;
                                touch=false;
                                addView(ListViews, herf, 2, null);
                                viewpager.setCurrentItem(count, false);
                                itemLongClickedPopWindow.dismiss();
                            }
                        });
                        itemLongClickedPopWindow.getView(R.id.table_in).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                table_text.setText(String.valueOf(final_count + 1));
                                adapter.notifyItemInserted(list.size());
                                listviewAll.add(listview);
                                listview = 0;
                                isWindow = true;
                                touch=false;
                                addView(ListViews, herf, 2, null);
                                itemLongClickedPopWindow.dismiss();
                            }
                        });
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // 带有链接的图片类型
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                        String url = result.getExtra();
                        itemLongClickedPopWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, downX, downY);
                        itemLongClickedPopWindow.getView(R.id.in_table).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                table_text.setText(String.valueOf(final_count + 1));
                                adapter.notifyItemInserted(list.size());
                                listviewAll.add(listview);
                                listview = 0;
                                isWindow = true;
                                touch=false;
                                addView(ListViews, url, 2, null);
                                viewpager.setCurrentItem(count, false);
                                itemLongClickedPopWindow.dismiss();
                            }
                        });
                        itemLongClickedPopWindow.getView(R.id.table_in).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                table_text.setText(String.valueOf(final_count + 1));
                                adapter.notifyItemInserted(list.size());
                                listviewAll.add(listview);
                                listview = 0;
                                isWindow = true;
                                touch=false;
                                addView(ListViews, url, 2, null);
                                itemLongClickedPopWindow.dismiss();
                            }
                        });
                        itemLongClickedPopWindow.getView(R.id.item_longclicked_viewImage).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                view_image.setVisibility(View.VISIBLE);
                                WebView web = findViewById(R.id.image_view);
                                //设置可以支持缩放
                                web.getSettings().setSupportZoom(true);
                                web.getSettings().setBuiltInZoomControls(true);
                                web.getSettings().setDisplayZoomControls(false);
                                web.setInitialScale(120);
                                web.loadUrl(url);
                                itemLongClickedPopWindow.dismiss();
                            }
                        });

                        itemLongClickedPopWindow.getView(R.id.item_longclicked_saveImage).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String result = FileUtil.saveImage(IndexActivity.this, url);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.Toast(IndexActivity.this, result);
                                            }
                                        });

                                    }
                                }).start();
                                itemLongClickedPopWindow.dismiss();
                            }
                        });
                        itemLongClickedPopWindow.getView(R.id.copy_url).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ClipboardManager cmb = (ClipboardManager) IndexActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                cmb.setText(url.trim());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.Toast(IndexActivity.this, "复制成功");
                                    }
                                });
                                itemLongClickedPopWindow.dismiss();
                            }
                        });

                        return true;
                    case WebView.HitTestResult.UNKNOWN_TYPE: //未知
                        break;
                }
                return false;
            }
        });

        itemlistViewAll.get(countlist.get(count)).get(listview).setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Webcamera == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        request.grant(request.getResources());
                    }
                } else {
                    ToastUtil.Toast(IndexActivity.this, "摄像头权限默认关闭状态");
                }

            }

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
                View view = inflater.inflate(R.layout.data_dialog, null);
                // 对话框
                final Dialog dialog = new Dialog(IndexActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                WindowManager windowManager = getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = display.getWidth() - 150; // 设置宽度
                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().setContentView(view);
                TextView data_title = view.findViewById(R.id.data_title);
                data_title.setText("消息");
                TextView data_content = view.findViewById(R.id.data_content);
                data_content.setText("来自:" + url + "的消息\n" + "网站请求获取地理位置");
                TextView data_sure = view.findViewById(R.id.data_sure);
                TextView data_cancel = view.findViewById(R.id.data_cancel);
                data_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (ActivityCompat.checkSelfPermission(IndexActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(IndexActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                            return;
                        }

                        callback.invoke(origin, true, true);
                        dialog.cancel();
                    }
                });
                data_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.invoke(origin, false, true);
                        dialog.cancel();
                    }
                });

            }

            @Override
            public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
                LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
                View view = inflater.inflate(R.layout.data_dialog, null);
                // 对话框
                final Dialog dialog = new Dialog(IndexActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                // 设置宽度为屏幕的宽度
                WindowManager windowManager = getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = display.getWidth() - 150; // 设置宽度
                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().setContentView(view);

                TextView data_title = view.findViewById(R.id.data_title);
                data_title.setText("消息");
                TextView data_content = view.findViewById(R.id.data_content);
                data_content.setText("来自:" + url + "的消息\n" + message);
                TextView data_sure = view.findViewById(R.id.data_sure);
                TextView data_cancel = view.findViewById(R.id.data_cancel);

                data_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        result.confirm();
                        dialog.cancel();
                    }
                });
                data_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                // 只呈现1s
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView webView, String url, String message, JsResult result) {
                LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
                View view = inflater.inflate(R.layout.data_dialog, null);
                // 对话框
                final Dialog dialog = new Dialog(IndexActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                // 设置宽度为屏幕的宽度
                WindowManager windowManager = getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = display.getWidth() - 150; // 设置宽度
                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().setContentView(view);

                TextView data_title = view.findViewById(R.id.data_title);
                TextView data_content = view.findViewById(R.id.data_content);
                data_content.setText("来自:" + url + "的消息\n" + message);
                TextView data_sure = view.findViewById(R.id.data_sure);
                TextView data_cancel = view.findViewById(R.id.data_cancel);

                data_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        result.confirm();
                        dialog.cancel();
                    }
                });
                data_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        result.cancel();
                        dialog.cancel();
                    }
                });

                return true;
            }

            @Override
            public boolean onJsBeforeUnload(WebView webView, String url, String message, JsResult result) {
                LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
                View view = inflater.inflate(R.layout.data_dialog, null);
                // 对话框
                final Dialog dialog = new Dialog(IndexActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                // 设置宽度为屏幕的宽度
                WindowManager windowManager = getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = display.getWidth() - 150; // 设置宽度
                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().setContentView(view);
                TextView data_title = view.findViewById(R.id.data_title);
                data_title.setText("页面即将跳转");
                TextView data_content = view.findViewById(R.id.data_content);
                data_content.setText("来自:" + url + "的消息\n" + message);
                TextView data_sure = view.findViewById(R.id.data_sure);
                TextView data_cancel = view.findViewById(R.id.data_cancel);
                data_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        result.confirm();
                        dialog.cancel();
                    }
                });
                data_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        result.cancel();
                        dialog.cancel();
                    }
                });
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView webView, String url, String message, String defaultValue, JsPromptResult result) {
                final EditText input = new EditText(IndexActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                new AlertDialog.Builder(IndexActivity.this).setTitle("").setMessage(message).setView(input).setPositiveButton("确定", (dialogInterface, i) -> result.confirm(input.getText().toString())).setCancelable(false).show();
                return true;
            }

            /**
             * Return value usage see FILE_CHOOSE_REQUEST in
             */
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;
                openFileChooseProcess(fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE);
                return true;
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                // 在当前 WebView 中打开新窗口
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                resultMsg.sendToTarget();
                Message href = view.getHandler().obtainMessage();
                view.requestFocusNodeHref(href);
                String url = href.getData().getString("url");
                table_text.setText(String.valueOf(final_count + 1));
                adapter.notifyItemInserted(list.size());
                listviewAll.add(listview);
                //initWeb();
                listview = 0;
                touch=false;
                isWindow = true;
                addView(ListViews, url, 2, null);
                viewpager.setCurrentItem(count, false);
                return true;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                frameLayout.addView(view);
                ScreenUtils.setLandscape(IndexActivity.this);
                frameLayout.setVisibility(View.VISIBLE);
                View decorView = getWindow().getDecorView();
                //decorView.setVisibility(View.GONE);
                //decorView.setSystemUiVisibility(View.GONE);
                //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                frameLayout.removeAllViews();
                ScreenUtils.setPortrait(IndexActivity.this);
                frameLayout.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(IndexActivity.this, R.color.white));
                    setDarkStatusIcon(true);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title == null || title.equals("")) {
                } else {
                    if (listview > 0) {
                        for (Map.Entry<Integer, List<WebViewGm>> all : itemlistViewAll.entrySet()) {
                            if (all.getValue().contains(view)) {
                               int i = all.getKey();
                                if (WebPageTitle[i] == null) {
                                } else {
                                    WebPageTitle[i].set(WebPageTitle[i].size() - 1, title);
                                    WebPageTitleAll.put(i, WebPageTitle[i]);
                                }
                            }
                        }

                    }
                    if(!isOn)
                    {
                        webtitle.setText(title);
                    }
                }
                WebTitle = title;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.contains("Error")) {
                        view.loadUrl("about:blank");
                    }
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根
                ps.setColor("#E93D3D", "#EDA2A2");
                ps.setWebProgress(newProgress);//设置进度值
                find.setImageResource(R.drawable.ic_go);
                if (newProgress == 10) {
                    itemlistViewAll.get(countlist.get(count)).get(listview).setBackgroundColor(Color.WHITE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isNight) {
                                getWindow().setNavigationBarColor(getResources().getColor(R.color.night));
                                getWindow().setStatusBarColor(getResources().getColor(R.color.night));
                                bottomView.setBackgroundColor(getResources().getColor(R.color.night));
                                main.setBackgroundColor(getResources().getColor(R.color.night));
                                goBack.setColorFilter(getResources().getColor(R.color.white));
                                goForward.setColorFilter(getResources().getColor(R.color.white));
                                Home.setColorFilter(getResources().getColor(R.color.white));
                                More.setColorFilter(getResources().getColor(R.color.white));
                                add.setColorFilter(getResources().getColor(R.color.white));
                                search.setColorFilter(getResources().getColor(R.color.white));
                                find.setColorFilter(getResources().getColor(R.color.white));
                                api.setColorFilter(getResources().getColor(R.color.white));
                                webtitle.setTextColor(getResources().getColor(R.color.white));
                                table_text.setTextColor(getResources().getColor(R.color.white));
                            } else {
                                getWindow().setNavigationBarColor(Color.WHITE);
                                getWindow().setStatusBarColor(Color.WHITE);
                                mWebView[count].setBackgroundColor(Color.WHITE);
                                bottomView.setBackgroundColor(Color.WHITE);
                                main.setBackgroundColor(Color.WHITE);
                                goBack.setColorFilter(getResources().getColor(R.color.black));
                                goForward.setColorFilter(getResources().getColor(R.color.black));
                                Home.setColorFilter(getResources().getColor(R.color.black));
                                More.setColorFilter(getResources().getColor(R.color.black));
                                add.setColorFilter(getResources().getColor(R.color.black));
                                search.setColorFilter(getResources().getColor(R.color.black));
                                find.setColorFilter(getResources().getColor(R.color.black));
                                api.setColorFilter(getResources().getColor(R.color.black));
                                webtitle.setTextColor(getResources().getColor(R.color.black));
                                table_text.setTextColor(getResources().getColor(R.color.black));
                                pscode.setColorFilter(getResources().getColor(R.color.black));
                            }
                        }
                    });

                }
                if (newProgress == 100 && !isOn) {
                    view.getSettings().setBlockNetworkImage(false);
                    find.setImageResource(R.drawable.ic_refresh);
                    search.setImageResource(R.drawable.ic_suo);
                    initView();
                }
                progress = newProgress;
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                if (listview > 0) {
                    for (Map.Entry<Integer, List<WebViewGm>> all : itemlistViewAll.entrySet()) {
                        if (all.getValue().contains(view)) {
                            int i = all.getKey();
                            try {
                                if (WebPageIcon[i] == null) {
                                } else {
                                    WebPageIcon[i].set(listview, icon);
                                    WebPageIconAll.put(i, WebPageIcon[i]);
                                    bitmap = icon;
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        });
        initWebPageView(itemlistViewAll.get(countlist.get(count)).get(listview), true);
        itemlistViewAll.get(countlist.get(count)).get(listview).loadUrl(url);
    }

    public void addView(List<View> viewList, String url, int is, String data) {
        count = final_count - 1;
        count++;
        addcount++;
        final_count++;
        list.add("主页");
        List.add(Util.drawableToBitamp(getDrawable(R.drawable.ic_home)));
        countlist.add(addcount);
        itempager[countlist.get(count)] = new NoSwipeViewPager(IndexActivity.this);
        itempager[countlist.get(count)].setCanSwipe(false);
        OverScrollDecoratorHelper.setUpOverScroll(itempager[countlist.get(count)]);
        mWebView[count] = new WebViewGm(IndexActivity.this);
        WebPageList[countlist.get(count)] = new ArrayList<>();
        WebPageTitle[countlist.get(count)] = new ArrayList<>();
        WebPageIcon[countlist.get(count)] = new ArrayList<>();
        itemlistView[countlist.get(count)] = new ArrayList<>();

        itemlistView[countlist.get(count)].add(mWebView[count]);
        itemlistViewAll.put(countlist.get(count), itemlistView[countlist.get(count)]);
        viewPagerAdapter[countlist.get(count)] = new WebAdpter(itemlistViewAll.get(countlist.get(count)));
        itempager[countlist.get(count)].setAdapter(viewPagerAdapter[countlist.get(count)]);
        itempager[countlist.get(count)].setOffscreenPageLimit(20);
        WebPageList[countlist.get(count)].add(url);
        WebPageTitle[countlist.get(count)].add(url);

        WebPageAll.put(countlist.get(count), WebPageList[countlist.get(count)]);
        WebPageIcon[countlist.get(count)].add(Util.drawableToBitamp(getDrawable(R.drawable.ic_home)));
        WebPageIconAll.put(countlist.get(count), WebPageIcon[countlist.get(count)]);

        WebPageTitleAll.put(countlist.get(count), WebPageTitle[countlist.get(count)]);

        mWebView[count].setScriptStore(scriptStore);

        mWebView[count].setDownloadListener(new ScriptBrowserDownloadListener(this));
        mWebView[count].setWebViewClient(new ScriptBrowserWebViewClientGm(scriptStore, mWebView[count].getWebViewClient().getJsBridgeName(), mWebView[count].getWebViewClient().getSecret(), this));
        mWebView[count].setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Webcamera == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        request.grant(request.getResources());
                    }
                } else {
                    ToastUtil.Toast(IndexActivity.this, "摄像头权限默认关闭状态");
                }

            }

            /**
             * Return value usage see FILE_CHOOSE_REQUEST in
             */
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;
                openFileChooseProcess(fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE);
                return true;
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                // 在当前 WebView 中打开新窗口
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                resultMsg.sendToTarget();
                Message href = view.getHandler().obtainMessage();
                view.requestFocusNodeHref(href);
                String url = href.getData().getString("url");
                table_text.setText(String.valueOf(final_count + 1));
                adapter.notifyItemInserted(list.size());
                listviewAll.add(listview);
                //initWeb();
                listview = 0;
                touch=false;
                isWindow = true;
                addView(ListViews, url, 2, null);
                viewpager.setCurrentItem(count, false);
                return true;
            }
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title == null || title.equals("")) {
                } else {
                    int i = 0;
                    for (Map.Entry<Integer, List<WebViewGm>> all : itemlistViewAll.entrySet()) {
                        if (all.getValue().contains(view)) {
                            i = all.getKey();
                        }

                    }
                    if (WebPageTitle[i] == null) {
                    } else {
                        WebPageTitle[i].set(WebPageTitle[i].size() - 1, title);
                        WebPageTitleAll.put(i, WebPageTitle[i]);
                    }

                    webtitle.setText(title);
                }
                WebTitle = title;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.contains("Error")) {
                        view.loadUrl("about:blank");
                    }
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根
                if(is==2)
                {
                    ps.setWebProgress(newProgress);
                }
                if (newProgress == 10) {
                    find.setImageResource(R.drawable.ic_go);
                    webtitle.clearFocus();
                    isOn = false;
                }
                if (newProgress == 100 && !isOn) {
                    itemlistViewAll.get(countlist.get(count)).get(listview).getSettings().setBlockNetworkImage(false);
                    find.setImageResource(R.drawable.ic_refresh);
                    search.setImageResource(R.drawable.ic_suo);
                    initView();
                }
                progress = newProgress;
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                int i = 0;
                for (Map.Entry<Integer, List<WebViewGm>> all : itemlistViewAll.entrySet()) {
                    if (all.getValue().contains(view)) {
                        i = all.getKey();
                    }
                }
                try {
                    if (WebPageIcon[i] == null) {
                    } else {
                        WebPageIcon[i].set(listview, icon);
                        WebPageIconAll.put(i, WebPageIcon[i]);
                        bitmap = icon;
                    }
                } catch (Exception e) {
                }
            }
        });
        initWebPageView(mWebView[count], false);
        if (is == 1) {
            mWebView[count].loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
        } else {
            mWebView[count].loadUrl(url);
        }
        viewList.add(itempager[countlist.get(count)]);
        myAdapter.notifyDataSetChanged();
        isStart = true;
    }

    public void initView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String indexBackground = getSharedPreferences("data", MODE_PRIVATE).getString("indexBackground", "空白背景");
                if (itempager[countlist.get(count)].getCurrentItem() == 0) {
                    mWebView[count].setBackgroundColor(0);
                    getWindow().setNavigationBarColor(Color.TRANSPARENT);
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                    bottomView.setBackgroundColor(Color.TRANSPARENT);
                    main.setBackgroundColor(Color.TRANSPARENT);
                    if (isNight) {
                        getWindow().setNavigationBarColor(getResources().getColor(R.color.night));
                        getWindow().setStatusBarColor(getResources().getColor(R.color.night));
                        bottomView.setBackgroundColor(getResources().getColor(R.color.night));
                        main.setBackgroundColor(getResources().getColor(R.color.night));
                        goBack.setColorFilter(getResources().getColor(R.color.white));
                        goForward.setColorFilter(getResources().getColor(R.color.white));
                        Home.setColorFilter(getResources().getColor(R.color.white));
                        More.setColorFilter(getResources().getColor(R.color.white));
                        add.setColorFilter(getResources().getColor(R.color.white));
                        search.setColorFilter(getResources().getColor(R.color.white));
                        find.setColorFilter(getResources().getColor(R.color.white));
                        api.setColorFilter(getResources().getColor(R.color.white));
                        webtitle.setTextColor(getResources().getColor(R.color.white));
                        table_text.setTextColor(getResources().getColor(R.color.white));
                    } else if (!Objects.equals(indexBackground, "空白背景")) {
                        goBack.setColorFilter(getResources().getColor(R.color.white));
                        goForward.setColorFilter(getResources().getColor(R.color.white));
                        Home.setColorFilter(getResources().getColor(R.color.white));
                        More.setColorFilter(getResources().getColor(R.color.white));
                        add.setColorFilter(getResources().getColor(R.color.white));
                        search.setColorFilter(getResources().getColor(R.color.white));
                        find.setColorFilter(getResources().getColor(R.color.white));
                        api.setColorFilter(getResources().getColor(R.color.white));
                        webtitle.setTextColor(getResources().getColor(R.color.white));
                        table_text.setTextColor(getResources().getColor(R.color.white));
                        pscode.setColorFilter(getResources().getColor(R.color.white));
                    } else {
                        mWebView[count].setBackgroundColor(0);
                        getWindow().setNavigationBarColor(Color.TRANSPARENT);
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                        bottomView.setBackgroundColor(Color.TRANSPARENT);
                        main.setBackgroundColor(Color.TRANSPARENT);
                        goBack.setColorFilter(getResources().getColor(R.color.black));
                        goForward.setColorFilter(getResources().getColor(R.color.black));
                        Home.setColorFilter(getResources().getColor(R.color.black));
                        More.setColorFilter(getResources().getColor(R.color.black));
                        add.setColorFilter(getResources().getColor(R.color.black));
                        search.setColorFilter(getResources().getColor(R.color.black));
                        find.setColorFilter(getResources().getColor(R.color.black));
                        api.setColorFilter(getResources().getColor(R.color.black));
                        webtitle.setTextColor(getResources().getColor(R.color.black));
                        table_text.setTextColor(getResources().getColor(R.color.black));
                        pscode.setColorFilter(getResources().getColor(R.color.black));
                    }
                } else if (isNight) {
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.night));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.night));
                    bottomView.setBackgroundColor(getResources().getColor(R.color.night));
                    main.setBackgroundColor(getResources().getColor(R.color.night));
                    goBack.setColorFilter(getResources().getColor(R.color.white));
                    goForward.setColorFilter(getResources().getColor(R.color.white));
                    Home.setColorFilter(getResources().getColor(R.color.white));
                    More.setColorFilter(getResources().getColor(R.color.white));
                    add.setColorFilter(getResources().getColor(R.color.white));
                    search.setColorFilter(getResources().getColor(R.color.white));
                    find.setColorFilter(getResources().getColor(R.color.white));
                    api.setColorFilter(getResources().getColor(R.color.white));
                    webtitle.setTextColor(getResources().getColor(R.color.white));
                    table_text.setTextColor(getResources().getColor(R.color.white));

                } else {
                    getWindow().setNavigationBarColor(Color.WHITE);
                    getWindow().setStatusBarColor(Color.WHITE);
                    mWebView[count].setBackgroundColor(Color.WHITE);
                    bottomView.setBackgroundColor(Color.WHITE);
                    main.setBackgroundColor(Color.WHITE);
                    goBack.setColorFilter(getResources().getColor(R.color.black));
                    goForward.setColorFilter(getResources().getColor(R.color.black));
                    Home.setColorFilter(getResources().getColor(R.color.black));
                    More.setColorFilter(getResources().getColor(R.color.black));
                    add.setColorFilter(getResources().getColor(R.color.black));
                    search.setColorFilter(getResources().getColor(R.color.black));
                    find.setColorFilter(getResources().getColor(R.color.black));
                    api.setColorFilter(getResources().getColor(R.color.black));
                    webtitle.setTextColor(getResources().getColor(R.color.black));
                    table_text.setTextColor(getResources().getColor(R.color.black));
                    pscode.setColorFilter(getResources().getColor(R.color.black));
                }
            }});
    }

    private void initWebPageView(WebViewGm view, Boolean is) {
        //自适应屏幕
        UserAgent = getSharedPreferences("UserAgent", MODE_PRIVATE).getString("UserAgent", "手机");
        SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (is) {
            view.getSettings().setBlockNetworkImage(false);
            view.getSettings().setLoadsImagesAutomatically(Webimg);
            view.getSettings().setUseWideViewPort(true);
            view.getSettings().setLoadWithOverviewMode(true);
            view.getSettings().setBuiltInZoomControls(true);
            view.setVerticalScrollBarEnabled(false);
            view.getSettings().setSupportZoom(true);
            view.getSettings().setDisplayZoomControls(false);
            view.requestFocusFromTouch();
            view.setInitialScale(60);
            view.getSettings().setJavaScriptEnabled(Webjavascript);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            } else {
                view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //适配5.0不允许http和https混合使用情况
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            if (db.isOpen()) {
                Cursor cursorr = db.rawQuery("SELECT content FROM UserAgent WHERE title='" + UserAgent + "'", null);
                if (cursorr.getCount() > 0) {
                    while (cursorr.moveToNext()) {
                        String ua = cursorr.getString(cursorr.getColumnIndex("content"));
                        view.getSettings().setUserAgentString(ua);
                    }
                }
            }
        }
        view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式/
        view.getSettings().setDomStorageEnabled(true);
        view.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        view.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        int WebFontSize = getSharedPreferences("WebFontSize", MODE_PRIVATE).getInt("WebFontSize", 20);
        view.getSettings().setDatabasePath(cacheDirPath);
        view.getSettings().setAppCacheMaxSize(Long.MAX_VALUE);
        view.getSettings().setAllowFileAccess(true);
        view.getSettings().setAppCachePath(cacheDirPath);
        view.getSettings().setAppCacheEnabled(true);
        view.setBackgroundColor(0);
        view.addJavascriptInterface(IndexActivity.this, "androidObject");
        view.getSettings().setDomStorageEnabled(true);
        view.getSettings().setNeedInitialFocus(true);
        view.getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式
        view.getSettings().setDefaultFontSize(WebFontSize);
        view.getSettings().setMinimumFontSize(10);//设置WebView支持的最小字体大小，默认为
        view.getSettings().setGeolocationEnabled(true);
        view.getSettings().setSupportMultipleWindows(true);
        view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        view.getSettings().setAllowUniversalAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.getSettings().setAllowUniversalAccessFromFileURLs(true);
        } else {
            try {
                Class<?> clazz = view.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(view.getSettings(), true);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();}}}
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                boolean writeExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && writeExternalStorage && readExternalStorage) {
                    getImage();
                } else {
                    Toast.makeText(this, "请设置必要权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 10:
                boolean qr = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0) {
                    Toast.makeText(this, "请设置必要权限", Toast.LENGTH_SHORT).show();
                } else {

                }
                break;
        }

    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");  //设置要过滤的文件格式
        startActivityForResult(intent, 1);
    }

    //将uri对应的文件复制一份到私有目录，之后就可以操作复制后的File了
    @RequiresApi(Build.VERSION_CODES.Q)
    public File uriToFileApiQ(Context context, Uri uri) {
        File file = null;
        if (uri == null) return file;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            String displayName = "uritofile" + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));
            InputStream is = null;
            try {
                is = contentResolver.openInputStream(uri);
                File cache = new File(context.getCacheDir().getAbsolutePath(), displayName);
                FileOutputStream fos = new FileOutputStream(cache);
                byte[] b = new byte[1024];
                while ((is.read(b)) != -1) {
                    fos.write(b);// 写入数据
                }
                file = cache;
                fos.close();
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 6:
                    if (resultCode == RESULT_OK) {
                        Uri uri = data.getData();
                        String path = FileUtil.getPath(this, uri);
                        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
                        View view = inflater.inflate(R.layout.more_dialog, null);
                        TextView t = view.findViewById(R.id.more_title);
                        t.setText(path);
                    }
                    break;
                case 66:
                    if (mFilePathCallback != null) {
                        if (data != null && data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            Uri[] uris = new Uri[count];
                            int currentItem = 0;
                            while (currentItem < count) {
                                Uri fileUri = data.getClipData().getItemAt(currentItem).getUri();
                                uris[currentItem] = fileUri;
                                currentItem = currentItem + 1;
                            }
                            mFilePathCallback.onReceiveValue(uris);
                        } else {
                            Uri result = data == null ? null : data.getData();
                            mFilePathCallback.onReceiveValue(new Uri[]{result});
                        }
                        mFilePathCallback = null;
                    }
                    break;

                case 10086:
                    if (data.getData() != null) {
                        File file = uriToFileApiQ(this, data.getData());
                        try {
                            FileInputStream fs = new FileInputStream(file.getAbsolutePath());
                            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
                            String line;
                            StringBuffer result = new StringBuffer();
                            while ((line = br.readLine()) != null) {
                                result.append(line);
                            }
                            ToastUtil.Toast(IndexActivity.this, "导入中...");
                            HashMap<String, HashMap<String, String>> map = Util.importBookmarks(IndexActivity.this,String.valueOf(result));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 666:
                    ToastUtil.Toast(IndexActivity.this, "权限获取成功，再次点击安装即可");
                    break;
                case 11:
                    ToastUtil.Toast(IndexActivity.this, "");
                    break;
            }
            switch (requestCode) {
                case 1:
                    if (data != null) {
                        FileUtil file = new FileUtil();
                        String realPathFromUri = FileUtil.getRealPathFromUriAboveApi19(IndexActivity.this, data.getData());
                        Boolean img = getSharedPreferences("data", MODE_PRIVATE).edit().putString("indexBackground", realPathFromUri).commit();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                index.setBackground(Drawable.createFromPath(realPathFromUri));
                                goBack.setColorFilter(getResources().getColor(R.color.white));
                                goForward.setColorFilter(getResources().getColor(R.color.white));
                                Home.setColorFilter(getResources().getColor(R.color.white));
                                More.setColorFilter(getResources().getColor(R.color.white));
                                add.setColorFilter(getResources().getColor(R.color.white));
                                search.setColorFilter(getResources().getColor(R.color.white));
                                find.setColorFilter(getResources().getColor(R.color.white));
                                api.setColorFilter(getResources().getColor(R.color.white));
                                webtitle.setTextColor(getResources().getColor(R.color.white));
                                table_text.setTextColor(getResources().getColor(R.color.white));
                                pscode.setColorFilter(getResources().getColor(R.color.white));

                            }
                        });
                    } else {
                        ToastUtil toast = new ToastUtil();
                        ToastUtil.Toast(IndexActivity.this, "选择失败，请重新选择");
                    }
                    break;
            }
        }
    }

    //模拟60条数据
    public void Download_Dialog(String url, String contentDisposition, String mimeType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String size = "未知";
                String filename = HttpUtil.getFileName(url);
                //getFileName(url, contentDisposition, mimeType);
                URL myURL = null;
                try {
                    myURL = new URL(url);
                    URLConnection conn = myURL.openConnection();
                    int file_size = conn.getContentLength();
                    Log.w("file_size", "" + file_size);
                    if (file_size > 0) {
                        DecimalFormat df = new DecimalFormat("#.00");
                        if (file_size < 1024) {
                            size = df.format((double) file_size) + "BT";
                        } else if (file_size < 1048576) {
                            size = df.format((double) file_size / 1024) + "KB";
                        } else if (file_size < 1073741824) {
                            size = df.format((double) file_size / 1048576) + "MB";
                        } else {
                            size = df.format((double) file_size / 1073741824) + "GB";
                        }
                    } else {
                        size = "未知";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String finalSize = size;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
                        View view = inflater.inflate(R.layout.download_dialog, null);
                        final Dialog dialog = new Dialog(IndexActivity.this);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.show();
                        WindowManager windowManager = getWindowManager();
                        Display display = windowManager.getDefaultDisplay();
                        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                        lp.width = display.getWidth() - 120; // 设置宽度
                        dialog.getWindow().setAttributes(lp);
                        dialog.getWindow().setContentView(view);
                        EditText download_name = view.findViewById(R.id.download_edit);
                        TextView download_size = view.findViewById(R.id.download_size);
                        download_size.setText("文件大小：" + finalSize);
                        download_name.setText(filename);
                        TextView download_sure = view.findViewById(R.id.download_sure);
                        download_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                download(IndexActivity.this, url, download_name.getText().toString(), mimeType);
                                dialog.cancel();
                            }
                        });
                        TextView download_cancel = view.findViewById(R.id.download_cancel);
                        download_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.cancel();
                            }
                        });

                    }
                });

            }
        }).start();
        // 只呈现1s
    }

    //模拟60条数据
    public void Addbookmark_Dialog(String title, String content) {
        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.addbookmark_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 130; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);
        EditText bookmark_title = view.findViewById(R.id.bookmark_title);
        bookmark_title.setText(title);
        ListView recycler = view.findViewById(R.id.bookmark_dialog);
        LinearLayout add = view.findViewById(R.id.bookmark_add);
        EditText bookmark_content = view.findViewById(R.id.bookmark_content);
        bookmark_content.setText(content);
        TextView bookmark_list = view.findViewById(R.id.bookmark_list);
        TextView bookmark_cancel = view.findViewById(R.id.bookmark_cancel);
        TextView add_home = view.findViewById(R.id.add_home);
        add_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_home.getText().toString().equals("○ 同时添加到主页")) {
                    add_home.setText("● 同时添加到主页");
                } else {
                    add_home.setText("○ 同时添加到主页");
                }
            }
        });
        TextView bookmark_sure = view.findViewById(R.id.bookmark_sure);
        LinearLayout back = view.findViewById(R.id.back_dialog);
        LinearLayout bookmark_dialog_sure = view.findViewById(R.id.bookmark_dialog_sure);
        bookmark_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.setVisibility(View.VISIBLE);
                bookmark_dialog_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bookmark_list.setText(bookmarkpath.substring(bookmarkpath.lastIndexOf("bookmark")));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recycler.setVisibility(View.GONE);
                                add.setVisibility(View.VISIBLE);
                                back.setVisibility(View.GONE);
                                bookmark_dialog_sure.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (FilePath.size() > 1) {
                            FilePath.remove(FilePath.size() - 1);
                            showFiles(FilePath.remove(FilePath.size() - 1), recycler);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recycler.setVisibility(View.GONE);
                                    add.setVisibility(View.VISIBLE);
                                    back.setVisibility(View.GONE);
                                }
                            });
                        }

                    }
                });
                recycler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView li = view.findViewById(R.id.list_title);
                        TextView is = view.findViewById(R.id.list_url);
                        if (is.getText().toString().equals("文件夹")) {
                            showFiles(bookmarkpath + "/" + li.getText().toString(), recycler);
                            bookmark_dialog_sure.setVisibility(View.VISIBLE);
                        }

                    }

                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setVisibility(View.VISIBLE);
                        add.setVisibility(View.GONE);
                    }
                });
                File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                showFiles(folder.getAbsolutePath(), recycler);

            }
        });

        bookmark_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookmarkpath == null || bookmarkpath.equals("")) {
                    bookmarkpath = IndexActivity.this.getExternalFilesDir("bookmark").getAbsolutePath();
                    createFile(bookmarkpath + "/" + bookmark_title.getText().toString(), content);
                    if (add_home.getText().toString().equals("● 同时添加到主页")) {
                        SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        if (db.isOpen()) {
                            String sql = "insert into Collect(title,content) values(\"" + bookmark_title.getText().toString() + "\",\"" + content + "\")";
                            db.execSQL(sql);

                        }
                    }
                } else {
                    createFile(bookmarkpath + "/" + bookmark_title.getText().toString(), content);
                    if (add_home.getText().toString().equals("● 同时添加到主页")) {
                        SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        if (db.isOpen()) {
                            String sql = "insert into Collect(title,content) values(\"" + bookmark_title.getText().toString() + "\",\"" + content + "\")";
                            db.execSQL(sql);

                        }
                    }
                }
                dialog.cancel();
            }
        });
        bookmark_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        // 只呈现1s
    }

    //模拟60条数据
    public void getCookie_Dialog() {
        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.data_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 130; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);

        TextView data_title = view.findViewById(R.id.data_title);
        TextView data_content = view.findViewById(R.id.data_content);
        data_title.setText("Cookie");
        TextView data_sure = view.findViewById(R.id.data_sure);
        data_sure.setText("复制");
        if (Cookie == null || Cookie.equals("")) {
            data_content.setText("暂无Cookie");
        } else {
            data_content.setText(Cookie);
        }
        TextView data_cancel = view.findViewById(R.id.data_cancel);
        data_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) IndexActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", data_content.getText().toString());
                manager.setPrimaryClip(mClipData);
                ToastUtil.Toast(IndexActivity.this, "已复制");
                dialog.cancel();
            }

        });
        data_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        // 只呈现1s
    }

    //模拟60条数据
    public void setPath_Dialog() {
        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.more_dialog, null);
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 150; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);
        EditText more_name = view.findViewById(R.id.more_name);
        EditText more_content = view.findViewById(R.id.more_content);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        more_name.setText(sharedPreferences.getString("download", "/storage/emulated/0/Download/"));
        more_name.setFocusable(false);//不可编辑
        more_content.setVisibility(View.GONE);
        TextView more_title = view.findViewById(R.id.more_title);
        more_title.setText("下载路径");
        TextView more_sure = view.findViewById(R.id.more_sure);
        TextView more_cancel = view.findViewById(R.id.more_cancel);
        more_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri1 = Uri.parse("content:///storage/emulated/0/");
                Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                intent1.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri1);
                startActivityForResult(intent1, 11);

            }
        });
        more_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (more_title.getText().toString() == "" || more_content.getText().toString() == "") {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.Toast(IndexActivity.this, "不能为空");
                        }
                    });
                } else {
                    SharedPreferences.Editor sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE).edit();
                    sharedPreferences.putString("网页大小", more_name.getText().toString());
                    sharedPreferences.putString("字体大小", more_content.getText().toString());
                    dialog.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.Toast(IndexActivity.this, "设置成功");
                        }
                    });
                }
            }

        });
        more_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    //模拟60条数据
    public void Engines_Dialog() {
        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.more_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 150; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);
        EditText more_name = view.findViewById(R.id.more_name);
        more_name.setHint("搜索引擎名称");
        EditText more_content = view.findViewById(R.id.more_content);
        more_content.setHint("搜索引擎");
        TextView more_title = view.findViewById(R.id.more_title);
        more_title.setText("添加搜索引擎");
        TextView more_sure = view.findViewById(R.id.more_sure);
        TextView more_cancel = view.findViewById(R.id.more_cancel);
        more_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(more_name.getText().toString().equals("")||more_content.getText().toString().equals(""))
                {
                    ToastUtil.Toast(IndexActivity.this,"不能为空");
                }else {
                    SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    if (db.isOpen()) {
                        Cursor cursorr = db.rawQuery("SELECT title FROM Engines WHERE title='" + more_name.getText().toString() + "'", null);
                        if (cursorr.getCount() > 0) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil t = new ToastUtil();
                                    ToastUtil.Toast(IndexActivity.this, "名称已经存在，换一个吧");
                                }
                            });
                        } else {
                            String sql = "insert into Engines(title,content) values(\"" + more_name.getText().toString() + "\",\"" + more_content.getText().toString() + "\")";
                            db.execSQL(sql);

                            ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                            //Cursor cursor = db.rawQuery("select * from bookmarkDB", null);
                            //查询语句也可以这样写
                            Cursor cursor = db.query("Engines", null, null, null, null, null, "id desc");
                            SharedPreferences sharedPreferences = getSharedPreferences("Engines", MODE_PRIVATE);
                            String Engines = sharedPreferences.getString("Engines", "百度");
                            if (cursor != null && cursor.getCount() > 0) {
                                if_exsit = true;
                                while (cursor.moveToNext()) {
                                    get_id = cursor.getInt(0);//得到int型的自增变量
                                    String get_title = cursor.getString(1);
                                    String get_content = cursor.getString(2);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("title", get_title);
                                    map.put("Engines", Engines);
                                    // map.put("is",)
                                    list.add(map);
                                    moreAdpter = new MoreAdpter(list, "engines");
                                    more_list.setAdapter(moreAdpter);
                                    moreAdpter.setOnItemClickListener(new MoreAdpter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position, String name) {
                                            TextView tv = view.findViewById(R.id.user);
                                            if (tv.getText().toString().equals("○")) {
                                                tv.setText("●");
                                                SharedPreferences.Editor sharedPreferences = getSharedPreferences("Engines", MODE_PRIVATE).edit();
                                                sharedPreferences.putString("Engines", name);
                                                sharedPreferences.commit();
                                                // recyclerView -  RecyclerView 控件变量名
                                                RecyclerView.LayoutManager manager = more_list.getLayoutManager();
                                                // 方式一  position : 你需要获取的对应View的索引值即 index
                                                for (int i = 0; i < moreAdpter.getItemCount(); i++) {
                                                    View itemView = manager.getChildAt(i);
                                                    TextView t = itemView.findViewById(R.id.user);
                                                    if (position == i) {
                                                        continue;
                                                    } else {
                                                        t.setText("○");
                                                    }
                                                }
                                            }

                                        }
                                    });

                                }
                            }
                            db.close();
                            dialog.cancel();
                        }

                    }
                }
            }
        });
        more_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.cancel();
            }
        });
    }

    //模拟60条数据
    public void DIYDialog(String title, int i) {
        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.more_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 150; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);
        EditText more_name = view.findViewById(R.id.more_name);
        more_name.setText(title);
        EditText more_content = view.findViewById(R.id.more_content);
        more_content.setHint("为空即是恢复默认");
        TextView more_title = view.findViewById(R.id.more_title);
        more_title.setText(title);
        TextView more_sure = view.findViewById(R.id.more_sure);
        TextView more_cancel = view.findViewById(R.id.more_cancel);
        more_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (more_content.getText().toString().equals("")) {
                    getSharedPreferences("DIY", MODE_PRIVATE).edit().putString("content", more_content.getText().toString()).commit();
                    getSharedPreferences("DIY", MODE_PRIVATE).edit().putInt("title", 0).commit();
                } else {
                    getSharedPreferences("DIY", MODE_PRIVATE).edit().putString("content", more_content.getText().toString()).commit();
                    getSharedPreferences("DIY", MODE_PRIVATE).edit().putInt("title", i).commit();
                }

                dialog.cancel();
            }
        });
        more_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.cancel();
            }
        });
        // 只呈现1s
    }


    //模拟60条数据
    public void Menu_Dialog() {
        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.menu_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 150; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);
        TextView menu_1 = view.findViewById(R.id.menu_1);
        TextView menu_2 = view.findViewById(R.id.menu_2);
        TextView menu_1_check = view.findViewById(R.id.menu_1_check);
        TextView menu_2_check = view.findViewById(R.id.menu_2_check);
        menu_1_check.setVisibility(View.GONE);
        menu_2_check.setVisibility(View.GONE);

        menu_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Network_Dialog();
                dialog.cancel();
            }
        });
        menu_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editType = "html";
                dialog.cancel();
                String result = FileUtil.getTxtFromAssets(IndexActivity.this, "home.html");
                script_edit.setText(result);
                edit_script.setVisibility(View.VISIBLE);
            }
        });
    }

    //模拟60条数据
    public void Download_Menu() {
        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.menu_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 150; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);
        TextView menu_1 = view.findViewById(R.id.menu_1);
        menu_1.setText("内置下载器");
        TextView menu_2 = view.findViewById(R.id.menu_2);
        menu_2.setText("ADM");
        TextView menu_1_check = view.findViewById(R.id.menu_1_check);
        TextView menu_2_check = view.findViewById(R.id.menu_2_check);

        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        String home = sharedPreferences.getString("DownloadBy", "内置下载器");
        if (Objects.equals(home, "内置下载器")) {
            menu_1_check.setText("●");
        } else if (Objects.equals(home, "ADM")) {
            menu_2_check.setText("●");
        }

        menu_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor sharedPreferences = getSharedPreferences("data", MODE_PRIVATE).edit();
                sharedPreferences.putString("DownloadBy", "内置下载器");
                sharedPreferences.commit();
                dialog.cancel();
            }
        });
        menu_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor sharedPreferences = getSharedPreferences("data", MODE_PRIVATE).edit();
                sharedPreferences.putString("DownloadBy", "ADM");
                sharedPreferences.commit();
                dialog.cancel();
            }
        });
    }

    //模拟60条数据
    public void More_Menu(String type,String title,String content) {
        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.menu_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 150; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);
        TextView menu_1 = view.findViewById(R.id.menu_1);

        TextView menu_2 = view.findViewById(R.id.menu_2);

        TextView menu_1_check = view.findViewById(R.id.menu_1_check);
        TextView menu_2_check = view.findViewById(R.id.menu_2_check);
        if(Objects.equals(type, "搜索引擎"))
        {

            menu_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.cancel();
                }
            });
        }else if(Objects.equals(type, "主页"))
        {
            menu_1.setText("编辑");
            menu_2.setText("删除");
            menu_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editType = "edithtml";
                    dialog.cancel();
                    String toresult = new String(Base64.decode(content.getBytes(), Base64.DEFAULT));
                    script_edit.setText(toresult);
                    edit_name.setText(title);
                    edit_script.setVisibility(View.VISIBLE);

                }
            });
            menu_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.cancel();
                }
            });
        }else if(Objects.equals(type, "浏览器标识"))
        {
            menu_2.setText("删除");
            menu_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.cancel();
                }
            });
        }

    }

    //模拟60条数据
    public void Home_Dialog(String content) {

        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.more_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);

        EditText more_name = view.findViewById(R.id.more_name);
        EditText more_content = view.findViewById(R.id.more_content);
        more_content.setVisibility(View.GONE);
        TextView more_title = view.findViewById(R.id.more_title);
        more_title.setText("DIY主页");
        TextView more_sure = view.findViewById(R.id.more_sure);
        TextView more_cancel = view.findViewById(R.id.more_cancel);

        more_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (more_name.getText().toString().equals("") || more_content.getText().toString().equals("")) {
                    ToastUtil.Toast(IndexActivity.this, "不能为空");
                } else {
                    SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    if (db.isOpen()) {
                        Cursor cursorr = db.rawQuery("SELECT title FROM Homepage WHERE title='" + "本地主页：" + more_name.getText().toString() + "'", null);
                        if (cursorr.getCount() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil t = new ToastUtil();
                                    ToastUtil.Toast(IndexActivity.this, "名称已经存在，换一个吧");
                                }
                            });

                        } else {
                            String toresult = Base64.encodeToString(content.getBytes(), Base64.DEFAULT);
                            String sql = "insert into Homepage(title,content) values(\"" + "本地主页：" + more_name.getText().toString() + "\",\"" + toresult + "\")";
                            db.execSQL(sql);
                            dialog.cancel();
                            ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                            Cursor cursor = db.query("Homepage", null, null, null, null, null, "id desc");
                            SharedPreferences sharedPreferences = getSharedPreferences("Homepage", MODE_PRIVATE);
                            String home = sharedPreferences.getString("Homepage", "主页");
                            if (cursor != null && cursor.getCount() > 0) {
                                if_exsit = true;
                                while (cursor.moveToNext()) {
                                    get_id = cursor.getInt(0);//得到int型的自增变量
                                    String get_title = cursor.getString(1);
                                    String get_content = cursor.getString(2);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("title", get_title);
                                    map.put("Homepage", home);
                                    list.add(map);
                                    moreAdpter = new MoreAdpter(list, "homepage");
                                    more_list.setAdapter(moreAdpter);
                                    moreAdpter.setOnItemClickListener(new MoreAdpter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position, String name) {

                                            TextView tv = view.findViewById(R.id.user);

                                            if (tv.getText().toString().equals("○")) {
                                                tv.setText("●");
                                                SharedPreferences.Editor sharedPreferences = getSharedPreferences("Homepage", MODE_PRIVATE).edit();
                                                sharedPreferences.putString("Homepage", name);
                                                sharedPreferences.commit();
                                                // recyclerView -  RecyclerView 控件变量名
                                                RecyclerView.LayoutManager manager = more_list.getLayoutManager();
                                                // 方式一  position : 你需要获取的对应View的索引值即 index
                                                for (int i = 0; i < moreAdpter.getItemCount(); i++) {
                                                    View itemView = manager.getChildAt(i);
                                                    TextView t = itemView.findViewById(R.id.user);
                                                    if (position == i) {
                                                        continue;
                                                    } else {
                                                        t.setText("○");
                                                    }
                                                }

                                            }

                                        }
                                    });

                                }
                            }
                            db.close();
                        }


                    }
                }
            }
        });

        more_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    //模拟60条数据
    public void Network_Dialog() {

        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.more_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);

        EditText more_name = view.findViewById(R.id.more_name);
        EditText more_content = view.findViewById(R.id.more_content);
        more_name.setHint("主页名称");
        more_content.setHint("在线链接");
        TextView more_title = view.findViewById(R.id.more_title);
        more_title.setText("添加在线主页");
        TextView more_sure = view.findViewById(R.id.more_sure);
        TextView more_cancel = view.findViewById(R.id.more_cancel);
        more_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(more_name.getText().toString().equals("")||more_content.getText().toString().equals(""))
                {
                    ToastUtil.Toast(IndexActivity.this,"不能为空");
                }else
                {
                SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db.isOpen()) {
                    Cursor cursorr = db.rawQuery("SELECT title FROM Homepage WHERE title='" + "在线主页：" + more_name.getText().toString() + "'", null);
                    if (cursorr.getCount() > 0) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil t = new ToastUtil();
                                ToastUtil.Toast(IndexActivity.this, "名称已经存在，换一个吧");
                            }
                        });

                    } else {
                        String sql = "insert into Homepage(title,content) values(\"" + "在线主页：" + more_name.getText().toString() + "\",\"" + more_content.getText().toString() + "\")";
                        db.execSQL(sql);
                        dialog.cancel();
                        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

                        Cursor cursor = db.query("Homepage", null, null, null, null, null, "id desc");
                        SharedPreferences sharedPreferences = getSharedPreferences("Homepage", MODE_PRIVATE);
                        String home = sharedPreferences.getString("Homepage", "主页");
                        if (cursor != null && cursor.getCount() > 0) {
                            if_exsit = true;
                            while (cursor.moveToNext()) {
                                get_id = cursor.getInt(0);//得到int型的自增变量
                                String get_title = cursor.getString(1);
                                String get_content = cursor.getString(2);
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("title", get_title);
                                map.put("Homepage", home);
                                // map.put("is",)
                                list.add(map);
                                //new String  数据来源， new int 数据到哪去\

                                // moreAdpter=new MoreAdpter(get_title,get_content);
                                moreAdpter = new MoreAdpter(list, "homepage");
                                more_list.setAdapter(moreAdpter);
                                moreAdpter.setOnItemClickListener(new MoreAdpter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position, String name) {

                                        TextView tv = view.findViewById(R.id.user);

                                        if (tv.getText().toString().equals("○")) {
                                            tv.setText("●");
                                            SharedPreferences.Editor sharedPreferences = getSharedPreferences("Homepage", MODE_PRIVATE).edit();
                                            sharedPreferences.putString("Homepage", name);
                                            sharedPreferences.commit();
                                            // recyclerView -  RecyclerView 控件变量名
                                            RecyclerView.LayoutManager manager = more_list.getLayoutManager();
                                            // 方式一  position : 你需要获取的对应View的索引值即 index
                                            for (int i = 0; i < moreAdpter.getItemCount(); i++) {
                                                View itemView = manager.getChildAt(i);
                                                TextView t = itemView.findViewById(R.id.user);
                                                if (position == i) {
                                                    continue;
                                                } else {
                                                    t.setText("○");
                                                }
                                            }
                                            // 方式二  position : 你需要获取的对应View的索引值即 index
                                            //View itemView2 = manager.findViewByPosition(position);

                                        }

                                    }
                                });

                            }
                        }
                        db.close();
                    }

                }
            }
            }
        });
        more_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    //模拟60条数据
    public void UserAgent_Dialog() {
        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.more_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);

        EditText more_name = view.findViewById(R.id.more_name);
        EditText more_content = view.findViewById(R.id.more_content);
        more_name.setHint("UserAgent名称");
        more_content.setHint("UserAgent");
        TextView more_title = view.findViewById(R.id.more_title);
        more_title.setText("添加UserAgent");
        TextView more_sure = view.findViewById(R.id.more_sure);
        TextView more_cancel = view.findViewById(R.id.more_cancel);

        more_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(more_name.getText().toString().equals("")||more_content.getText().toString().equals(""))
                {
                    ToastUtil.Toast(IndexActivity.this,"不能为空");
                }else {
                    SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    if (db.isOpen()) {
                        Cursor cursorr = db.rawQuery("SELECT title FROM UserAgent WHERE title='" + more_name.getText().toString() + "'", null);
                        if (cursorr.getCount() > 0) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil t = new ToastUtil();
                                    ToastUtil.Toast(IndexActivity.this, "名称已经存在，换一个吧");
                                }
                            });

                        } else {
                            String sql = "insert into UserAgent(title,content) values(\"" + more_name.getText().toString() + "\",\"" + more_content.getText().toString() + "\")";
                            db.execSQL(sql);
                            dialog.cancel();
                            ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                            Cursor cursor = db.query("UserAgent", null, null, null, null, null, "id desc");
                            SharedPreferences sharedPreferences = getSharedPreferences("UserAgent", MODE_PRIVATE);
                            String ua = sharedPreferences.getString("UserAgent", "手机");
                            if (cursor != null && cursor.getCount() > 0) {
                                if_exsit = true;
                                while (cursor.moveToNext()) {
                                    get_id = cursor.getInt(0);//得到int型的自增变量
                                    String get_title = cursor.getString(1);
                                    String get_content = cursor.getString(2);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("title", get_title);
                                    map.put("UserAgent", ua);
                                    // map.put("is",)
                                    list.add(map);

                                    moreAdpter = new MoreAdpter(list, "useragent");
                                    more_list.setAdapter(moreAdpter);
                                    moreAdpter.setOnItemClickListener(new MoreAdpter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position, String name) {

                                            TextView tv = view.findViewById(R.id.user);

                                            if (tv.getText().toString().equals("○")) {
                                                tv.setText("●");
                                                SharedPreferences.Editor sharedPreferences = getSharedPreferences("UserAgent", MODE_PRIVATE).edit();
                                                sharedPreferences.putString("UserAgent", name);
                                                sharedPreferences.commit();
                                                // recyclerView -  RecyclerView 控件变量名
                                                RecyclerView.LayoutManager manager = more_list.getLayoutManager();
                                                // 方式一  position : 你需要获取的对应View的索引值即 index
                                                for (int i = 0; i < moreAdpter.getItemCount(); i++) {
                                                    View itemView = manager.getChildAt(i);
                                                    TextView t = itemView.findViewById(R.id.user);
                                                    if (position == i) {
                                                        continue;
                                                    } else {
                                                        t.setText("○");
                                                    }
                                                }

                                            }

                                        }
                                    });

                                }
                            }
                            db.close();
                        }

                    }
                }
            }
        });
        more_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.cancel();
            }
        });
        // 只呈现1s
    }

    public void createFile(String path, String content) {

        File mFile = new File(path);
        //判断文件是否存在，存在就删除
        if (mFile.exists()) {
            mFile.delete();
        }
        try {
            //创建文件
            mFile.createNewFile();
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(path, true), "gbk");
            osw.write(content);
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //模拟60条数据
    public void addAdblock_Dialog() {

        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.addadblock_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);


        EditText adblock_edit = view.findViewById(R.id.adblock_edit);
        TextView adblock_sure = view.findViewById(R.id.adblock_sure);
        adblock_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getFilesDir()获取你app的内部存储空间
                File folder = IndexActivity.this.getExternalFilesDir("Adblock");
                //File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                downloadAdblock(folder.getPath(), adblock_edit.getText().toString());
                dialog.cancel();
            }
        });

        TextView adblock_cancel = view.findViewById(R.id.adblock_cancel);
        adblock_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        // 只呈现1s
    }

    //模拟60条数据
    public void Addfile_Dialog() {
        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = inflater.inflate(R.layout.addfile_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 150; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);

        EditText file_edit = view.findViewById(R.id.file_edit);
        TextView file_sure = view.findViewById(R.id.file_sure);
        file_edit.setHint("收藏夹名称");
        file_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(file_edit.getText().toString().equals(""))
                {
                    ToastUtil.Toast(IndexActivity.this,"不能为空");
                }else {
                    File Folder = new File(bookmarkpath, file_edit.getText().toString());
                    if (!Folder.exists()) {
                        Folder.mkdir();
                        boolean isFilemaked1 = Folder.isDirectory();
                        boolean isFilemaked2 = Folder.mkdirs();
                        if (isFilemaked1 || isFilemaked2) {
                                    ToastUtil t = new ToastUtil();
                                    ToastUtil.Toast(IndexActivity.this, "收藏夹创建成功");
                                    File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                                    showFile(folder.getAbsolutePath());
                                    dialog.cancel();
                        } else {
                                    ToastUtil t = new ToastUtil();
                                    ToastUtil.Toast(IndexActivity.this, "收藏夹创建失败");
                                      dialog.cancel();
                        }
                    } else {
                        ToastUtil t = new ToastUtil();
                        ToastUtil.Toast(IndexActivity.this, "收藏夹已经存在");
                    }
                }
            }
        });

        TextView file_cancel = view.findViewById(R.id.file_cancel);
        file_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        // 只呈现1s
    }

    //数据库添加数据（历史记录表）
    public void add_history(String title, String url) {
        //第二个参数是数据库名
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"); //设置时间格式
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")); //设置时区
        Date curDate = new Date(System.currentTimeMillis()); //获取当前时间
        String createDate = formatter.format(curDate);   //格式转换

        SQLiteOpenHelper dbHelper = new Database(this, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("url", url);
        values.put("date", createDate);
        //insert（）方法中第一个参数是表名，第二个参数是表示给表中未指定数据的自动赋值为NULL。第三个参数是一个ContentValues对象
        db.insert("History", null, values);
        query_history();
    }

    //数据库逆序查询函数：
    public void query_history() {
        array.clear();
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
        //第二个参数是数据库名
        SQLiteOpenHelper dbHelper = new Database(this, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Cursor cursor = db.rawQuery("select * from bookmarkDB", null);
        //查询语句也可以这样写
        Cursor cursor = db.query("History", null, null, null, null, null, "id desc");
        if (cursor != null && cursor.getCount() > 0) {
            if_exsit = true;
            while (cursor.moveToNext()) {
                get_id = cursor.getInt(0);//得到int型的自增变量
                String get_title = cursor.getString(1);
                String get_url = cursor.getString(2);
                String get_date = cursor.getString(3);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("title", get_title);
                map.put("url", get_url);
                map.put("date", get_date);
                array.add(get_id);
                listItem.add(map);
                //new String  数据来源， new int 数据到哪去
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"title", "url", "date"}, new int[]{R.id.list_title, R.id.list_url, R.id.list_date});
                settinglist2.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }
        } else {
            if_exsit = false;
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("title", "暂时没有浏览记录");
            map.put("url", "此处是存放您历史记录地方");
            listItem.add(map);
            //new String  数据来源， new int 数据到哪去
            SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"title", "url"}, new int[]{R.id.list_title, R.id.list_url});
            settinglist2.setAdapter(mSimpleAdapter);//为ListView绑定适配器
        }

        cursor.close();
        db.close();
    }

    //数据库逆序查询函数：
    public void query_collect() {
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
        SQLiteOpenHelper dbHelper = new Database(this, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Card", null, null, null, null, null, "id desc");
        if (cursor != null && cursor.getCount() > 0) {
            if_exsit = true;
            while (cursor.moveToNext()) {
                String get_title = cursor.getString(1);
                String get_content = cursor.getString(2);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("title", get_title);
                map.put("content", get_content);
                listItem.add(map);
                //new String  数据来源， new int 数据到哪去
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.collect_item, new String[]{"title", "content"}, new int[]{R.id.list_title, R.id.list_url});
                settinglist4.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }
        } else {
            if_exsit = false;
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("title", "暂时没有收藏");
            map.put("content", "此处是存放您收藏的地方");
            listItem.add(map);
            //new String  数据来源， new int 数据到哪去
            SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.collect_item, new String[]{"title", "content"}, new int[]{R.id.list_title, R.id.list_url});
            settinglist4.setAdapter(mSimpleAdapter);//为ListView绑定适配器
        }
        cursor.close();
        db.close();
    }

    //按值查找：
    public String query_history_id(int position) {
        SQLiteOpenHelper dbHelper = new Database(this, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from History", null);
        while (cursor.moveToNext()) {
            url = cursor.getString(2);
            String titler = cursor.getString(1);
            int id = cursor.getInt(0);
            //找到id相等的就返回url
            if (array.get(position).equals(id))
                break;
        }
        cursor.close();
        db.close();
        return url;
    }



    //删除函数：
    public void history_empty() {
        SQLiteOpenHelper dbHelper = new Database(this, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("History", null, null);
        db.close();
        ToastUtil.Toast(IndexActivity.this, "清空成功");
        //删除后清空数组，重新放入数据，刷新UI
        array.clear();

    }


    //按值查找：
    public String query_homepage_name(String name) {
        SQLiteOpenHelper dbHelper = new Database(this, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Homepage", null);
        String content=null;
        while (cursor.moveToNext()) {
            content = cursor.getString(2);
            String titler = cursor.getString(1);
            if (titler.equals(name))
                break;
        }
        cursor.close();
        db.close();
        return content;
    }

    public void showDownloadWeb(String path) {
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
        File file = new File(path);
        //判断文件是否存在
        if (file.exists()) {
            //将文件转变成文件数组
            File[] files = file.listFiles();
            //判断文件是否为空
            if (files.length == 0) {
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"title"}, new int[]{R.id.list_title});
                settinglist3.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }

            for (File file2 : files) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                //判断文件是否是文件夹9
                if (file2.isDirectory()) {
                } else {
                    map.put("title", file2.getName());
                    listItem.add(map);
                }
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"title", "url"}, new int[]{R.id.list_title, R.id.list_url});
                settinglist3.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }
        } else {

        }
    }

    public void showFile(String path) {
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
        final ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
        File file = new File(path);
        //判断文件是否存在
        bookmarkpath = file.getAbsolutePath();
        FilePath.add(bookmarkpath);
        if (file.exists()) {
            //将文件转变成文件数组
            File[] files = file.listFiles();
            //判断文件是否为空
            if (files.length == 0) {
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"title", "url"}, new int[]{R.id.list_title, R.id.list_url});
                settinglist.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }

            for (File file2 : files) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                HashMap<String, Object> f = new HashMap<String, Object>();
                //判断文件是否是文件夹9
                if (file2.isDirectory()) {
                    String mformatType = "yyyy/MM/dd HH:mm:ss";
                    Calendar cal = Calendar.getInstance();
                    long time = file.lastModified();
                    SimpleDateFormat formatter = new SimpleDateFormat(mformatType);
                    cal.setTimeInMillis(time);
                    map.put("title", file2.getName());
                    map.put("url", "文件夹");
                    map.put("data", formatter.format(cal.getTime()));
                    listItem.add(map);
                } else {
                    try {
                        String mformatType = "yyyy/MM/dd HH:mm:ss";
                        Calendar cal = Calendar.getInstance();
                        long time = file.lastModified();
                        SimpleDateFormat formatter = new SimpleDateFormat(mformatType);
                        cal.setTimeInMillis(time);
                        FileInputStream fs = new FileInputStream(file2.getAbsolutePath());
                        BufferedReader br = new BufferedReader(new InputStreamReader(fs));
                        String line;
                        StringBuffer result = new StringBuffer();
                        while ((line = br.readLine()) != null) {
                            //将读取到的内容放入结果字符串
                            result.append(line);
                        }
                        f.put("title", file2.getName());
                        f.put("url", result);
                        f.put("data", formatter.format(cal.getTime()));
                        list.add(f);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            listItem.addAll(list);
            SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"title", "url", "data"}, new int[]{R.id.list_title, R.id.list_url, R.id.list_date});
            settinglist.setAdapter(mSimpleAdapter);//为ListView绑定适配器

        } else {
            System.out.println("文件不存在！");
        }
    }

    public void showAdblock(String path) {
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
        File file = new File(path);
        //判断文件是否存在
        if (file.exists()) {
            //将文件转变成文件数组
            File[] files = file.listFiles();
            //判断文件是否为空
            if (files.length == 0) {
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list, new String[]{"title"}, new int[]{R.id.list_title});
                adblock_list.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }

            for (File file2 : files) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                //判断文件是否是文件夹9
                if (file2.isDirectory()) {
                } else {
                    map.put("title", file2.getName());
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();//指定SharedPreferences的文件名为data并得到了SharedPreferences.Editor对象
                    //添加三条不同的数据类型
                    String b = PreferenceManager.getDefaultSharedPreferences(this).getString(file2.getName(), "○");
                    map.put("user", b);
                    listItem.add(map);

                }

                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list, new String[]{"title", "user"}, new int[]{R.id.list_title, R.id.adblock_user});
                adblock_list.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }
        } else {
            System.out.println("文件不存在！");
        }
    }

    public void downloadAdblock(String path, String url) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                String adb = DownloadHelper.downloadAdblock(url);
                if (adb != null || adb != "") {
                    String name = adb.substring(adb.indexOf("Title:") + 7, adb.indexOf("! Last"));
                    createFile(path + "/" + name, adb);
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();//指定SharedPreferences的文件名为data并得到了SharedPreferences.Editor对象
                    //添加三条不同的数据类型
                    editor.putString(name, "●");
                    //调用apply（）进行提交
                    editor.commit();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            File folder = IndexActivity.this.getExternalFilesDir("Adblock");
                            showAdblock(folder.getAbsolutePath());
                        }
                    });

                }
            }
        }).start();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil t = new ToastUtil();
                ToastUtil.Toast(IndexActivity.this, "订阅中...");
            }
        });
    }

    public void showFiles(String path, ListView view) {
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
        File file = new File(path);
        //判断文件是否存在
        bookmarkpath = file.getAbsolutePath();
        FilePath.add(bookmarkpath);
        if (file.exists()) {
            //将文件转变成文件数组
            File[] files = file.listFiles();
            //判断文件是否为空
            if (files.length == 0) {
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"title", "url"}, new int[]{R.id.list_title, R.id.list_url});
                view.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }
            for (File file2 : files) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                //判断文件是否是文件夹9
                if (file2.isDirectory()) {
                    map.put("title", file2.getName());
                    map.put("url", "文件夹");
                    listItem.add(map);
                }
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"title", "url"}, new int[]{R.id.list_title, R.id.list_url});
                view.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }
        } else {
            System.out.println("文件不存在！");
        }
    }

    //删除函数：
    public void delete(int position) {
        SQLiteOpenHelper dbHelper = new Database(this, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("History", "id=?", new String[]{String.valueOf(array.get(position))});
        db.close();
        ToastUtil.Toast(IndexActivity.this, "删除成功");
        //删除后清空数组，重新放入数据，刷新UI
        array.clear();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        table_text = findViewById(R.id.table);
        table = findViewById(R.id.table_dialog);
        ps = findViewById(R.id.pg);
        goBack = findViewById(R.id.goBack);
        goForward = findViewById(R.id.goForward);
        webviewgm = findViewById(R.id.WebViewGm);
        text_bookmark = findViewById(R.id.text_bookmark);
        text_history = findViewById(R.id.text_history);
        text_collect = findViewById(R.id.text_collect);
        downloadsList = findViewById(R.id.downloadsList);
        framewebview = findViewById(R.id.framewebview);
        index = findViewById(R.id.index);
        new_script = findViewById(R.id.new_script);
        add_file = findViewById(R.id.add_file);

        more_add = findViewById(R.id.more_add);
        WebConfig = findViewById(R.id.WebConfig);
        search = findViewById(R.id.search);
        config_log = findViewById(R.id.config_log);
        config_tool = findViewById(R.id.config_tool);
        config_cookie = findViewById(R.id.config_cookie);
        api = findViewById(R.id.loadapi);
        webdownload = findViewById(R.id.webdownload);
        downloadweb = findViewById(R.id.downloadweb);
        QR = findViewById(R.id.QR);
        view_image = findViewById(R.id.view_image);
        webSettings = findViewById(R.id.WebSettings);
        goto_desktop = findViewById(R.id.goto_desktop);
        downloads_ok = findViewById(R.id.downloads_ok);
        downloaded = findViewById(R.id.downloaded);
        top = findViewById(R.id.top);
        about = findViewById(R.id.about);
        SizeSettings = findViewById(R.id.size_settings);
        back_setting = findViewById(R.id.back_setting);
        back_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (web.canGoBack()) {
                    web.goBack();//返回上一页面
                } else {
                    frame.setVisibility(View.GONE);
                }
            }
        });
        back_script = findViewById(R.id.back_script);
        back_script.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                script.setVisibility(View.GONE);
            }
        });
        back_more = findViewById(R.id.back_more);
        back_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more.setVisibility(View.GONE);
            }
        });
        back_editor = findViewById(R.id.back_editor);
        back_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_script.setVisibility(View.GONE);
            }
        });
        back_download = findViewById(R.id.back_download);
        back_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadsList.setVisibility(View.GONE);
            }
        });
        back_downloaded = findViewById(R.id.back_downloaded);
        back_downloaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloaded.setVisibility(View.GONE);
            }
        });
        back_bookmark = findViewById(R.id.back_bookmark);
        back_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              viewlist.setVisibility(View.GONE);
            }
        });
        back_code = findViewById(R.id.back_code);
        back_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QR.setVisibility(View.GONE);
            }
        });
        back_about = findViewById(R.id.back_about);
        back_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about.setVisibility(View.GONE);
            }
        });
        back_websetting = findViewById(R.id.back_websetting);
        back_websetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webSettings.setVisibility(View.GONE);
            }
        });
        back_sizesetting = findViewById(R.id.back_sizesetting);
        back_sizesetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SizeSettings.setVisibility(View.GONE);
            }
        });
        ImageView back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_image.setVisibility(View.GONE);
            }
        });

        fileManager = new FileManager(IndexActivity.this);
        Webapk = getSharedPreferences("data", MODE_PRIVATE).getString("setting_apk", "询问");
        Webcamera = getSharedPreferences("data", MODE_PRIVATE).getBoolean("setting_camera", false);
        Webimg = getSharedPreferences("data", MODE_PRIVATE).getBoolean("setting_img", true);
        Webjavascript = getSharedPreferences("data", MODE_PRIVATE).getBoolean("setting_javascript", true);
        Webposition = getSharedPreferences("data", MODE_PRIVATE).getBoolean("setting_position", false);
        Webmic = getSharedPreferences("data", MODE_PRIVATE).getBoolean("setting_mic", false);
        Engines = getSharedPreferences("Engines", MODE_PRIVATE).getString("Engines", "百度");
        Homepage = getSharedPreferences("Homepage", MODE_PRIVATE).getString("Homepage", "本地主页：主页");
        SQLiteOpenHelper help = MyDatabase.getInstance(IndexActivity.this);
        SQLiteDatabase readDatabase = help.getReadableDatabase();
        ScriptStoreSQLite scriptStore = new ScriptStoreSQLite(IndexActivity.this);
        scriptStore.open();
        this.scriptStore = scriptStore;
        scripts = scriptStore.getAll();
        script = findViewById(R.id.script);
        script_list = findViewById(R.id.script_recyclerview);
        home_list = findViewById(R.id.home_list);
        home_list.setLayoutManager(new LinearLayoutManager(this));
        script_list.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<String>();
        adapter = new MyAdapter(list, List);
        home_list.setAdapter(adapter);

        myAdapter = new MyPagerAdapter();
        viewpager = findViewById(R.id.viewpager);
        OverScrollDecoratorHelper.setUpOverScroll(viewpager);
        viewpager.setAdapter(myAdapter);
        viewpager.setOffscreenPageLimit(20);
        viewpager.setCanSwipe(false);
        more_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(type, "搜索引擎")) {
                    Engines_Dialog();
                } else if (Objects.equals(type, "主页")) {
                    Menu_Dialog();
                } else if (Objects.equals(type, "浏览器标识")) {
                    UserAgent_Dialog();
                }

            }
        });
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                String uri = intent.getStringExtra(Intent.EXTRA_TEXT);
                addView(ListViews, uri, 0, null);
            } else if ("application/xhtml+xml".equals(type) || "text/html".equals(type) || "application/vnd.wap.xhtml+xml".equals(type)) {
                String uri = intent.getStringExtra(Intent.EXTRA_TEXT);
                addView(ListViews, uri, 0, null);
            }

        } else if (intent.getData() != null) {
            addView(ListViews, String.valueOf(intent.getData()), 0, null);
        } else {
            initWeb();
        }
        frame = findViewById(R.id.frame);
        table_add = findViewById(R.id.add_table);
        full = findViewById(R.id.full);
        RelativeLayout.LayoutParams paramsss = (RelativeLayout.LayoutParams) full.getLayoutParams();
        paramsss.bottomMargin = IndexActivity.getNavBarHeight(IndexActivity.this) + SizeUtil.dp2px(IndexActivity.this, 45);
        full.setLayoutParams(paramsss);

        table_text.setText(String.valueOf(final_count));

        downloads_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DownloadManager.getInstance().initialize(IndexActivity.this, 10);
                        downloadss = new ArrayList<>();
                        //fileManager = new FileManager(context);
                        managerr = DownloadManager.getInstance();
                        managerr.addDownloadJobListener(jobListener);
                        List<DownloadInfo> infos = managerr.getDataInfo();
                        for (DownloadInfo info : infos) {
                            if (!info.isFinished()) continue;
                            downloadss.add(info);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloaded.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).start();
            }
        });
        goto_desktop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listview == 0) {

                } else {
                    try {
                        Intent shortcutIntent = new Intent(IndexActivity.this, IndexActivity.class);
                        shortcutIntent.setData(Uri.parse(itemlistViewAll.get(countlist.get(count)).get(listview).getUrl()));
                        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);// 加入action,和category之后，程序卸载的时候才会主动将该快捷方式也卸载
                        Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                        addIntent.putExtra("duplicate", false);    // 不重复创建快捷方式图标
                        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, itemlistViewAll.get(countlist.get(count)).get(listview).getTitle());
                        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, itemlistViewAll.get(countlist.get(count)).get(listview).getFavicon());
                        sendBroadcast(addIntent);
                        ToastUtil.Toast(IndexActivity.this, "创建桌面快捷方式成功");
                    } catch (Exception e) {
                        ToastUtil.Toast(IndexActivity.this, "创建桌面快捷方式失败：" + e);
                    }
                }
            }
        });

        toast_view = findViewById(R.id.toast_view);
        toast_cancel = findViewById(R.id.toast_cancel);
        toast_sure = findViewById(R.id.toast_sure);
        toast_content = findViewById(R.id.toast_content);

        save_pdf = findViewById(R.id.save_pdf);
        save_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                full.setVisibility(View.GONE);
                WebConfig.setVisibility(View.GONE);
                if (listview == 0) {
                    ToastUtil.Toast(IndexActivity.this, "当前页面不可操作");
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                        PrintDocumentAdapter printAdapter = itemlistViewAll.get(countlist.get(count)).get(listview).createPrintDocumentAdapter();
                        String jobName = getString(R.string.app_name) + " Document";
                        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
                    } else {
                        ToastUtil.Toast(IndexActivity.this, "系统不支持此功能");
                    }
                }

            }
        });

        qrCodeView = findViewById(R.id.qr_code_view);
        qrCodeView.setOnQRCodeListener(new QRCodeView.OnQRCodeRecognitionListener() {
            @Override
            public void onQRCodeRecognition(Result result) {
                Toast.makeText(IndexActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        pscode = findViewById(R.id.code);
        pscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QR.setVisibility(View.VISIBLE);
               // IndexActivity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
                qrCodeView.startPreview();
                qrCodeView.setOnQRCodeListener(new QRCodeView.OnQRCodeRecognitionListener() {
                    @Override
                    public void onQRCodeRecognition(Result result) {
                        QR.setVisibility(View.GONE);
                        addWebView(result.getText());
                        qrCodeView.stopPreview();
                    }
                });
            }
        });

        SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        config_cookie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                full.setVisibility(View.GONE);
                WebConfig.setVisibility(View.GONE);
                getCookie_Dialog();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WebConfig.getVisibility() == View.VISIBLE) {
                    full.setVisibility(View.GONE);
                    WebConfig.setVisibility(View.GONE);
                } else {
                    full.setVisibility(View.VISIBLE);
                    WebConfig.setVisibility(View.VISIBLE);
                }

            }
        });

        adblock = findViewById(R.id.adblock);
        adblock_add = findViewById(R.id.adblock_add);
        adblock_list = findViewById(R.id.adblock_list);

        adblock_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView li = view.findViewById(R.id.list_title);
                TextView user = view.findViewById(R.id.adblock_user);

                if (user.getText().toString().equals("●")) {
                    user.setText("○");
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();//指定SharedPreferences的文件名为data并得到了SharedPreferences.Editor对象
                    //添加三条不同的数据类型
                    editor.putString(li.getText().toString(), "○");
                    //调用apply（）进行提交
                    editor.commit();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            File folder = IndexActivity.this.getExternalFilesDir("Adblock");
                            showAdblock(folder.getAbsolutePath());
                        }
                    });
                } else {
                    user.setText("●");
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();//指定SharedPreferences的文件名为data并得到了SharedPreferences.Editor对象
                    //添加三条不同的数据类型
                    editor.putString(li.getText().toString(), "●");
                    //调用apply（）进行提交
                    editor.commit();
                    File folder = IndexActivity.this.getExternalFilesDir("Adblock");
                    showAdblock(folder.getAbsolutePath());

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil t = new ToastUtil();
                        ToastUtil.Toast(IndexActivity.this, user.getText().toString());
                    }
                });

            }
        });
        adblock_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAdblock_Dialog();
            }
        });
        add_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Addfile_Dialog();
            }
        });
        more_list = findViewById(R.id.more_list);
        more = findViewById(R.id.more);
        more_title = findViewById(R.id.more_title);
        more_text = findViewById(R.id.more_text);
        more_list.setLayoutManager(new LinearLayoutManager(this));
        more_list.setItemAnimator(new DefaultItemAnimator());

        go_back = findViewById(R.id.go_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FilePath.size() > 1) {
                    FilePath.remove(FilePath.size() - 1);
                    showFile(FilePath.remove(FilePath.size() - 1));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil t = new ToastUtil();
                            ToastUtil.Toast(IndexActivity.this, "当前为根目录");
                        }
                    });
                }
            }
        });


        //downloadAdapter = new NativeDownloadAdapter();
        mRecyclerView = findViewById(R.id.download_recyclerview);
        downloaded_recyclerview = findViewById(R.id.downloaded_recyclerview);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DownloadAdapter = new DownloadAdapter();
        mRecyclerView.setAdapter(DownloadAdapter);

        downloaded_recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        downloaded_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DownloadedAdapter = new DownloadedAdapter();
        downloaded_recyclerview.setAdapter(DownloadedAdapter);

        main = findViewById(R.id.main);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) main.getLayoutParams();
        params.topMargin = getStatusBarHeight(this);
        main.setLayoutParams(params);

        bottomView = findViewById(R.id.bottomView);

        LinearLayout.LayoutParams paramss = (LinearLayout.LayoutParams) bottomView.getLayoutParams();
        paramss.bottomMargin = getNavBarHeight(this);
        bottomView.setLayoutParams(paramss);

        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) table.getLayoutParams();
        p.bottomMargin = getNavBarHeight(this) + SizeUtil.dp2px(IndexActivity.this, 45);
        table.setLayoutParams(p);

        script_edit = findViewById(R.id.script_edit);
        edit_script = findViewById(R.id.edit_script);
        edit_name=findViewById(R.id.edit_name);
        script_save = findViewById(R.id.script_save);
        script_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Objects.equals(editType, "script")) {
                    if (isNewScript) {
                        saveScript(script_edit.getText().toString(), "http://ddonging.online");

                    } else {
                        saveScript(script_edit.getText().toString(), downloadScriptUrl);
                    }
                } else if (Objects.equals(editType, "html")) {
                    Home_Dialog(script_edit.getText().toString());
                }else if (Objects.equals(editType, "edithtml"))
                {
                       try{
                           String content=script_edit.getText().toString();
                           String toresult = Base64.encodeToString(content.getBytes(), Base64.DEFAULT);
                           String sql = "update  Homepage set content=\"" + toresult +"\""+"where title=\""+ edit_name.getText().toString()+"\"";
                           db.execSQL(sql);
                           ToastUtil.Toast(IndexActivity.this,"修改成功");
                           edit_script.setVisibility(View.GONE);
                       }catch (Exception e)
                       {
                           ToastUtil.Toast(IndexActivity.this,"修改失败:"+e);
                       }


                }

            }
        });
        new_script.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editType = "script";
                edit_script.setVisibility(View.VISIBLE);
                isNewScript = true;
                script_edit.setText("// ==UserScript==\n// @name NewScript\n// @namespace http://ddonging.online\n// @version 1.0\n// @description introduce\n// @author world\n// @match http://*/\n// @grant none\n// ==/UserScript==");
            }
        });
        manager = getSupportFragmentManager();
        pager = findViewById(R.id.pager);
        viewlist = findViewById(R.id.view);
        View bookmark = LayoutInflater.from(this).inflate(R.layout.bookmark, null);
        View history = LayoutInflater.from(this).inflate(R.layout.history, null);
        View downloadwebpage = LayoutInflater.from(this).inflate(R.layout.downloadweb, null);
        View collect = LayoutInflater.from(this).inflate(R.layout.collect, null);

        settinglist = bookmark.findViewById(R.id.list);
        OverScrollDecoratorHelper.setUpOverScroll(settinglist);
        settinglist2 = history.findViewById(R.id.list);
        OverScrollDecoratorHelper.setUpOverScroll(settinglist2);
        settinglist3 = downloadwebpage.findViewById(R.id.list);
        OverScrollDecoratorHelper.setUpOverScroll(settinglist3);

        settinglist4 = collect.findViewById(R.id.list);
        OverScrollDecoratorHelper.setUpOverScroll(settinglist4);
        /*
        settinglist4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int in, long l) {
                viewlist.setVisibility(View.GONE);
                TextView is = view.findViewById(R.id.list_url);
                for (int i = WebPageAll.get(countlist.get(count)).size() - 1; i > itempager[countlist.get(count)].getCurrentItem(); i--) {
                    viewPagerAdapter[countlist.get(count)].destroyItem(itempager[countlist.get(count)], i, "");
                    itemlistViewAll.get(countlist.get(count)).get(i).destroy();
                    WebPageAll.get(countlist.get(count)).remove(i);
                    itemlistViewAll.get(countlist.get(count)).remove(i);
                    WebPageTitleAll.get(countlist.get(count)).remove(i);
                    WebPageIconAll.get(countlist.get(count)).remove(i);
                }
                viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                addWebView(is.getText().toString());
            }
        });

         */
        settinglist4.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                ItemLongClickedPopWindow itemLongClickedPopWindow = new ItemLongClickedPopWindow(IndexActivity.this, ItemLongClickedPopWindow.COLLECT_VIEW_POPUPWINDOW, SizeUtil.dp2px(IndexActivity.this, 120), SizeUtil.dp2px(IndexActivity.this, 220), position);
                itemLongClickedPopWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, xDown, yDown);
                itemLongClickedPopWindow.getView(R.id.copy_content).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView is = view.findViewById(R.id.list_title);
                        ClipboardManager cmb = (ClipboardManager) IndexActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(is.getText().toString().trim());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.Toast(IndexActivity.this, "复制成功");
                            }
                        });

                        itemLongClickedPopWindow.dismiss();
                    }
                });
                itemLongClickedPopWindow.getView(R.id.item_open).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         viewlist.setVisibility(View.GONE);
                         TextView is = view.findViewById(R.id.list_url);
                         destory();
                         viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                         addWebView(is.getText().toString());
                        itemLongClickedPopWindow.dismiss();
                    }});
                return false;
            }
        });

        List<View> listView = new ArrayList<View>();
        in_to = findViewById(R.id.in_to_sure);
        settinglist2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (if_exsit) {
                    viewlist.setVisibility(View.GONE);
                    for (int i = WebPageAll.get(countlist.get(count)).size() - 1; i > itempager[countlist.get(count)].getCurrentItem(); i--) {
                        itemlistViewAll.get(countlist.get(count)).get(i).destroy();
                        viewPagerAdapter[countlist.get(count)].destroyItem(itempager[countlist.get(count)], i, "");
                        WebPageAll.get(countlist.get(count)).remove(i);
                        itemlistViewAll.get(countlist.get(count)).remove(i);
                        WebPageTitleAll.get(countlist.get(count)).remove(i);
                        WebPageIconAll.get(countlist.get(count)).remove(i);
                    }
                    viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                    addWebView(query_history_id(position));
                }
            }
        });
        settinglist2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ItemLongClickedPopWindow itemLongClickedPopWindow = new ItemLongClickedPopWindow(IndexActivity.this, ItemLongClickedPopWindow.FAVORITES_ITEM_POPUPWINDOW, SizeUtil.dp2px(IndexActivity.this, 120), SizeUtil.dp2px(IndexActivity.this, 220), position);
                itemLongClickedPopWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, xDown, yDown);
                itemLongClickedPopWindow.getView(R.id.in_table).setVisibility(View.GONE);
                itemLongClickedPopWindow.getView(R.id.history_empty).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        history_empty();
                        query_history();
                        ToastUtil.Toast(IndexActivity.this, "清空成功");
                        itemLongClickedPopWindow.dismiss();
                    }
                });
                itemLongClickedPopWindow.getView(R.id.copy_url).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView is = view.findViewById(R.id.list_url);
                        ClipboardManager cmb = (ClipboardManager) IndexActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(is.getText().toString().trim());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.Toast(IndexActivity.this, "复制成功");
                            }
                        });

                        itemLongClickedPopWindow.dismiss();
                    }
                });
                itemLongClickedPopWindow.getView(R.id.item_longclicked_open).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                        TextView li = view.findViewById(R.id.list_title);
                        TextView is = view.findViewById(R.id.list_url);

                        addWebView(is.getText().toString());
                        viewlist.setVisibility(View.GONE);

                        itemLongClickedPopWindow.dismiss();
                    }
                });
                itemLongClickedPopWindow.getView(R.id.item_longclicked_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                        TextView li = view.findViewById(R.id.list_title);
                        TextView is = view.findViewById(R.id.list_url);
                        delete(position);
                        query_history();
                        itemLongClickedPopWindow.dismiss();
                    }
                });
                return true;
            }
        });
        in_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File f = new File(bookmarkpath + "/" + fromFilePath);
                FileUtil.copyfile(fromFile, f);
                if (fromFile.delete()) {
                    showFile(bookmarkpath);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            in_to_view.setVisibility(View.GONE);
                            ToastUtil.Toast(IndexActivity.this, "移动成功");
                        }
                    });
                }
            }
        });
        in_to_cancel = findViewById(R.id.in_to_cancel);
        in_to_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in_to_view.setVisibility(View.GONE);
            }
        });

        settinglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                TextView li = view.findViewById(R.id.list_title);
                TextView is = view.findViewById(R.id.list_url);
                if (is.getText().toString().equals("文件夹")) {
                    showFile(bookmarkpath + "/" + li.getText().toString());
                } else {
                    for (int i = WebPageAll.get(countlist.get(count)).size() - 1; i > itempager[countlist.get(count)].getCurrentItem(); i--) {
                        itemlistViewAll.get(countlist.get(count)).get(i).destroy();
                        viewPagerAdapter[countlist.get(count)].destroyItem(itempager[countlist.get(count)], i, "");
                        WebPageAll.get(countlist.get(count)).remove(i);
                        itemlistViewAll.get(countlist.get(count)).remove(i);
                        WebPageTitleAll.get(countlist.get(count)).remove(i);
                        WebPageIconAll.get(countlist.get(count)).remove(i);
                    }
                    viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                    addWebView(is.getText().toString());
                    viewlist.setVisibility(View.GONE);
                }
            }
        });

        settinglist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ItemLongClickedPopWindow itemLongClickedPopWindow = new ItemLongClickedPopWindow(IndexActivity.this, ItemLongClickedPopWindow.FAVORITES_ITEM_POPUPWINDOW, SizeUtil.dp2px(IndexActivity.this, 120), SizeUtil.dp2px(IndexActivity.this, 220), position);
                itemLongClickedPopWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, xDown, yDown);
                itemLongClickedPopWindow.getView(R.id.history_empty).setVisibility(View.GONE);
                itemLongClickedPopWindow.getView(R.id.copy_url).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView is = view.findViewById(R.id.list_url);
                        if (is.getText().toString().equals("文件夹")) {
                            itemLongClickedPopWindow.getView(R.id.copy_url).setVisibility(View.GONE);
                        } else {
                            ClipboardManager cmb = (ClipboardManager) IndexActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            cmb.setText(is.getText().toString().trim());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.Toast(IndexActivity.this, "复制成功");
                                }
                            });
                        }
                        itemLongClickedPopWindow.dismiss();
                    }
                });
                itemLongClickedPopWindow.getView(R.id.item_longclicked_open).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                        TextView li = view.findViewById(R.id.list_title);
                        TextView is = view.findViewById(R.id.list_url);
                        if (is.getText().toString().equals("文件夹")) {
                            showFile(bookmarkpath + "/" + li.getText().toString());
                        } else {
                            addWebView(is.getText().toString());
                            viewlist.setVisibility(View.GONE);
                        }
                        itemLongClickedPopWindow.dismiss();
                    }
                });
                itemLongClickedPopWindow.getView(R.id.item_longclicked_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                        TextView li = view.findViewById(R.id.list_title);
                        TextView is = view.findViewById(R.id.list_url);
                        if (is.getText().toString().equals("文件夹")) {

                            File f = new File(bookmarkpath + "/" + li.getText().toString());
                            FileUtil.deleteDirectory(f);
                            showFile(bookmarkpath);
                        } else {
                            File f = new File(bookmarkpath + "/" + li.getText().toString());
                            if (f.delete()) {

                                showFile(bookmarkpath);
                            }
                        }
                        itemLongClickedPopWindow.dismiss();
                    }
                });
                itemLongClickedPopWindow.getView(R.id.in_table).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                        TextView li = view.findViewById(R.id.list_title);
                        TextView is = view.findViewById(R.id.list_url);
                        if (is.getText().toString().equals("文件夹")) {
                            ToastUtil.Toast(IndexActivity.this, "暂不支持收藏夹移动");
                        } else {
                            in_to_view = findViewById(R.id.in_to_view);
                            in_to_view.setVisibility(View.VISIBLE);
                            fromFilePath = li.getText().toString();
                            fromFile = new File(bookmarkpath + "/" + li.getText().toString());

                        }
                        itemLongClickedPopWindow.dismiss();
                    }
                });

                return true;
            }
        });


        settinglist3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView data = view.findViewById(R.id.list_title);
                try {
                    File folder = getExternalFilesDir("DownloadWeb");
                    FileInputStream fs = new FileInputStream(folder.getAbsolutePath() + "/" + data.getText().toString());
                    BufferedReader br = new BufferedReader(new InputStreamReader(fs));
                    Log.w("ttt", "" + folder.getAbsolutePath());
                    String line;
                    StringBuffer result = new StringBuffer();
                    while ((line = br.readLine()) != null) {
                        //将读取到的内容放入结果字符串
                        result.append(line);
                    }
                    Log.w("content", "" + result);
                    mWebView[count].loadDataWithBaseURL("", result.toString(), "multipart/related", "UTF-8", "");
                    viewlist.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        listView.add(bookmark);
        listView.add(history);
        listView.add(collect);
        listView.add(downloadwebpage);


        PagerAdapter = new ViewPagerAdapter(listView);
        pager.setAdapter(PagerAdapter);
        pager.setCanSwipe(false);
        text_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_bookmark.setTextSize(20);
                text_history.setTextSize(14);
                text_collect.setTextSize(14);
                pager.setCurrentItem(0, false);
                downloadweb.setTextSize(14);
                go_back.setVisibility(View.VISIBLE);
                File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                showFile(folder.getAbsolutePath());

            }
        });
        text_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_history.setTextSize(20);
                text_bookmark.setTextSize(14);
                downloadweb.setTextSize(14);
                text_collect.setTextSize(14);
                pager.setCurrentItem(1, false);
                go_back.setVisibility(View.GONE);
                query_history();
            }
        });
        text_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_history.setTextSize(14);
                text_bookmark.setTextSize(14);
                downloadweb.setTextSize(14);
                text_collect.setTextSize(20);
                pager.setCurrentItem(2, false);
                go_back.setVisibility(View.GONE);
                query_collect();
            }
        });
        downloadweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_history.setTextSize(14);
                text_bookmark.setTextSize(14);
                text_collect.setTextSize(14);
                downloadweb.setTextSize(20);
                go_back.setVisibility(View.GONE);
                File folder = IndexActivity.this.getExternalFilesDir("DownloadWeb");
                showDownloadWeb(folder.getAbsolutePath());
                pager.setCurrentItem(3, false);
            }
        });
        // contentContainer = findViewById(R.id.NewWeb);

        web = findViewById(R.id.web);
        settitle = findViewById(R.id.settings_title);


        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ImageView add = findViewById(R.id.add_more);
                switch (url) {
                    case "file:///android_asset/setting.html":
                        settitle.setText("设置");
                        add.setVisibility(View.GONE);
                        break;
                    case "file:///android_asset/general.html":
                        settitle.setText("通用");
                        add.setVisibility(View.GONE);
                        break;
                    case "file:///android_asset/person.html":
                        settitle.setText("个性化");
                        add.setVisibility(View.GONE);
                        break;
                    case "file:///android_asset/Api.html":
                        settitle.setText("接口管理");
                        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                        SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        Cursor cursor = db.query("Api", null, null, null, null, null, "id desc");
                        if (cursor != null && cursor.getCount() > 0) {
                            if_exsit = true;
                            while (cursor.moveToNext()) {
                                get_id = cursor.getInt(0);//得到int型的自增变量
                                String get_title = cursor.getString(1);
                                String get_content = cursor.getString(2);
                                String get_label = cursor.getString(3);
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("title", get_title);
                                map.put("content", get_content);
                                map.put("label", get_label);
                                // map.put("is",)
                                list.add(map);
                            }
                        }
                        JSONArray data = new JSONArray(list);
                        web.post(new Runnable() {
                            @Override
                            public void run() {
                                web.getSettings().setJavaScriptEnabled(true);
                                String renderList = "javascript:renderList('" + data + "')";
                                web.evaluateJavascript(renderList, null);
                            }
                        });
                        break;
                }
            }
        });
        web.addJavascriptInterface(IndexActivity.this, "androidObject");
        config_tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                full.setVisibility(View.GONE);
                WebConfig.setVisibility(View.GONE);
                if (isTool == false) {
                    isTool = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.Toast(IndexActivity.this, "开发者工具已开启");
                        }
                    });
                } else {
                    isTool = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.Toast(IndexActivity.this, "开发者工具已关闭");
                        }
                    });
                }
            }
        });

        config_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                full.setVisibility(View.GONE);
                WebConfig.setVisibility(View.GONE);
                if (itempager[countlist.get(count)].getCurrentItem() == 0) {
                    ToastUtil.Toast(IndexActivity.this, "当前页面不允许操作");
                } else {
                    touch = true;
                    addWebView("file:///android_asset/NetWorkLog.html");
                    itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).post(new Runnable() {
                        @Override
                        public void run() {
                            JSONArray data = new JSONArray(resources);
                            String renderList = "javascript:renderList('" + data + "')";
                            itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).evaluateJavascript(renderList, null);
                        }
                    });
                }
            }
        });
        api.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                top.setBackgroundColor(Color.TRANSPARENT);
                pscode.setVisibility(View.VISIBLE);
                String indexBackground = getSharedPreferences("data", MODE_PRIVATE).getString("indexBackground", "空白背景");
                if (isNight || Objects.equals(indexBackground, "空白背景")) {
                    search.setColorFilter(getResources().getColor(R.color.white));
                    find.setColorFilter(getResources().getColor(R.color.white));
                    api.setColorFilter(getResources().getColor(R.color.white));
                    webtitle.setTextColor(getResources().getColor(R.color.white));
                }
                full.setVisibility(View.GONE);
                WebConfig.setVisibility(View.GONE);
                api.setVisibility(View.GONE);
                apiTitle = webtitle.getText().toString();
                for (int i = WebPageAll.get(countlist.get(count)).size() - 1; i > itempager[countlist.get(count)].getCurrentItem(); i--) {
                    itemlistViewAll.get(countlist.get(count)).get(i).destroy();
                    viewPagerAdapter[countlist.get(count)].destroyItem(itempager[countlist.get(count)], i, "");
                    WebPageAll.get(countlist.get(count)).remove(i);
                    itemlistViewAll.get(countlist.get(count)).remove(i);
                    WebPageTitleAll.get(countlist.get(count)).remove(i);
                    WebPageIconAll.get(countlist.get(count)).remove(i);
                }
                addWebView("file:///android_asset/LoadApi.html");
            }
        });
        home_list.setItemAnimator(new DefaultItemAnimator());
        script_list.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String text) {
                // frameLayout.bringChildToFront(po);
                table.setVisibility(View.GONE);
                ps.setWebProgress(100);
                viewpager.setCurrentItem(position, false);
                count = position;
                viewpager.setOffscreenPageLimit(20);
                listview = itempager[countlist.get(count)].getCurrentItem();
                itempager[countlist.get(count)].setCurrentItem(itempager[countlist.get(count)].getCurrentItem(), false);
                updateStatusColor(itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()));
                webtitle.setText(text);
                full.setVisibility(View.GONE);
            }

        });
        adapter.setOnRemoveClickListener(new MyAdapter.OnRemoveClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                listviewAll.add(listview);
                int Count = list.size();
                if (Count > 1) {
                    ps.setWebProgress(100);
                    if (position == final_count - 1) {
                        count = position;
                        final_count = final_count - 1;
                        table.setVisibility(View.GONE);
                        full.setVisibility(View.GONE);

                        adapter.notifyItemRemoved(position);
                        myAdapter.notifyDataSetChanged();
                        list.remove(position);
                        adapter.notifyItemRangeChanged(position, list.size());
                        List.remove(position);

                        adapter.notifyItemRangeChanged(position, List.size());
                        table_text.setText(String.valueOf(final_count));
                        viewpager.setCurrentItem(position - 1);
                        myAdapter.destroyItem(viewpager, position, "");

                        mWebView[position].removeAllViews();
                        mWebView[position].destroy();
                        if (ListViews.size() > 0 && ListViews != null) {
                            ListViews.remove(ListViews.get(position));

                        }
                        count = count - 1;
                        myAdapter.notifyDataSetChanged();
                        if (position == 0) {
                            RecyclerView.LayoutManager manager = home_list.getLayoutManager();
                            View itemView = manager.getChildAt(1);
                            TextView title = itemView.findViewById(R.id.list_item);
                            webtitle.setText(title.getText().toString());


                        } else {
                            RecyclerView.LayoutManager manager = home_list.getLayoutManager();
                            View itemView = manager.getChildAt(position - 1);
                            TextView title = itemView.findViewById(R.id.list_item);
                            webtitle.setText(title.getText().toString());

                        }
                    } else {
                        count = final_count;
                        final_count = final_count - 1;
                        table.setVisibility(View.GONE);
                        full.setVisibility(View.GONE);
                        adapter.notifyItemRemoved(position);
                        myAdapter.notifyDataSetChanged();
                        list.remove(position);
                        adapter.notifyItemRangeChanged(position, list.size());
                        List.remove(position);
                        adapter.notifyItemRangeChanged(position, List.size());
                        table_text.setText(String.valueOf(final_count));
                        viewpager.setCurrentItem(position - 1);
                        myAdapter.destroyItem(viewpager, position, "");
                        mWebView[position].removeAllViews();
                        mWebView[position].destroy();
                        if (ListViews.size() > 0 && ListViews != null) {
                            ListViews.remove(ListViews.get(position));
                            for (int i = 0; i < count - 1; i++) {
                                if (position <= i) {
                                    mWebView[i] = mWebView[i + 1];
                                } else {
                                    mWebView[i] = mWebView[i];
                                }
                            }
                        }
                        count = count - 2;
                        myAdapter.notifyDataSetChanged();
                        if (position == 0) {
                            RecyclerView.LayoutManager manager = home_list.getLayoutManager();
                            View itemView = manager.getChildAt(1);
                            TextView title = itemView.findViewById(R.id.list_item);
                            webtitle.setText(title.getText().toString());


                        } else {
                            RecyclerView.LayoutManager manager = home_list.getLayoutManager();
                            View itemView = manager.getChildAt(position - 1);
                            TextView title = itemView.findViewById(R.id.list_item);
                            webtitle.setText(title.getText().toString());


                        }
                    }
                    if (position == 0) {
                        /*
                        for (int i = WebPageAll.get(countlist.get(0)).size() - 1; i > itempager[countlist.get(0)].getCurrentItem(); i--) {
                            //viewPagerAdapter[countlist.get(0)].destroyItem(itempager[countlist.get(0)], i, "");
                            WebPageAll.get(countlist.get(0)).remove(i);
                            itemlistViewAll.get(countlist.get(0)).remove(i);
                            itemlistViewAll.get(countlist.get(0)).get(i).destroy();
                            WebPageTitleAll.get(countlist.get(0)).remove(i);
                            WebPageIconAll.get(countlist.get(0)).remove(i);
                        }
                        viewPagerAdapter[countlist.get(position)].notifyDataSetChanged();
                        updateStatusColor(itemlistViewAll.get(countlist.get(count)).get(listview));
                         */

                        countlist.remove(0);
                        listviewAll.remove(0);
                        WebPageAll.remove(0);
                        itemlistViewAll.remove(0);
                        count = position;
                        listview = itempager[countlist.get(0)].getCurrentItem();
                        updateStatusColor(itemlistViewAll.get(countlist.get(0)).get(listview));

                    } else {
                        /*
                        for (int i = WebPageAll.get(countlist.get(position)).size() - 1; i > itempager[countlist.get(position)].getCurrentItem(); i--) {
                            //viewPagerAdapter[countlist.get(position)].destroyItem(itempager[countlist.get(position)], i, "");
                            WebPageAll.get(countlist.get(position)).remove(i);
                            itemlistViewAll.get(countlist.get(position)).get(i).destroy();
                            itemlistViewAll.get(countlist.get(position)).remove(i);
                            WebPageTitleAll.get(countlist.get(position)).remove(i);
                            WebPageIconAll.get(countlist.get(position)).remove(i);
                        }
                            updateStatusColor(itemlistViewAll.get(countlist.get(count)).get(listview));
                         */
                        countlist.remove(position);
                        listviewAll.remove(position);
                        WebPageAll.remove(position);
                        itemlistViewAll.remove(position);
                        count = position - 1;
                        listview = itempager[countlist.get(position - 1)].getCurrentItem();
                        try{
                            updateStatusColor(itemlistViewAll.get(countlist.get(position-1)).get(listview));
                        }catch (Exception e)
                        {
                            Log.w("Error updating","error:"+e);
                        }

                    }

                } else {
                    count = count - 1;
                    final_count = final_count - 1;
                    list.remove(position);
                    List.remove(position);
                    adapter.notifyItemRangeChanged(position, List.size());
                    adapter.notifyItemRemoved(position);
                    mWebView[position].removeAllViews();
                    mWebView[position].destroy();
                    myAdapter.destroyItem(viewpager, position, "");
                    if (ListViews.size() > 0 && ListViews != null) {
                        ListViews.remove(ListViews.get(position));
                    }
                    myAdapter.notifyDataSetChanged();
                    table.setVisibility(View.GONE);
                    full.setVisibility(View.GONE);
                    initWeb();
                    viewpager.setCurrentItem(0);
                    table_text.setText(String.valueOf(final_count));
                    listview = 0;
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        goForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goForward();
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            setDarkStatusIcon(true);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Home = findViewById(R.id.Home);
        More = findViewById(R.id.More);
        (find) = findViewById(R.id.find);
        webtitle = findViewById(R.id.title);

        frameLayout = findViewById(R.id.framelayout);
        (add) = findViewById(R.id.add);
        table_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ps.setWebProgress(100);

                table.setVisibility(View.GONE);

                table_text.setText(String.valueOf(final_count + 1));
                adapter.notifyItemInserted(list.size());
                listviewAll.add(listview);
                full.setVisibility(View.GONE);
                if (isStart) {
                    isStart = false;
                    initWeb();
                    viewpager.setCurrentItem(count, false);
                    listview = 0;
                }

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (table.getVisibility() == View.GONE) {
                    full.setVisibility(View.VISIBLE);

                    table.setVisibility(View.VISIBLE);
                    for (int i = 0; i < count + 1; i++) {
                        list.set(i, WebPageTitleAll.get(countlist.get(i)).get(itempager[countlist.get(i)].getCurrentItem()));
                        List.set(i, WebPageIconAll.get(countlist.get(i)).get(itempager[countlist.get(i)].getCurrentItem()));
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    full.setVisibility(View.GONE);
                    table.setVisibility(View.GONE);
                }
            }
        });
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listview = 0;
                itempager[countlist.get(count)].setCurrentItem(0, false);
                webtitle.setText(WebPageTitleAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()));
                itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).onResume();
                viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                ps.setWebProgress(100);
                Util.clearWebViewCache(IndexActivity.this);
            }
        });
        More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog();
            }
        });

        webtitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtils.isEmpty(webtitle.getText().toString())) {
                        ToastUtil t = new ToastUtil();
                        ToastUtil.Toast(IndexActivity.this, "请输入内容");
                        return true;
                    } else {
                        top.setBackgroundColor(Color.TRANSPARENT);
                        pscode.setVisibility(View.VISIBLE);
                        full.setVisibility(View.GONE);
                        String indexBackground = getSharedPreferences("data", MODE_PRIVATE).getString("indexBackground", "空白背景");
                        if (isNight || Objects.equals(indexBackground, "空白背景")) {
                            search.setColorFilter(getResources().getColor(R.color.white));
                            find.setColorFilter(getResources().getColor(R.color.white));
                            api.setColorFilter(getResources().getColor(R.color.white));
                            webtitle.setTextColor(getResources().getColor(R.color.white));
                        }
                        if (webtitle.getText().toString().startsWith("http://") || webtitle.getText().toString().startsWith("https://") || webtitle.getText().toString().startsWith("chrome://")) {
                            View vi = getCurrentFocus();
                            closeKeyboard(vi);
                            mWebView[count].setBackgroundColor(Color.WHITE);
                            api.setVisibility(View.GONE);
                            touch = false;
                            destory();
                            addWebView(webtitle.getText().toString());
                        } else {
                            View vi = getCurrentFocus();
                            closeKeyboard(vi);
                            SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            if (db.isOpen()) {
                                Cursor cursorr = db.rawQuery("SELECT content FROM Engines WHERE title='" + Engines + "'", null);
                                if (cursorr.getCount() > 0) {
                                    while (cursorr.moveToNext()) {
                                        String Engines = cursorr.getString(cursorr.getColumnIndex("content"));
                                       destory();
                                        viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                                        addWebView(Engines + webtitle.getText().toString());
                                        full.setVisibility(View.GONE);
                                        api.setVisibility(View.GONE);
                                    }
                                }
                            }

                        }
                    }

                    return true;
                }
                return false;
            }
        });
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webtitle.getText().toString().equals("")) {
                    ToastUtil.Toast(IndexActivity.this,"请输入内容");
                } else {
                    top.setBackgroundColor(Color.TRANSPARENT);
                    pscode.setVisibility(View.VISIBLE);
                    String indexBackground = getSharedPreferences("data", MODE_PRIVATE).getString("indexBackground", "空白背景");
                    if (isNight || Objects.equals(indexBackground, "空白背景")) {
                        search.setColorFilter(getResources().getColor(R.color.white));
                        find.setColorFilter(getResources().getColor(R.color.white));
                        api.setColorFilter(getResources().getColor(R.color.white));
                        webtitle.setTextColor(getResources().getColor(R.color.white));
                    }
                    if (progress == 100 && !isOn) {
                        itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).reload();
                    } else {
                        full.setVisibility(View.GONE);
                        api.setVisibility(View.GONE);
                        View v = getCurrentFocus();
                        closeKeyboard(v);
                        isOn = false;
                        if (webtitle.getText().toString().startsWith("http://") || webtitle.getText().toString().startsWith("https://") || webtitle.getText().toString().startsWith("chrome://")) {
                            mWebView[count].setBackgroundColor(Color.WHITE);
                            touch = true;
                            destory();
                            addWebView(webtitle.getText().toString());
                        } else if (webtitle.getText().toString().equals("file:///android_asset/index.html")) {
                            mWebView[count].setBackgroundColor(0);
                        } else {
                            mWebView[count].setBackgroundColor(Color.WHITE);
                            touch = true;
                            SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            if (db.isOpen()) {
                                Cursor cursorr = db.rawQuery("SELECT content FROM Engines WHERE title='" + Engines + "'", null);
                                if (cursorr.getCount() > 0) {
                                    while (cursorr.moveToNext()) {
                                        String Engines = cursorr.getString(cursorr.getColumnIndex("content"));
                                        destory();
                                        viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                                        addWebView(Engines + webtitle.getText().toString());
                                    }
                                }
                            }


                        }
                    }
                }
            }
        });

        webtitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                isOn = true;
                if (b) {

                    final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
                    Cursor cursor = db.query("History", null, null, null, null, null, "id desc");
                    if (cursor != null && cursor.getCount() > 0) {
                        if_exsit = true;
                        while (cursor.moveToNext()) {
                            String get_title = cursor.getString(1);
                            String get_url = cursor.getString(2);
                            String get_date = cursor.getString(3);
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("title", get_title);
                            map.put("url", get_url);
                            map.put("date", get_date);
                            listItem.add(map);
                            SimpleAdapter mSimpleAdapter = new SimpleAdapter(IndexActivity.this, listItem, R.layout.list_item, new String[]{"title", "url", "date"}, new int[]{R.id.list_title, R.id.list_url, R.id.list_date});
                            webtitle.setAdapter(mSimpleAdapter);//给文本框绑定数据
                        }
                        webtitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                TextView tv =view.findViewById(R.id.list_url);
                                    top.setBackgroundColor(Color.TRANSPARENT);
                                    pscode.setVisibility(View.VISIBLE);
                                    String indexBackground = getSharedPreferences("data", MODE_PRIVATE).getString("indexBackground", "空白背景");
                                    if (isNight || Objects.equals(indexBackground, "空白背景")) {
                                        search.setColorFilter(getResources().getColor(R.color.white));
                                        find.setColorFilter(getResources().getColor(R.color.white));
                                        api.setColorFilter(getResources().getColor(R.color.white));
                                        webtitle.setTextColor(getResources().getColor(R.color.white));
                                    }
                                        full.setVisibility(View.GONE);
                                        api.setVisibility(View.GONE);
                                        View v = getCurrentFocus();
                                        closeKeyboard(v);
                                        isOn = false;
                                destory();
                                viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                                addWebView(tv.getText().toString());
                            }
                        });

                    }
                    top.setBackgroundColor(Color.WHITE);
                    String indexBackground = getSharedPreferences("data", MODE_PRIVATE).getString("indexBackground", "空白背景");
                    if (isNight || !Objects.equals(indexBackground, "空白背景")) {
                        search.setColorFilter(getResources().getColor(R.color.black));
                        find.setColorFilter(getResources().getColor(R.color.black));
                        api.setColorFilter(getResources().getColor(R.color.black));
                        webtitle.setTextColor(getResources().getColor(R.color.black));
                    }
                    full.setVisibility(View.VISIBLE);
                    find.setImageResource(R.drawable.ic_go);
                    pscode.setVisibility(View.GONE);
                    api.setVisibility(View.VISIBLE);
                    if (listview == 0) {
                        webtitle.setText("");
                    } else {
                        webtitle.setText(itemlistViewAll.get(countlist.get(count)).get(listview).getUrl());
                        webtitle.selectAll();
                    }
                }
            }
        });
        String indexBackground = getSharedPreferences("data", MODE_PRIVATE).getString("indexBackground", "空白背景");
        if (Objects.equals(indexBackground, "空白背景")) {
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    index.setBackground(Drawable.createFromPath(indexBackground));
                }
            });
        }
    }

    //js调用安卓，必须加@JavascriptInterface注释的方法才可以被js调用
    @JavascriptInterface
    public String androidMethod(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Objects.equals(mWebView[count].getUrl(), "file:///android_asset/index.html")) {
                    mWebView[count].setBackgroundColor(0);
                    getWindow().setNavigationBarColor(Color.TRANSPARENT);
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                    bottomView.setBackgroundColor(Color.TRANSPARENT);
                    main.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    mWebView[count].setBackgroundColor(Color.WHITE);
                    getWindow().setNavigationBarColor(Color.WHITE);
                    getWindow().setStatusBarColor(Color.WHITE);
                    bottomView.setBackgroundColor(Color.WHITE);
                    main.setBackgroundColor(Color.WHITE);
                }
                Engines = getSharedPreferences("Engines", MODE_PRIVATE).getString("Engines", "百度");
                SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db.isOpen()) {
                    Cursor cursorr = db.rawQuery("SELECT content FROM Engines WHERE title='" + Engines + "'", null);
                    if (cursorr.getCount() > 0) {
                        while (cursorr.moveToNext()) {
                            @SuppressLint("Range") String Engines = cursorr.getString(cursorr.getColumnIndex("content"));
                            if (WebPageAll.get(countlist.get(count)).size() == 1) {
                                addWebView(Engines + msg);
                            } else {
                                destory();
                                viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                                addWebView(Engines + msg);
                            }
                        }
                    }
                }

            }
        });
        return "我是js调用安卓获取的数据";
    }

    @JavascriptInterface
    public String call_api(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                destory();
                viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                addWebView(msg);
            }
        });
        return "我是js调用安卓获取的数据";
    }

    @JavascriptInterface
    public String call_loadapi(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                itemlistViewAll.get(countlist.get(count)).get(listview).loadUrl(msg + apiTitle);
            }
        });
        return "我是js调用安卓获取的数据";
    }

    @JavascriptInterface
    public String call(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Objects.equals(msg, "通用")) {
                    web.loadUrl("file:///android_asset/general.html");
                } else if (Objects.equals(msg, "关于")) {

                    about_addtg = findViewById(R.id.about_addtg);
                    about_addtg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            about.setVisibility(View.GONE);
                            addWebView("https://t.me/wujingyonghu");
                        }
                    });

                    about_agree = findViewById(R.id.about_agree);
                    about_agree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            about.setVisibility(View.GONE);
                            addWebView("http://ddonging.online/privacy.html");
                        }
                    });
                    about_git = findViewById(R.id.about_git);
                    about_git.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            about.setVisibility(View.GONE);
                            addWebView("http://github.com/1990569689");
                        }
                    });
                    about_back = findViewById(R.id.about_back);
                    about_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            about.setVisibility(View.GONE);
                            addWebView("https://txc.qq.com/products/603607");
                        }
                    });
                    about_donation = findViewById(R.id.about_donation);
                    about_donation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            about.setVisibility(View.GONE);
                            addWebView("http://ddonging.online/donation.html");
                        }
                    });
                    about_qun = findViewById(R.id.about_qun);
                    about_qun.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            about.setVisibility(View.GONE);
                            addWebView("http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=bfPxvTBvY43b8NCU5ozXQ_RzH0kNGpGc&authKey=gd9ZQ776Jhf%2B%2FC8gqE2pVTkKQnX7995rlbuqZe0im1gfBVTdsDhdvQOwRCHjL46l&noverify=0&group_code=136105263");
                        }
                    });
                    about_share = findViewById(R.id.about_share);
                    about_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_SEND);

                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "分享应用");
                            intent.putExtra(Intent.EXTRA_TEXT, "无尽：http://ddonging.online");
                            startActivity(Intent.createChooser(intent, getTitle()));

                        }
                    });
                    about_updata = findViewById(R.id.about_updata);
                    about_updata.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    lastversion = Float.parseFloat(HttpUtil.sendGet("http://ddonging.online/api_version_get.php"));
                                    try {
                                        PackageManager pm = IndexActivity.this.getPackageManager();
                                        PackageInfo pi = null;
                                        pi = pm.getPackageInfo(IndexActivity.this.getPackageName(), 0);
                                        fristversion = Float.parseFloat(pi.versionName);

                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            if (lastversion > fristversion) {
                                about.setVisibility(View.GONE);
                                addWebView("http://ddonging.online");
                            } else {
                                ToastUtil.Toast(IndexActivity.this, "当前为最新版");
                            }
                        }
                    });
                    open = findViewById(R.id.open);
                    open.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            about.setVisibility(View.GONE);
                            addWebView("http://ddonging.online/opencode.html");
                        }
                    });
                    frame.setVisibility(View.GONE);
                    about.setVisibility(View.VISIBLE);
                } else if (Objects.equals(msg, "接口管理")) {

                    web.loadUrl("file:///android_asset/Api.html");
                    ImageView add = findViewById(R.id.add_more);
                    add.setVisibility(View.VISIBLE);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setApi_Dialog();
                        }
                    });


                } else if (Objects.equals(msg, "广告拦截")) {
                    adblock.setVisibility(View.VISIBLE);
                    File folder = IndexActivity.this.getExternalFilesDir("Adblock");
                    showAdblock(folder.getAbsolutePath());
                } else if (Objects.equals(msg, "网页脚本")) {
                    ScriptAdpter = new ScriptAdpter(arrayList);
                    script_list.setAdapter(ScriptAdpter);

                    script.setVisibility(View.VISIBLE);
                    ScriptAdpter.setItemClickListener(new ScriptAdpter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, String name) {
                            ScriptStoreSQLite scriptStore = new ScriptStoreSQLite(IndexActivity.this);
                            scriptStore.open();
                            scripts = scriptStore.getAll();
                            for (Script scriptss : scripts) {
                                if (Objects.equals(scriptss.getName(), name)) {
                                    isNewScript = false;
                                    editType = "script";
                                    edit_script.setVisibility(View.VISIBLE);
                                    script_edit.setText(scriptss.getContent());
                                    downloadScriptUrl = scriptss.getDownloadurl();
                                }
                            }
                        }
                    });
                    ScriptAdpter.setOnItemClickListener(new ScriptAdpter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, String name) {
                            TextView tv = view.findViewById(R.id.user);

                            SQLiteOpenHelper help = MyDatabase.getInstance(IndexActivity.this);
                            SQLiteDatabase db = help.getWritableDatabase();

                            if (tv.getText().toString().equals("●")) {
                                tv.setText("○");
                                if (db.isOpen()) {
                                    String sql = "update UserScript  set user='false' where name='" + name + "'";
                                    db.execSQL(sql);
                                    db.close();
                                }
                            } else {
                                tv.setText("●");
                                if (db.isOpen()) {
                                    String sql = "update UserScript set user='true' where name='" + name + "'";
                                    db.execSQL(sql);
                                    db.close();
                                }
                            }
                        }
                    });
                } else if (Objects.equals(msg, "个性化")) {
                    web.loadUrl("file:///android_asset/person.html");
                }
            }
        });
        return "我是js调用安卓获取的数据";
    }

    private void setApi_Dialog() {

        LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.api_dialog, null);
        // 对话框
        final Dialog dialog = new Dialog(IndexActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth()-150; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);

        TextView sure = view.findViewById(R.id.sure);
        TextView cancel = view.findViewById(R.id.cancel);
        EditText api = view.findViewById(R.id.api);
        EditText title = view.findViewById(R.id.title);
        EditText label = view.findViewById(R.id.label);
        dialog.setContentView(view);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db.isOpen()) {
                    Cursor cursorr = db.rawQuery("SELECT title FROM Api WHERE title='" + title.getText().toString() + "'", null);
                    if (cursorr.getCount() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.Toast(IndexActivity.this, "接口名称已经存在");
                            }
                        });
                    } else {
                        if (api.getText().toString().equals("") || title.getText().toString().equals("") || label.getText().toString().equals("")) {
                            ToastUtil.Toast(IndexActivity.this, "请不要为空");
                            dialog.dismiss();
                        } else {
                            try{
                                String sql = "insert into Api(title,content,label) values(\"" + title.getText().toString() + "\",\"" + api.getText().toString() + "\",\"" + label.getText().toString() + "\")";
                                db.execSQL(sql);
                                ToastUtil.Toast(IndexActivity.this, "添加成功");
                                dialog.dismiss();
                                web.reload();
                            }catch (Exception e)
                            {
                            }
                        }
                    }
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @JavascriptInterface
    public String call_general(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Objects.equals(msg, "搜索引擎")) {
                    more.setVisibility(View.VISIBLE);
                    more_text.setText("搜索引擎");
                    type = "搜索引擎";
                    ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                    SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    //Cursor cursor = db.rawQuery("select * from bookmarkDB", null);
                    //查询语句也可以这样写
                    Cursor cursor = db.query("Engines", null, null, null, null, null, "id desc");
                    SharedPreferences sharedPreferences = getSharedPreferences("Engines", MODE_PRIVATE);
                    String Engines = sharedPreferences.getString("Engines", "百度");
                    if (cursor != null && cursor.getCount() > 0) {
                        if_exsit = true;
                        while (cursor.moveToNext()) {
                            get_id = cursor.getInt(0);//得到int型的自增变量
                            String get_title = cursor.getString(1);
                            String get_content = cursor.getString(2);
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("title", get_title);
                            map.put("Engines", Engines);
                            // map.put("is",)
                            list.add(map);
                            //new String  数据来源， new int 数据到哪去\
                            // moreAdpter=new MoreAdpter(get_title,get_content);
                            moreAdpter = new MoreAdpter(list, "engines");
                            more_list.setAdapter(moreAdpter);
                            moreAdpter.setOnItemClickListener(new MoreAdpter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position, String name) {

                                    TextView tv = view.findViewById(R.id.user);

                                    if (tv.getText().toString().equals("○")) {
                                        tv.setText("●");
                                        SharedPreferences.Editor sharedPreferences = getSharedPreferences("Engines", MODE_PRIVATE).edit();
                                        sharedPreferences.putString("Engines", name);
                                        sharedPreferences.commit();
                                        // recyclerView -  RecyclerView 控件变量名
                                        RecyclerView.LayoutManager manager = more_list.getLayoutManager();
                                        // 方式一  position : 你需要获取的对应View的索引值即 index
                                        for (int i = 0; i < moreAdpter.getItemCount(); i++) {
                                            View itemView = manager.getChildAt(i);
                                            TextView t = itemView.findViewById(R.id.user);
                                            if (position == i) {
                                                continue;
                                            } else {
                                                t.setText("○");
                                            }
                                        }
                                    }

                                }
                            });

                        }
                    }


                } else if (Objects.equals(msg, "浏览器标识")) {
                    type = "浏览器标识";
                    more_text.setText("浏览器标识");
                    more.setVisibility(View.VISIBLE);
                    ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                    SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor cursor = db.query("UserAgent", null, null, null, null, null, "id desc");
                    SharedPreferences sharedPreferences = getSharedPreferences("UserAgent", MODE_PRIVATE);
                    String ua = sharedPreferences.getString("UserAgent", "手机");
                    if (cursor != null && cursor.getCount() > 0) {
                        if_exsit = true;
                        while (cursor.moveToNext()) {
                            get_id = cursor.getInt(0);//得到int型的自增变量
                            String get_title = cursor.getString(1);
                            String get_content = cursor.getString(2);
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("title", get_title);
                            map.put("UserAgent", ua);
                            list.add(map);
                            moreAdpter = new MoreAdpter(list, "useragent");
                            more_list.setAdapter(moreAdpter);
                            moreAdpter.setOnItemClickListener(new MoreAdpter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position, String name) {

                                    TextView tv = view.findViewById(R.id.user);

                                    if (tv.getText().toString().equals("○")) {
                                        tv.setText("●");
                                        SharedPreferences.Editor sharedPreferences = getSharedPreferences("UserAgent", MODE_PRIVATE).edit();
                                        sharedPreferences.putString("UserAgent", name);
                                        sharedPreferences.commit();
                                        // recyclerView -  RecyclerView 控件变量名
                                        RecyclerView.LayoutManager manager = more_list.getLayoutManager();
                                        // 方式一  position : 你需要获取的对应View的索引值即 index
                                        for (int i = 0; i < moreAdpter.getItemCount(); i++) {
                                            View itemView = manager.getChildAt(i);
                                            TextView t = itemView.findViewById(R.id.user);
                                            if (position == i) {
                                                continue;
                                            } else {
                                                t.setText("○");
                                            }
                                        }
                                    }
                                }
                            });


                        }
                    }
                } else if (Objects.equals(msg, "主页")) {
                    type = "主页";
                    more_text.setText("主页");
                    more.setVisibility(View.VISIBLE);
                    ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                    SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    //Cursor cursor = db.rawQuery("select * from bookmarkDB", null);
                    //查询语句也可以这样写
                    Cursor cursor = db.query("Homepage", null, null, null, null, null, "id desc");
                    SharedPreferences sharedPreferences = getSharedPreferences("Homepage", MODE_PRIVATE);
                    String home = sharedPreferences.getString("Homepage", "本地主页：主页");
                    if (cursor != null && cursor.getCount() > 0) {
                        if_exsit = true;
                        while (cursor.moveToNext()) {
                            get_id = cursor.getInt(0);//得到int型的自增变量
                            String get_title = cursor.getString(1);
                            String get_content = cursor.getString(2);
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("title", get_title);
                            map.put("Homepage", home);
                            // map.put("is",)
                            list.add(map);

                            moreAdpter = new MoreAdpter(list, "homepage");
                            more_list.setAdapter(moreAdpter);
                            moreAdpter.setOnItemClickListener(new MoreAdpter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position, String name) {
                                    TextView tv = view.findViewById(R.id.user);
                                    if (tv.getText().toString().equals("○")) {
                                        tv.setText("●");
                                        SharedPreferences.Editor sharedPreferences = getSharedPreferences("Homepage", MODE_PRIVATE).edit();
                                        sharedPreferences.putString("Homepage", name);
                                        sharedPreferences.commit();

                                        RecyclerView.LayoutManager manager = more_list.getLayoutManager();

                                        for (int i = 0; i < moreAdpter.getItemCount(); i++) {
                                            View itemView = manager.getChildAt(i);
                                            TextView t = itemView.findViewById(R.id.user);
                                            if (position == i) {
                                                continue;
                                            } else {
                                                t.setText("○");
                                            }
                                        }

                                    }
                                }
                            });
                            moreAdpter.setOnLongClickListener(new MoreAdpter.OnItemLongClickListener() {
                                @Override
                                public void onItemClick(View view, int position, String name) {
                                   String content=query_homepage_name(name);
                                   More_Menu(type,name,content);


                                }
                            });



                        }
                    }
                } else if (Objects.equals(msg, "大小设置")) {

                    size_seekbar = findViewById(R.id.size_seekbar);
                    size_progress = findViewById(R.id.size_progress);
                    int WebFontSize = getSharedPreferences("WebFontSize", MODE_PRIVATE).getInt("WebFontSize", 20);
                    size_progress.setText("字体大小：" + WebFontSize);
                    WebView websize = findViewById(R.id.size_web);
                    websize.loadData("落霞与孤鹜齐飞，秋水共长天一色", "text/html", "UTF-8");
                    websize.getSettings().setDefaultFontSize(WebFontSize);
                    websize.getSettings().setMinimumFontSize(10);
                    size_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    size_progress.setText("字体大小：" + i);
                                    boolean editor = getSharedPreferences("WebFontSize", MODE_PRIVATE).edit().putInt("WebFontSize", i).commit();
                                }
                            });
                            websize.loadData("落霞与孤鹜齐飞，秋水共长天一色", "text/html", "UTF-8");
                            websize.getSettings().setDefaultFontSize(i);
                            websize.getSettings().setMinimumFontSize(i);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });

                    // setSize_Dialog();
                    SizeSettings.setVisibility(View.VISIBLE);
                } else if (Objects.equals(msg, "下载目录")) {
                    setPath_Dialog();
                } else if (Objects.equals(msg, "下载方式")) {
                    Download_Menu();
                } else if (Objects.equals(msg, "网站设定")) {
                    setting_img = findViewById(R.id.setting_img);
                    setting_apk = findViewById(R.id.setting_apk);
                    setting_cookies = findViewById(R.id.setting_cookies);
                    setting_javascript = findViewById(R.id.setting_javascript);
                    setting_camera = findViewById(R.id.setting_camera);
                    setting_mic = findViewById(R.id.setting_mic);
                    setting_position = findViewById(R.id.setting_position);
                    setting_img_text = findViewById(R.id.setting_img_text);
                    setting_apk_text = findViewById(R.id.setting_apk_text);
                    setting_cookies_text = findViewById(R.id.setting_cookies_text);
                    setting_javascript_text = findViewById(R.id.setting_javascript_text);
                    setting_camera_text = findViewById(R.id.setting_camera_text);
                    setting_mic_text = findViewById(R.id.setting_mic_text);
                    setting_position_text = findViewById(R.id.setting_position_text);
                    setting_apk_text.setText(Webapk);
                    if (Webmic == true) {
                        setting_mic_text.setText("允许");
                    } else {
                        setting_mic_text.setText("禁止");
                    }
                    if (Webimg == true) {
                        setting_img_text.setText("允许");
                    } else {
                        setting_img_text.setText("禁止");
                    }
                    if (Webposition == true) {
                        setting_position_text.setText("允许");
                    } else {
                        setting_position_text.setText("禁止");
                    }
                    if (Webjavascript == true) {
                        setting_javascript_text.setText("允许");
                    } else {
                        setting_javascript_text.setText("禁止");
                    }
                    if (Webcamera == true) {
                        setting_camera_text.setText("允许");
                    } else {
                        setting_camera_text.setText("禁止");
                    }

                    setting_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (setting_img_text.getText().toString().equals("允许")) {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_img", false).commit();
                                Webimg = false;
                                if (editor) {
                                    setting_img_text.setText("禁止");
                                }
                            } else {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_img", true).commit();

                                Webimg = true;
                                if (editor) {
                                    setting_img_text.setText("允许");
                                }
                            }

                        }
                    });
                    setting_apk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (setting_apk_text.getText().toString().equals("允许")) {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putString("setting_apk", "询问").commit();
                                //调用apply（）进行提交
                                Webapk = "询问";
                                if (editor) {
                                    setting_apk_text.setText("询问");
                                }
                            } else if (setting_apk_text.getText().toString().equals("询问")) {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putString("setting_apk", "禁止").commit();
                                //调用apply（）进行提交
                                Webapk = "禁止";
                                if (editor) {
                                    setting_apk_text.setText("禁止");
                                }
                            } else {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putString("setting_apk", "允许").commit();
                                //调用apply（）进行提交
                                Webapk = "允许";
                                if (editor) {
                                    setting_apk_text.setText("允许");
                                }
                            }

                        }
                    });
                    setting_cookies.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (setting_cookies_text.getText().toString().equals("允许")) {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_cookies", false).commit();
                                //调用apply（）进行提交
                                if (editor) {
                                    setting_cookies_text.setText("禁止");
                                }
                            } else {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_cookies", true).commit();
                                //调用apply（）进行提交
                                if (editor) {
                                    setting_cookies_text.setText("允许");
                                }
                            }

                        }
                    });
                    setting_javascript.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (setting_javascript_text.getText().toString().equals("允许")) {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_javascript", false).commit();
                                //调用apply（）进行提交
                                Webjavascript = false;
                                if (editor) {
                                    setting_javascript_text.setText("禁止");
                                }
                            } else {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_javascript", true).commit();
                                //调用apply（）进行提交
                                Webjavascript = true;
                                if (editor) {
                                    setting_javascript_text.setText("允许");
                                }
                            }

                        }
                    });
                    setting_camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (setting_camera_text.getText().toString().equals("允许")) {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_camera", false).commit();
                                //调用apply（）进行提交
                                Webcamera = false;
                                if (editor) {
                                    setting_camera_text.setText("禁止");
                                }
                            } else {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_camera", true).commit();
                                //调用apply（）进行提交
                                Webcamera = true;
                                IndexActivity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
                                if (editor) {
                                    setting_camera_text.setText("允许");
                                }
                            }

                        }
                    });
                    setting_mic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (setting_mic_text.getText().toString().equals("允许")) {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_mic", false).commit();
                                //调用apply（）进行提交
                                Webmic = false;
                                if (editor) {
                                    setting_mic_text.setText("禁止");
                                }
                            } else {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_mic", true).commit();
                                //调用apply（）进行提交
                                Webmic = true;
                                IndexActivity.this.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 11);
                                if (editor) {
                                    setting_mic_text.setText("允许");
                                }
                            }

                        }
                    });
                    setting_position.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (setting_position_text.getText().toString().equals("允许")) {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_position", false).commit();
                                //调用apply（）进行提交
                                Webposition = false;
                                if (editor) {
                                    setting_position_text.setText("禁止");
                                }
                            } else {
                                boolean editor = getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("setting_position", true).commit();
                                //调用apply（）进行提交
                                Webposition = true;
                                if (editor) {
                                    setting_position_text.setText("允许");
                                }
                            }

                        }
                    });
                    webSettings.setVisibility(View.VISIBLE);

                } else if (Objects.equals(msg, "导入书签数据")) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/html");
                    startActivityForResult(intent, 10086);
                } else if (Objects.equals(msg, "设置默认浏览器")) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
                    startActivity(intent);
                }
            }
        });
        return "我是js调用安卓获取的数据";
    }

    @JavascriptInterface
    public String call_person(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Objects.equals(msg, "设置背景壁纸")) {
                    String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(IndexActivity.this, mPermissionList, 100);
                } else if (Objects.equals(msg, "设置顶部Log")) {
                    DIYDialog("设置顶部Log", 1);
                } else if (Objects.equals(msg, "设置顶部文字")) {
                    DIYDialog("设置顶部文字", 2);
                } else if (Objects.equals(msg, "自定义顶部Css")) {
                    DIYDialog("自定义顶部Css", 3);
                }

            }
        });
        return "我是js调用安卓获取的数据";
    }

    @JavascriptInterface
    public String call_copy(String text, String title) {
        ToastUtil.Toast(IndexActivity.this, "" + text + title);
        return "我是js调用安卓获取的数据";
    }

    /**
     * 后退
     */
    private void goBack() {
        if (itemlistViewAll.get(countlist.get(count)).size() > 1 && itempager[countlist.get(count)].getCurrentItem() != 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).stopLoading();
                    itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).onPause();
                    ps.setWebProgress(100);
                    itempager[countlist.get(count)].setCurrentItem(itempager[countlist.get(count)].getCurrentItem() - 1, false);
                    try{
                        updateStatusColor(itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()));
                    }catch (Exception e)
                    {
                        Log.w("Error updating","error:"+e);
                    }

                    webtitle.setText(WebPageTitleAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()));

            if (itempager[countlist.get(count)].getCurrentItem() > 0) {
                itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).onResume();
                listview = listview - 1;
            } else {
                itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).onResume();
                listview = 0;
                viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                ps.setWebProgress(100);
                Util.clearWebViewCache(IndexActivity.this);
            }
                }});
        } else if (viewpager.getCurrentItem() > 0) {
            listviewAll.add(listview);
            int position = viewpager.getCurrentItem();
            ps.setWebProgress(100);
            if (position == final_count - 1) {
                final_count = final_count - 1;
                adapter.notifyItemRemoved(position);
                myAdapter.notifyDataSetChanged();
                list.remove(position);
                adapter.notifyItemRangeChanged(position, list.size());
                List.remove(position);
                adapter.notifyItemRangeChanged(position, List.size());
                table_text.setText(String.valueOf(final_count));
                viewpager.setCurrentItem(position - 1);
                myAdapter.destroyItem(viewpager, position, "");
                mWebView[position].removeAllViews();
                mWebView[position].destroy();
                if (ListViews.size() > 0 && ListViews != null) {
                    ListViews.remove(ListViews.get(position));
                }
                count = count - 1;
                myAdapter.notifyDataSetChanged();
            } else {
                final_count = final_count - 1;
                table.setVisibility(View.GONE);
                full.setVisibility(View.GONE);
                adapter.notifyItemRemoved(position);
                myAdapter.notifyDataSetChanged();
                list.remove(position);
                adapter.notifyItemRangeChanged(position, list.size());
                List.remove(position);
                adapter.notifyItemRangeChanged(position, List.size());
                table_text.setText(String.valueOf(final_count));
                viewpager.setCurrentItem(position - 1);
                myAdapter.destroyItem(viewpager, position, "");
                mWebView[position].removeAllViews();
                mWebView[position].destroy();
                if (ListViews.size() > 0 && ListViews != null) {
                    ListViews.remove(ListViews.get(position));
                    for (int i = 0; i < count - 1; i++) {
                        if (position <= i) {
                            mWebView[i] = mWebView[i + 1];
                        } else {
                            mWebView[i] = mWebView[i];
                        }
                    }
                }
                count = count - 2;
                myAdapter.notifyDataSetChanged();
            }
               /*
                for (int i = WebPageAll.get(countlist.get(position)).size() - 1; i > itempager[countlist.get(position)].getCurrentItem(); i--) {
                    itemlistViewAll.get(countlist.get(count)).get(i).destroy();
                    viewPagerAdapter[countlist.get(position)].destroyItem(itempager[countlist.get(position)], i, "");
                    WebPageAll.get(countlist.get(position)).remove(i);
                    itemlistViewAll.get(countlist.get(position)).remove(i);
                    WebPageTitleAll.get(countlist.get(position)).remove(i);
                    WebPageIconAll.get(countlist.get(position)).remove(i);
                }
                */
            countlist.remove(position);
            listviewAll.remove(position);
            WebPageAll.remove(position);
            itemlistViewAll.remove(position);
            count = position - 1;
            listview = itempager[countlist.get(position - 1)].getCurrentItem();
            String title = WebPageTitleAll.get(countlist.get(position - 1)).get(listview);
            try{
                updateStatusColor(itemlistViewAll.get(countlist.get(count)).get(listview));
            }catch (Exception e)
            {
                Log.w("Error updating","error:"+e);
            }

            webtitle.setText(title);
        }
    }

    /**
     * 前进
     */
    private void goForward() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).onPause();
                itempager[countlist.get(count)].setCurrentItem(itempager[countlist.get(count)].getCurrentItem() + 1, false);
                webtitle.setText(WebPageTitleAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()));
                try{
                    updateStatusColor(itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()));
                }catch (Exception e)
                {
                    Log.w("Error updating","error:"+e);
                }

            }
        });
        itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).onResume();
        listview = itempager[countlist.get(count)].getCurrentItem();
    }

    private void Dialog() {
        ScriptStoreSQLite scriptStore = new ScriptStoreSQLite(IndexActivity.this);
        scriptStore.open();
        //addWeb("http://baidu.com");
        this.scriptStore = scriptStore;
        scripts = scriptStore.getAll();
        arrayList = new ArrayList<HashMap<String, Object>>();
        int i = 0;
        for (Script scriptss : scripts) {
            HashMap<String, Object> js = new HashMap<String, Object>();
            i++;
            js.put("Name", scriptss.getName());
            js.put("Description", scriptss.getDescription());
            js.put("Namespace", scriptss.getNamespace());
            SQLiteOpenHelper help = MyDatabase.getInstance(IndexActivity.this);
            SQLiteDatabase db = help.getWritableDatabase();
            if (db.isOpen()) {
                String sql = "select * from UserScript where name='" + scriptss.getName() + "'";
                Cursor cursor = db.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    js.put("UserScript", cursor.getString(cursor.getColumnIndex("user")));
                }
                db.close();
            }
            js.put("Downloadurl", scriptss.getDownloadurl());
            arrayList.add(js);

        }

        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.MyBottomSheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.index_dialog, null);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animStyle);
        (person) = view.findViewById(R.id.person);
        (night) = view.findViewById(R.id.night);
        (source) = view.findViewById(R.id.source);
        (javascript) = view.findViewById(R.id.JavaScript);
        (set) = view.findViewById(R.id.settings);
        (add_bookmark) = view.findViewById(R.id.add_bookmark);
        (bookmark) = view.findViewById(R.id.bookmark);
        (history) = view.findViewById(R.id.history);
        (downloads) = view.findViewById(R.id.downloads);
        (resource) = view.findViewById(R.id.resource);
        (useragent) = view.findViewById(R.id.useragent);
        (save_web) = view.findViewById(R.id.save_web);
        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itempager[countlist.get(count)].getCurrentItem() == 0) {
                    ToastUtil.Toast(IndexActivity.this, "当前页面不允许操作");
                } else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, itemlistViewAll.get(countlist.get(count)).get(listview).getUrl());
                }

            }
        });
        save_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itempager[countlist.get(count)].getCurrentItem() > 0) {
                    File folder = IndexActivity.this.getExternalFilesDir("DownloadWeb");

                    itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).saveWebArchive(folder.getAbsolutePath() + "/" + webtitle.getText().toString() + ".mht");
                    ToastUtil.Toast(IndexActivity.this, "保存成功");
                } else {
                    ToastUtil.Toast(IndexActivity.this, "当前页面不允许操作");
                }
            }
        });

        useragent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                type = "浏览器标识";
                more.setVisibility(View.VISIBLE);
                ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                SQLiteOpenHelper dbHelper = new Database(IndexActivity.this, "Database", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //Cursor cursor = db.rawQuery("select * from bookmarkDB", null);
                //查询语句也可以这样写
                Cursor cursor = db.query("UserAgent", null, null, null, null, null, "id desc");
                SharedPreferences sharedPreferences = getSharedPreferences("UserAgent", MODE_PRIVATE);
                String ua = sharedPreferences.getString("UserAgent", "手机");
                if (cursor != null && cursor.getCount() > 0) {
                    if_exsit = true;
                    while (cursor.moveToNext()) {
                        get_id = cursor.getInt(0);//得到int型的自增变量
                        String get_title = cursor.getString(1);
                        String get_content = cursor.getString(2);
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("title", get_title);
                        map.put("UserAgent", ua);
                        // map.put("is",)
                        list.add(map);
                        moreAdpter = new MoreAdpter(list, "useragent");
                        more_list.setAdapter(moreAdpter);
                        moreAdpter.setOnItemClickListener(new MoreAdpter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position, String name) {
                                TextView tv = view.findViewById(R.id.user);
                                if (tv.getText().toString().equals("○")) {
                                    tv.setText("●");
                                    SharedPreferences.Editor sharedPreferences = getSharedPreferences("UserAgent", MODE_PRIVATE).edit();
                                    sharedPreferences.putString("UserAgent", name);
                                    sharedPreferences.commit();
                                    // recyclerView -  RecyclerView 控件变量名
                                    RecyclerView.LayoutManager manager = more_list.getLayoutManager();
                                    // 方式一  position : 你需要获取的对应View的索引值即 index
                                    for (int i = 0; i < moreAdpter.getItemCount(); i++) {
                                        View itemView = manager.getChildAt(i);
                                        TextView t = itemView.findViewById(R.id.user);
                                        if (position == i) {
                                            continue;
                                        } else {
                                            t.setText("○");
                                        }
                                    }
                                }
                            }
                        });


                    }
                }
            }
        });
        resource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listview > 0) {
                    String js = "javascript:function inspect(){var videos=document.getElementsByTagName('video');var array_video=new Array();for(var i=0;i<videos.length;i++){array_video.push(videos[i].src);}var array_iframe=new Array();var iframes=document.getElementsByTagName('iframe');for(var i=0;i<iframes.length;i++){array_iframe.push(iframes[i].src);}var array_img=new Array();var imgs=document.getElementsByTagName('img');for(var i=0;i<imgs.length;i++){array_img.push(imgs[i].src);}return{array_video,array_iframe,array_img}}";
                    itemlistViewAll.get(countlist.get(count)).get(listview).loadUrl(js);
                    itemlistViewAll.get(countlist.get(count)).get(listview).evaluateJavascript("javascript:inspect()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            String html = "<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'/><title>资源探查</title><style>h1{text-align:center;}a{text-decoration:none;}img{max-width:100%;}</style></head><body><h1>视频</h1><div id='div_video'></div><h1>图片</h1><div id='div_img'></div></body><script>var div_video=document.getElementById('div_video');var json=eval((" + value + "));var videos=json.array_video;for(var i=0; i<videos.length; i++){var video=document.createElement('video');video.src=videos[i];video.controls='controls';div_video.append(video);}var iframes=json.array_iframe;for(var i=0; i<iframes.length; i++){var p = document.createElement('p');var a=document.createElement('a');a.textContent=iframes[i];a.href=iframes[i];a.target='_blank';p.append(a);div_iframe.append(p);}var imgs=json.array_img;for(var i=0; i<imgs.length; i++){var img=document.createElement('img');img.src=imgs[i];div_img.append(img);}</script></html>";
                            itemlistViewAll.get(countlist.get(count)).get(listview).loadData(html, "text/html; charset=UTF-8", null);
                        }
                    });
                } else {
                }
            }
        });
        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadsList.setVisibility(View.VISIBLE);
                dialog.cancel();
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query_history();
                dialog.cancel();
                go_back.setVisibility(View.GONE);
                viewlist.setVisibility(View.VISIBLE);
                text_bookmark.setTextSize(14);
                text_history.setTextSize(20);
                downloadweb.setTextSize(14);
                pager.setCurrentItem(1, false);
            }
        });
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewlist.setVisibility(View.VISIBLE);
                text_bookmark.setTextSize(20);
                text_history.setTextSize(14);
                downloadweb.setTextSize(14);
                text_collect.setTextSize(14);
                go_back.setVisibility(View.VISIBLE);
                File folder = IndexActivity.this.getExternalFilesDir("bookmark");
                showFile(folder.getAbsolutePath());
                dialog.cancel();
                pager.setCurrentItem(0, false);
            }
        });
        add_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itempager[countlist.get(count)].getCurrentItem() == 0) {
                    ToastUtil.Toast(IndexActivity.this, "当前页面不允许操作");
                    dialog.cancel();
                } else {
                    Addbookmark_Dialog(webtitle.getText().toString(), itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).getUrl());
                    dialog.cancel();
                }

            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame.setVisibility(View.VISIBLE);
                web.loadUrl("file:///android_asset/setting.html");
                dialog.cancel();
            }
        });
        javascript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                String is = getSharedPreferences("data", MODE_PRIVATE).getString("trace", "关闭");
                if (Objects.equals(is, "关闭")) {
                    Boolean get = getSharedPreferences("data", MODE_PRIVATE).edit().putString("trace", "开启").commit();
                    if (get) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.Toast(IndexActivity.this, "无痕浏览已开启");
                            }
                        });
                    }
                } else {
                    Boolean get = getSharedPreferences("data", MODE_PRIVATE).edit().putString("trace", "关闭").commit();
                    if (get) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.Toast(IndexActivity.this, "无痕浏览已关闭");
                            }
                        });
                    }
                }

            }

        });
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (itempager[countlist.get(count)].getCurrentItem() == 0) {
                    ToastUtil.Toast(IndexActivity.this, "当前页面不允许操作");
                    dialog.cancel();
                } else {
                    touch = true;
                    String url = "view-source:" + itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).getUrl();
                    addWebView(url);
                }
            }
        });
        night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (isNight == false) {
                    isNight = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.Toast(IndexActivity.this, "夜间模式已开启");
                            mWebView[count].reload();
                        }
                    });
                } else {
                    isNight = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.Toast(IndexActivity.this, "夜间模式已关闭");
                            mWebView[count].reload();
                        }
                    });
                }

            }
        });
        person.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (itempager[countlist.get(count)].getCurrentItem() == 0) {
                    ToastUtil.Toast(IndexActivity.this, "当前页面不允许操作");
                } else {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    intent.putExtra(Intent.EXTRA_TEXT, itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).getUrl());
                    startActivity(Intent.createChooser(intent, getTitle()));
                }

            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (downloaded.getVisibility() == View.VISIBLE) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downloaded.setVisibility(View.GONE);
                }
            });
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (downloadsList.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadsList.setVisibility(View.GONE);
                    }
                });
                return true;
            } else if (edit_script.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        edit_script.setVisibility(View.GONE);
                    }
                });
                return true;
            } else if (script.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        script.setVisibility(View.GONE);
                    }
                });
                return true;
            } else if (about.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        frame.setVisibility(View.VISIBLE);
                        about.setVisibility(View.GONE);
                    }
                });
                return true;
            } else if (SizeSettings.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SizeSettings.setVisibility(View.GONE);
                    }
                });
                return true;
            } else if (view_image.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view_image.setVisibility(View.GONE);
                    }
                });
                return true;
            } else if (webSettings.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webSettings.setVisibility(View.GONE);
                    }
                });
                return true;
            } else if (QR.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        QR.setVisibility(View.GONE);

                    }
                });
                return true;
            } else if (more.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        more.setVisibility(View.GONE);
                    }
                });
                return true;
            } else if (adblock.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adblock.setVisibility(View.GONE);
                    }
                });
                return true;
            } else if (frame.getVisibility() == View.VISIBLE) {
                if (web.canGoBack()) {
                    web.goBack();//返回上一页面
                    return true;
                } else {
                    frame.setVisibility(View.GONE);
                }
                return true;
            } else if (viewlist.getVisibility() == View.VISIBLE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewlist.setVisibility(View.GONE);
                    }
                });
                return true;
            } else if (WebPageTitleAll.get(countlist.get(count)).size() > 1 && itempager[countlist.get(count)].getCurrentItem() != 0) {
                webtitle.clearFocus();
                api.setVisibility(View.GONE);
                full.setVisibility(View.GONE);
                pscode.setVisibility(View.VISIBLE);
                top.setBackgroundColor(Color.TRANSPARENT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).stopLoading();
                        itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).onPause();
                        ps.setWebProgress(100);
                        itempager[countlist.get(count)].setCurrentItem(itempager[countlist.get(count)].getCurrentItem() - 1, false);
                        try{
                            updateStatusColor(itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()));
                        }catch (Exception e)
                        {
                            Log.w("Error updating","error:"+e);
                        }
                        webtitle.setText(WebPageTitleAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()));

                if (itempager[countlist.get(count)].getCurrentItem() > 0) {
                    itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).onResume();
                    listview = listview - 1;
                } else {
                    itemlistViewAll.get(countlist.get(count)).get(itempager[countlist.get(count)].getCurrentItem()).onResume();
                    listview = 0;
                    viewPagerAdapter[countlist.get(count)].notifyDataSetChanged();
                    ps.setWebProgress(100);
                    Util.clearWebViewCache(IndexActivity.this);
                }
                    }   });

                return true;
            } else if (viewpager.getCurrentItem() > 0) {
                listviewAll.add(listview);
                int position = viewpager.getCurrentItem();
                ps.setWebProgress(100);
                if (position == final_count - 1) {
                    final_count = final_count - 1;
                    adapter.notifyItemRemoved(position);
                    myAdapter.notifyDataSetChanged();
                    list.remove(position);
                    adapter.notifyItemRangeChanged(position, list.size());
                    List.remove(position);
                    adapter.notifyItemRangeChanged(position, List.size());
                    table_text.setText(String.valueOf(final_count));
                    viewpager.setCurrentItem(position - 1);
                    myAdapter.destroyItem(viewpager, position, "");
                    mWebView[position].removeAllViews();
                    mWebView[position].destroy();
                    if (ListViews.size() > 0 && ListViews != null) {
                        ListViews.remove(ListViews.get(position));

                    }
                    count = count - 1;
                    myAdapter.notifyDataSetChanged();
                } else {
                    final_count = final_count - 1;
                    table.setVisibility(View.GONE);
                    full.setVisibility(View.GONE);
                    adapter.notifyItemRemoved(position);
                    myAdapter.notifyDataSetChanged();
                    list.remove(position);
                    adapter.notifyItemRangeChanged(position, list.size());
                    List.remove(position);
                    adapter.notifyItemRangeChanged(position, List.size());
                    table_text.setText(String.valueOf(final_count));
                    viewpager.setCurrentItem(position - 1);
                    myAdapter.destroyItem(viewpager, position, "");
                    mWebView[position].removeAllViews();
                    mWebView[position].destroy();
                    if (ListViews.size() > 0 && ListViews != null) {
                        ListViews.remove(ListViews.get(position));
                        for (int i = 0; i < count - 1; i++) {
                            if (position <= i) {
                                mWebView[i] = mWebView[i + 1];
                            } else {
                                mWebView[i] = mWebView[i];
                            }
                        }
                    }
                    count = count - 2;
                    myAdapter.notifyDataSetChanged();
                }
               /*
                for (int i = WebPageAll.get(countlist.get(position)).size() - 1; i > itempager[countlist.get(position)].getCurrentItem(); i--) {
                    itemlistViewAll.get(countlist.get(count)).get(i).destroy();
                    viewPagerAdapter[countlist.get(position)].destroyItem(itempager[countlist.get(position)], i, "");
                    WebPageAll.get(countlist.get(position)).remove(i);
                    itemlistViewAll.get(countlist.get(position)).remove(i);
                    WebPageTitleAll.get(countlist.get(position)).remove(i);
                    WebPageIconAll.get(countlist.get(position)).remove(i);
                }
                */
                countlist.remove(position);
                listviewAll.remove(position);
                WebPageAll.remove(position);
                itemlistViewAll.remove(position);
                count = position - 1;
                listview = itempager[countlist.get(position - 1)].getCurrentItem();
                String title = WebPageTitleAll.get(countlist.get(position - 1)).get(listview);
                try{
                    updateStatusColor(itemlistViewAll.get(countlist.get(count)).get(listview));
                }catch (Exception e)
                {
                    Log.w("Error updating","error:"+e);
                }
                webtitle.setText(title);
                return true;
            } else {
                // 点击两次退出变量
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!isExit) {
                        isExit = true;
                        ToastUtil.Toast(IndexActivity.this, "再按一次退出程序");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    isExit = false;
                                    //Util.clearWebViewCache(IndexActivity.this);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }).start();

                    } else {
                        finish();
                        Util.clearWebViewCache(IndexActivity.this);
                    }
                    return false;
                } else {
                    return super.onKeyDown(keyCode, event);
                }


            }
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 判断视图v是否应该隐藏输入软键盘，若v不是输入框，返回false
     *
     * @param v     视图
     * @param event 屏幕事件
     * @return 视图v是否应该隐藏输入软键盘，若v不是输入框，返回false
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth() + 500;
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                //点击editText控件外部
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    //软键盘工具类关闭软键盘
                    closeKeyboard(v);
                    //使输入框失去焦点
                    api.setVisibility(View.GONE);
                    full.setVisibility(View.GONE);
                    pscode.setVisibility(View.VISIBLE);
                    top.setBackgroundColor(Color.TRANSPARENT);

                    String indexBackground = getSharedPreferences("data", MODE_PRIVATE).getString("indexBackground", "空白背景");
                    if (itempager[countlist.get(count)].getCurrentItem() == 0) {
                        initView();
                    }
                    if (itempager[countlist.get(count)].getCurrentItem() == 0) {
                        webtitle.setText("主页");

                    } else {
                        webtitle.setText(WebPageTitleAll.get(countlist.get(count)).get(listview));
                    }
                    webtitle.clearFocus();
                    find.setImageResource(R.drawable.ic_refresh);
                    isOn = false;
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public void start(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String intentUri = intent.toUri(Intent.URI_INTENT_SCHEME);
        startActivity(intent);
    }

    protected void setDarkStatusIcon(boolean isDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (isDark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }

    class MyPagerAdapter extends PagerAdapter {
        private int mChildCount = 0;

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(ListViews.get(position));

        }

        @Override
        public void finishUpdate(View arg0) {
            Log.d("k", "finishUpdate");
        }

        @Override
        public int getCount() {
            Log.d("k", "getCount");
            return ListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            Log.d("k", "instantiateItem");
            /*
            ViewGroup viewGroup = (ViewGroup) ListViews.get(arg1).getParent();
            if (viewGroup != null) {
                viewGroup.removeView( ListViews.get(arg1));
            }

             */
            ((ViewPager) arg0).addView(ListViews.get(arg1), 0);
            return ListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            Log.d("k", "isViewFromObject");
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            Log.d("k", "restoreState");
        }

        @Override
        public Parcelable saveState() {
            Log.d("k", "saveState");
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            Log.d("k", "startUpdate");
        }


    }


    private class DownloadAdapter extends RecyclerView.Adapter<DownloadViewHolder>   {

        private final LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);

        @Override
        public DownloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.download_item, parent, false);
            return new DownloadViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(DownloadViewHolder holder, int position) {
            DownloadTask task = tasks.get(position);
            holder.setKey(task.key);
            task.setListener(holder);
            holder.title.setText(task.name);
            holder.downloads_progress.setVisibility(View.GONE);
            String extension = fileManager.getExtension(task.name);
            if (fileManager.isApk(extension)) {
                holder.icon.setImageResource(R.drawable.downlaod_apk);
            } else if (fileManager.isMusic(extension)) {
                holder.icon.setImageResource(R.drawable.download_mp3);
            } else if (fileManager.isVideo(extension)) {
                holder.icon.setImageResource(R.drawable.download_mp4);
            } else if (fileManager.isZip(extension) || fileManager.isRar(extension)) {
                holder.icon.setImageResource(R.drawable.download_zip);
            } else if (fileManager.isTxt(extension)) {
                holder.icon.setImageResource(R.drawable.download_txt);
            } else if (fileManager.isTable(extension)) {
                holder.icon.setImageResource(R.drawable.download_table);
            } else if (fileManager.isImg(extension)) {
                holder.icon.setImageResource(R.drawable.download_img);
            } else {
                holder.icon.setImageResource(R.drawable.download_other);
            }
            if (task.size == 0) {
                holder.size.setText(R.string.download_unknown);
            } else {
                holder.size.setText(String.format(Locale.US, "%.1fMB", task.size / 1048576.0f));
            }
            // Glide.with(MainActivity.this).load(task.extras).into(holder.icon);
        }

        @Override
        public int getItemCount() {
            return tasks == null ? 0 : tasks.size();
        }
    }

    private class DownloadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, com.ddonging.wenba.download.DownloadListener {

        int state;
        String key;
        ImageView icon, download_close;
        TextView title;
        TextView size, downloads_progress;
        Button download;
        CardView download_ing;

        private DownloadViewHolder(View itemView) {
            super(itemView);
            downloads_progress = itemView.findViewById(R.id.downloads_progress);
            icon = itemView.findViewById(R.id.icon_iv);
            title = itemView.findViewById(R.id.downloads_title);
            size = itemView.findViewById(R.id.downloads_size);
            download = itemView.findViewById(R.id.download_button);
            download_close = itemView.findViewById(R.id.download_close);
            download_ing = itemView.findViewById(R.id.download_ing);
            download_ing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(state==STATE_FINISHED)
                    {
                        final int position = getAdapterPosition();
                        if (view == itemView) {
                            if (isEditMode) {
                                Downadapter.toggle(position);
                            } else {
                                DownloadInfo info = downloadss.get(position);
                                // 注意这个是8.0新API需要判断
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    Boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                                    if (!haveInstallPermission) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            Uri packageURI = Uri.parse("package:" + IndexActivity.this.getPackageName());
                                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                                            startActivityForResult(intent, 666);
                                        }
                                    }
                                }
                                fileManager.open(info.name, info.path);
                            }
                        }
                    }
                }
            });
            download.setOnClickListener(this);

        }

        void setKey(String key) {
            this.key = key;
        }


        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            final DownloadTask task = tasks.get(position);
            switch (state) {
                case STATE_FAILED:
                case STATE_PREPARED:
                    task.start();
                    break;
                case STATE_PAUSED:
                    task.resume();
                    break;
                case STATE_WAITING:
                    task.resume();
                    break;
                case STATE_RUNNING:
                    task.pause();
                    break;

            }
        }

        @Override
        public void onStateChanged(String key, int state) {
            if (!key.equals(this.key)) return;
            this.state = state;
            switch (state) {
                case STATE_FAILED:
                    download.setText(R.string.download_retry);
                    break;
                case STATE_PREPARED:
                    download.setText(R.string.label_download);
                    break;
                case STATE_PAUSED:
                    download.setText(R.string.download_resume);
                    break;
                case STATE_WAITING:
                    download.setText(R.string.download_wait);
                    break;
                case STATE_FINISHED:
                    download.setText(R.string.download_finish);
                    if (tasks == null) return;
                    int position = getAdapterPosition();
                    if (position < 0) return;
                    DownloadTask task = tasks.get(position);
                    task.clear();
                    tasks.remove(task);

                    ToastUtil.Toast(IndexActivity.this,"下载完成");
                    IndexActivity.DownloadedAdapter.notifyDataSetChanged();
                    //IndexActivity.DownloadAdapter.notifyItemRemoved(position);
                    break;
            }
        }

        @Override
        public void onProgressChanged(String key, long finishedLength, long contentLength) {
            if (!key.equals(this.key)) return;
            download.setText(String.format(Locale.US, "%.1f%%", finishedLength * 100.f / Math.max(contentLength, 1)));
            if (contentLength == 0) {
                size.setText(R.string.download_unknown);
            } else {
                size.setText(String.format(Locale.US, "%.1fMB", contentLength / 1048576.0f));
            }
        }

    }


    private class DownloadedAdapter extends RecyclerView.Adapter<DownloadedViewHolder> {

        private final LayoutInflater inflater = LayoutInflater.from(IndexActivity.this);
        private final SimpleDateFormat format = new SimpleDateFormat(getResources().getString(R.string.download_date_format), Locale.CHINA);
        private List<Boolean> checks;

        @Override
        public DownloadedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.download_item, parent, false);
            return new DownloadedViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(DownloadedViewHolder holder, int position) {
            /*
            if (isEditMode) {
                if (holder.check.getVisibility() != View.VISIBLE)
                    holder.check.setVisibility(View.VISIBLE);
                if (holder.close.getVisibility() == View.VISIBLE) {
                    holder.close.setVisibility(View.GONE);
                }
                holder.check.setChecked(checks.get(position));
            } else {
                if (holder.check.getVisibility() == View.VISIBLE)
                    holder.check.setVisibility(View.GONE);
                if (holder.close.getVisibility() != View.VISIBLE) {
                    holder.close.setVisibility(View.VISIBLE);
                }
            }
             */
            DownloadInfo info = downloadss.get(position);
            holder.name.setText(info.name);
            holder.timestamp.setText(format.format(new Date(info.createTime)));
            holder.download.setText("打开");
            holder.size.setText(String.format(Locale.US, "%.1fMB", info.contentLength / 1048576.0f));
            String extension = fileManager.getExtension(info.name);
            if (fileManager.isApk(extension)) {
                holder.icon.setImageResource(R.drawable.downlaod_apk);
            } else if (fileManager.isMusic(extension)) {
                holder.icon.setImageResource(R.drawable.download_mp3);
            } else if (fileManager.isVideo(extension)) {
                holder.icon.setImageResource(R.drawable.download_mp4);
            } else if (fileManager.isZip(extension) || fileManager.isRar(extension)) {
                holder.icon.setImageResource(R.drawable.download_zip);
            } else if (fileManager.isTxt(extension)) {
                holder.icon.setImageResource(R.drawable.download_txt);
            } else if (fileManager.isTable(extension)) {
                holder.icon.setImageResource(R.drawable.download_table);
            } else if (fileManager.isImg(extension)) {
                holder.icon.setImageResource(R.drawable.download_img);
            } else {
                holder.icon.setImageResource(R.drawable.download_other);
            }
        }

        private void updateMenu() {
            if (isAllSelected()) {
                //  select.setText(R.string.label_select_none);
            } else {
                //  select.setText(R.string.label_select_all);
            }
        }

        private boolean isAllSelected() {
            for (Boolean bool : checks) {
                if (!bool) return false;
            }
            return true;
        }

        private void select() {
            boolean next = !isAllSelected();
            Collections.fill(checks, next);
            if (next) {
                // select.setText(R.string.label_select_none);
            } else {
                //  select.setText(R.string.label_select_all);
            }
            notifyDataSetChanged();
        }

        private void enterEditMode() {
            int count = getItemCount();
            checks = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                checks.add(false);
            }
            // select.setText(R.string.label_select_all);
            notifyDataSetChanged();
        }

        private void exitEditMode() {
            checks.clear();
            checks = null;
            notifyDataSetChanged();
        }

        private void toggle(int position) {
            if (position < 0 || position >= getItemCount()) return;
            checks.set(position, !checks.get(position));
            notifyItemChanged(position);
            updateMenu();
        }
        /*
        private List<DownloadInfo> getSelections() {
            if (!isEditMode) return new ArrayList<>();
            List<DownloadInfo> result = new ArrayList<>();
            for (int i = 0, count = getItemCount(); i < count; i++) {
                if (checks.get(i)) result.add(downloads.get(i));
            }
            return result;
        }

         */

        @Override
        public int getItemCount() {
            return downloadss == null ? 0 : downloadss.size();
        }
    }

    private class DownloadedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        CheckBox check;
        ImageView icon;
        TextView name;
        TextView size;
        TextView timestamp;
        View close;
        Button download;

        DownloadedViewHolder(View itemView) {
            super(itemView);
            //check = (CheckBox) itemView.findViewById(R.id.download_checkbox);
            icon = itemView.findViewById(R.id.icon_iv);
            name = itemView.findViewById(R.id.downloads_title);
            timestamp = itemView.findViewById(R.id.downloads_progress);
            size = itemView.findViewById(R.id.downloads_size);
            close = itemView.findViewById(R.id.download_close);
            download=itemView.findViewById(R.id.download_button);
            size.setVisibility(View.GONE);
            close.setVisibility(View.GONE);
            download.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            if (v == itemView) {
                if (isEditMode) {
                    Downadapter.toggle(position);
                } else {

                }
            } else if (R.id.download_button == v.getId()) {
                DownloadInfo info = downloadss.get(position);
                // 注意这个是8.0新API需要判断
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                    if (!haveInstallPermission) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Uri packageURI = Uri.parse("package:" + IndexActivity.this.getPackageName());
                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                            startActivityForResult(intent, 666);
                        }
                    }

                }
                fileManager.open(info.name, info.path);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            final int position = getAdapterPosition();
            DownloadInfo info = downloadss.get(position);
            ItemLongClickedPopWindow itemLongClickedPopWindow = new ItemLongClickedPopWindow(IndexActivity.this, ItemLongClickedPopWindow.FAVORITES_ITEM_POPUPWINDOW, SizeUtil.dp2px(IndexActivity.this, 120), SizeUtil.dp2px(IndexActivity.this, 220));
            itemLongClickedPopWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, xDown, yDown);
            itemLongClickedPopWindow.getView(R.id.in_table).setVisibility(View.GONE);
            itemLongClickedPopWindow.getView(R.id.history_empty).setVisibility(View.GONE);
            itemLongClickedPopWindow.getView(R.id.copy_url).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager cmb = (ClipboardManager) IndexActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(info.url.trim());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.Toast(IndexActivity.this, "复制成功");
                        }
                    });
                    itemLongClickedPopWindow.dismiss();
                }
            });
            itemLongClickedPopWindow.getView(R.id.item_longclicked_open).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 注意这个是8.0新API需要判断
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                        if (!haveInstallPermission) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Uri packageURI = Uri.parse("package:" + IndexActivity.this.getPackageName());
                                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                                startActivityForResult(intent, 666);
                            }
                        }

                    }
                    itemLongClickedPopWindow.dismiss();
                    fileManager.open(info.name, info.path);
                }
            });
            itemLongClickedPopWindow.getView(R.id.item_longclicked_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadss.remove(info);
                    File file = new File(info.path);
                    if (file.exists()) {
                        file.delete();
                        DownloadProvider d=new DownloadProvider(IndexActivity.this);
                        d.delete(info);
                    }else
                    {
                        DownloadProvider d=new DownloadProvider(IndexActivity.this);
                        d.delete(info);
                    }

                    IndexActivity.DownloadedAdapter.notifyItemRemoved(position);
                    itemLongClickedPopWindow.dismiss();
                }
            });
            return false;
        }
    }
}


class ScriptBrowserWebViewClientGm extends WebViewClientGm {

    private final IndexActivity indexActivity;
    String Overrideurl;
    boolean if_load, loading;
    String result;
    int count;
    public ScriptBrowserWebViewClientGm(ScriptStore scriptStore, String jsBridgeName, String secret, IndexActivity indexActivity) {
        super(scriptStore, jsBridgeName, secret);
        this.indexActivity = indexActivity;
    }
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        WebResourceResponse response = null;

        if(request.getUrl().toString().endsWith(".png")){
            try {
                final PipedOutputStream out = new PipedOutputStream();
                PipedInputStream in = new PipedInputStream(out);
                //out.write(HttpUtil.sendGet(request.getUrl().toString()));
                response = new WebResourceResponse("image/png", "UTF-8", in);
                Log.w("mmm",""+in);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            String mUrl = request.getUrl().toString();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("url", mUrl);
            if (mUrl.endsWith(".js")) {
                params.put("type", "JavaScript文件");
            } else if (mUrl.endsWith(".mp4")) {
                params.put("type", "视频文件");
            } else if (mUrl.endsWith(".png")) {
                params.put("type", "图片文件");
            } else if (mUrl.endsWith(".jpg")) {
                params.put("type", "图片文件");
            } else if (mUrl.endsWith(".jpeg")) {
                params.put("type", "图片文件");
            } else if (mUrl.endsWith(".gif")) {
                params.put("type", "图片文件");
            } else if (mUrl.endsWith(".mp3")) {
                params.put("type", "音频文件");
            } else if (mUrl.endsWith(".css")) {
                params.put("type", "CSS文件");
            } else {
                params.put("type", "其他文件");
            }
            IndexActivity.resources.add(params);

        } catch (Exception e) {
            return super.shouldInterceptRequest(view, request);
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        if (request.getUrl().toString().startsWith("http://") || request.getUrl().toString().startsWith("https://")) {

            if (IndexActivity.touch) {
                if (Objects.equals(indexActivity.WebPageAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview), request.getUrl().toString())) {
                } else {
                    if (indexActivity.WebPageAll.get(IndexActivity.countlist.get(IndexActivity.count)).size() - 1 == IndexActivity.itempager[IndexActivity.countlist.get(IndexActivity.count)].getCurrentItem()) {
                        indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).stopLoading();
                        indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).onPause();
                        indexActivity.addWebView(request.getUrl().toString());
                        IndexActivity.touch = false;

                    } else {
                        for (int i = indexActivity.WebPageAll.get(IndexActivity.countlist.get(IndexActivity.count)).size() - 1; i > IndexActivity.itempager[IndexActivity.countlist.get(IndexActivity.count)].getCurrentItem(); i--) {
                            indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(i).destroy();
                            indexActivity.viewPagerAdapter[IndexActivity.countlist.get(IndexActivity.count)].destroyItem(IndexActivity.itempager[IndexActivity.countlist.get(IndexActivity.count)], i, "");
                            indexActivity.WebPageAll.get(IndexActivity.countlist.get(IndexActivity.count)).remove(i);
                            indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).remove(i);
                            indexActivity.WebPageTitleAll.get(IndexActivity.countlist.get(IndexActivity.count)).remove(i);
                            indexActivity.WebPageIconAll.get(IndexActivity.countlist.get(IndexActivity.count)).remove(i);
                        }
                        indexActivity.viewPagerAdapter[IndexActivity.countlist.get(IndexActivity.count)].notifyDataSetChanged();
                        indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).stopLoading();
                        indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).onPause();
                        indexActivity.addWebView(request.getUrl().toString());
                        IndexActivity.touch = false;
                    }
                }
            }
            return false;
        } else {
            if (Objects.equals(indexActivity.Webapk, "询问")) {
                indexActivity.toast_view.setVisibility(View.VISIBLE);
                indexActivity.toast_content.setText("网站请求打开其他应用？");
                RelativeLayout.LayoutParams paramsss = (RelativeLayout.LayoutParams) indexActivity.toast_view.getLayoutParams();
                paramsss.bottomMargin = IndexActivity.getNavBarHeight(indexActivity) + SizeUtil.dp2px(indexActivity, 45);
                indexActivity.toast_view.setLayoutParams(paramsss);
                indexActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indexActivity.toast_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString()));
                                    intent.addCategory("android.intent.category.BROWSABLE");
                                    intent.setComponent(null);
                                    indexActivity.startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastUtil.Toast(indexActivity, "未安装");
                                    indexActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            indexActivity.toast_view.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                indexActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indexActivity.toast_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                indexActivity.toast_view.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        while (System.currentTimeMillis() - startTime < 3000) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                indexActivity.toast_view.setVisibility(View.GONE);
                            }
                        }
                        indexActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                indexActivity.toast_view.setVisibility(View.GONE);
                            }
                        });

                    }
                }).start();
            } else if (Objects.equals(indexActivity.Webapk, "允许")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString()));
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    indexActivity.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.Toast(indexActivity, "未安装");
                    indexActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            indexActivity.toast_view.setVisibility(View.GONE);
                        }
                    });
                }
            } else {
            }
            return true;
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if_load = true;
        indexActivity.checkDownload(url);
        if (indexActivity.isNight) {
            indexActivity.mWebView[IndexActivity.count].post(new Runnable() {
                @Override
                public void run() {
                    String night = "(function () { class ChangeBackground { constructor() { this.init(); }; init() { this.addStyle(`html, body {background-color: #272626 !important;}*{color: #CCD1D9 !important;box-shadow: none !important;}*:after, *:before {border-color: #CCD1D9 !important;color: #CCD1D9 !important;box-shadow: none !important;background-color: transparent !important;}a, a > *{color: #ffffff !important;}[data-change-border-color][data-change-border-color-important] {border-color: #1e1e1e !important;}[data-change-background-color][data-change-background-color-important] {background-color: #272626 !important;}`); this.selectAllNodes(node => { if (node.nodeType !== 1) { return; }; const style = window.getComputedStyle(node, null); const whiteList = ['rgba(0, 0, 0, 0)', 'transparent']; const backgroundColor = style.getPropertyValue('background-color'); const borderColor = style.getPropertyValue('border-color'); if (whiteList.indexOf(backgroundColor) < 0) { if (this.isWhiteToBlack(backgroundColor)) { node.dataset.changeBackgroundColor = ''; node.dataset.changeBackgroundColorImportant = ''; } else { delete node.dataset.changeBackgroundColor; delete node.dataset.changeBackgroundColorImportant; }; }; if (whiteList.indexOf(borderColor) < 0) { if (this.isWhiteToBlack(borderColor)) { node.dataset.changeBorderColor = ''; node.dataset.changeBorderColorImportant = ''; } else { delete node.dataset.changeBorderColor; delete node.dataset.changeBorderColorImportant; }; }; if (borderColor.indexOf('rgb(255, 255, 255)') >= 0) { delete node.dataset.changeBorderColor; delete node.dataset.changeBorderColorImportant; node.style.borderColor = 'transparent'; }; }); }; addStyle(style = '') { const styleElm = document.createElement('style'); styleElm.innerHTML = style; document.head.appendChild(styleElm); }; isWhiteToBlack(colorStr = '') { let hasWhiteToBlack = false; const colorArr = colorStr.match(/rgb.+?\\)/g); if (!colorArr || colorArr.length === 0) { return true; }; colorArr.forEach(color => { const reg = /rgb[a]*?\\(([0-9]+),.*?([0-9]+),.*?([0-9]+).*?\\)/g; const result = reg.exec(color); const red = result[1]; const green = result[2]; const blue = result[3]; const deviation = 20; const max = Math.max(red, green, blue); const min = Math.min(red, green, blue); if (max - min <= deviation) { hasWhiteToBlack = true; }; }); return hasWhiteToBlack; }; selectAllNodes(callback = () => { }) { const allNodes = document.querySelectorAll('*'); Array.from(allNodes, node => { callback(node); }); this.observe({ targetNode: document.documentElement, config: { attributes: false }, callback(mutations, observer) { const allNodes = document.querySelectorAll('*'); Array.from(allNodes, node => { callback(node); }); } }); }; observe({ targetNode, config = {}, callback = () => { } }) { if (!targetNode) { return; }; config = Object.assign({ attributes: true, childList: true, subtree: true }, config); const observer = new MutationObserver(callback); observer.observe(targetNode, config); }; }; new ChangeBackground(); })();\n";
                    if (IndexActivity.itempager[IndexActivity.countlist.get(IndexActivity.count)].getCurrentItem() > 0) {
                        indexActivity.itemlistViewAll.get(IndexActivity.count).get(indexActivity.listview).evaluateJavascript(night, null);
                    }
                    indexActivity.mWebView[IndexActivity.count].evaluateJavascript(night, null);
                }
            });
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String is = indexActivity.getSharedPreferences("data", MODE_PRIVATE).getString("trace", "关闭");
        if (if_load) {
            if (url.startsWith("file://")) {

            } else {
                if (Objects.equals(is, "开启")) {
                } else {
                    if(Objects.equals(view.copyBackForwardList().getCurrentItem().getTitle(), "") ||view.copyBackForwardList().getCurrentItem().getTitle()==null)
                    {
                        indexActivity.add_history("网页无法打开",view.copyBackForwardList().getCurrentItem().getUrl());
                    }else
                    {
                        indexActivity.add_history(view.copyBackForwardList().getCurrentItem().getTitle(),view.copyBackForwardList().getCurrentItem().getUrl());
                    }

                    if_load = false;
                }
            }
        }
        if (Objects.equals(url, "file:///android_asset/index.html")) {

        } else {
            try{  indexActivity.updateStatusColor(view);}catch (Exception e){}
        }

        if (indexActivity.isTool) {
            indexActivity.itemlistViewAll.get(IndexActivity.count).get(indexActivity.listview).post(new Runnable() {
                @Override
                public void run() {
                    String tool = "javascript:(function () { var script = document.createElement('script'); script.src=\"//cdn.jsdelivr.net/npm/eruda\"; document.body.appendChild(script); script.onload = function () { eruda.init() } })();";
                    if (IndexActivity.itempager[IndexActivity.countlist.get(IndexActivity.count)].getCurrentItem() > 0) {
                        indexActivity.itemlistViewAll.get(IndexActivity.count).get(indexActivity.listview).evaluateJavascript(tool, null);
                    }

                }
            });
        }

        count = 0;

        CookieManager cookieManager = CookieManager.getInstance();
        String CookieStr = cookieManager.getCookie(url);
        IndexActivity.Cookie = CookieStr;
        ArrayList<HashMap<String, Object>> lista = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> listb = new ArrayList<HashMap<String, Object>>();
        SQLiteOpenHelper dbHelper = new Database(indexActivity, "Database", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Api", null, null, null, null, null, "id desc");
        if (cursor != null && cursor.getCount() > 0) {
            boolean if_exsit = true;
            while (cursor.moveToNext()) {
                String get_title = cursor.getString(1);
                String get_content = cursor.getString(2);
                String get_label = cursor.getString(3);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("title", get_title);
                map.put("content", get_content);
                map.put("label", get_label);
                lista.add(map);
            }

        }
        Cursor cursorr = db.query("Collect", null, null, null, null, null, "id desc");
        if (cursorr != null && cursorr.getCount() > 0) {
            boolean if_exsit = true;
            while (cursorr.moveToNext()) {
                String get_title = cursorr.getString(1);
                String get_content = cursorr.getString(2);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("title", get_title);
                map.put("content", get_content);
                listb.add(map);
            }

        }

        indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).post(new Runnable() {
            @Override
            public void run() {
                JSONArray datab = new JSONArray(listb);
                String List = "javascript:CollectList('" + datab + "')";
                indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).evaluateJavascript(List, null);
            }
        });
        indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).post(new Runnable() {
            @Override
            public void run() {
                JSONArray dataa = new JSONArray(lista);
                String renderList = "javascript:renderList('" + dataa + "')";
                indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).evaluateJavascript(renderList, null);

            }
        });
        SharedPreferences sharedPreferences = indexActivity.getSharedPreferences("DIY", MODE_PRIVATE);
        int i = sharedPreferences.getInt("title", 0);
        String content = sharedPreferences.getString("content", "无");
        if (i == 1) {
            indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).post(new Runnable() {
                @Override
                public void run() {
                    String renderList = "javascript:setTop('Logo','" + content + "')";
                    indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).evaluateJavascript(renderList, null);

                }
            });
        } else if (i == 2) {
            indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).post(new Runnable() {
                @Override
                public void run() {
                    String renderList = "javascript:setTop('自定义Css','" + content + "')";
                    indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).evaluateJavascript(renderList, null);

                }
            });
        } else if (i == 3) {
            indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).post(new Runnable() {
                @Override
                public void run() {
                    String renderList = "javascript:setTop('文字','" + content + "')";
                    indexActivity.itemlistViewAll.get(IndexActivity.countlist.get(IndexActivity.count)).get(indexActivity.listview).evaluateJavascript(renderList, null);

                }
            });
        } else {

        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }

}

class ScriptBrowserDownloadListener implements DownloadListener {
    private final IndexActivity indexActivity;

    public ScriptBrowserDownloadListener(IndexActivity indexActivity) {
        this.indexActivity = indexActivity;
    }

    @Override
    public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        indexActivity.Download(url, contentDisposition, mimetype);
    }
}