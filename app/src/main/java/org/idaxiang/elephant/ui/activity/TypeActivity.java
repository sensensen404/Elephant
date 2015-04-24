package org.idaxiang.elephant.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.idaxiang.elephant.R;
import org.idaxiang.elephant.api.home.ReadingApi;
import org.idaxiang.elephant.model.ReadingModel;
import org.idaxiang.elephant.support.Constants;
import org.idaxiang.elephant.support.Settings;
import org.idaxiang.elephant.support.lib.MyAsyncTask;
import org.idaxiang.elephant.support.utils.Util;
import org.idaxiang.elephant.task.ImageDownloadTask;
import org.idaxiang.elephant.utils.AppLogger;
import org.idaxiang.elephant.utils.AssetsUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import taobe.tec.jcc.JChineseConvertor;

public class TypeActivity extends ToolbarActivity {

    public static String TAG = "ReadingActivity";
    WebView mWebView;
    List<ReadingModel> results;
    ReadingModel readingModel;
    String page_id;
    String title;

    private boolean isNeedConvertTraditionalChinese = false;
    private static JChineseConvertor chineseConvertor = null;

    private boolean isNeedKeepScreenOn = false;
    private boolean isNeedReplaceSymbol = false;
    private boolean isAutoPic = false;

    private static final int FIVE_MINUTES = 1000 * 60 * 5;

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayout = R.layout.activity_type;
        super.onCreate(savedInstanceState);

        try {
            chineseConvertor = JChineseConvertor.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.isNeedReplaceSymbol = Settings.getInstance(this).getBoolean(Settings.TRADITIONAL_SYMBOL, false);
        this.isNeedConvertTraditionalChinese =
                Settings.getInstance(this).getBoolean(getString(R.string.key_traditional_chinese), false);
        this.isNeedKeepScreenOn = Settings.getInstance(this).getBoolean(Settings.SCREEN_ON, true);
        this.isAutoPic = Settings.getInstance(this).getBoolean(Settings.AUTO_PIC, true);

        if (isNeedKeepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        page_id = "158";
        title = "为什么没有女码农？";

        mWebView = (WebView) findViewById(R.id.wv_reading);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        setUpWebViewDefaults(mWebView);

        mWebView.setVisibility(View.INVISIBLE);

        List<ReadingModel> readingModels = DataSupport.where("nid=?", page_id).find(ReadingModel.class);
        if (readingModels != null && readingModels.size() > 0) {
            readingModel = readingModels.get(0);
            setWebView();

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33FF5722")));
            getSupportActionBar().setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#33FF5722")));
        } else {
            new Refresher().execute(page_id);
        }
    }


    private void setUpWebViewDefaults(WebView mWebView) {

        mWebView.addJavascriptInterface(new JavaScriptObject(this), "injectedObject");

        // 设置缓存模式
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setJavaScriptEnabled(true);


        // Use WideViewport and Zoom out if there is no viewport defined
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(mWebViewClient);

        // 支持通过js打开新的窗口
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                result.cancel();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {

                return true;
            }
        });

        //设置webView背景
        Resources.Theme theme = this.getTheme();
        TypedArray typedArray = null;

        SharedPreferences mPerferences = PreferenceManager
                .getDefaultSharedPreferences(this);

//        if (mPerferences.getBoolean("dark_theme?", false)) {
//            typedArray = theme.obtainStyledAttributes(R.style.Theme_Daily_AppTheme_Dark,
//                    new int[] { R.attr.webViewBackground });
//        } else {
//            typedArray = theme.obtainStyledAttributes(R.style.Theme_Daily_AppTheme_Light,
//                    new int[] { R.attr.webViewBackground });
//        }
//
//        mWebView.setBackgroundColor(this.getResources().getColor(typedArray.getResourceId(0, 0)));

        if (Build.VERSION.SDK_INT >= 11) {
            mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private String generateClassName() {
        String className = "";
        int fontSize = Integer.parseInt(Settings.getInstance(this).getString(Settings.FONT_SIZE, "12"));
        AppLogger.e("fontSize " + fontSize);
        boolean isNeedIndent = Settings.getInstance(this).getBoolean(Settings.INDENT, false);
        switch (fontSize) {
            case 12:
                className += " tiny";
                break;
            case 14:
                className += " small";
                break;
            case 16:
                className += " normal";
                break;
            case 18:
                className += " big";
                break;
            case 20:
                className += " huge";
                break;
            default:
                className += " normal";
        }

        if (isNeedIndent) {
            className += " indent";
        }

        return className;
    }

    /**
     * 设置WebView内容
     */
    private void setWebView() {

        mWebView.setVisibility(View.VISIBLE);
        String headerDef = "file:///android_asset/www/news_detail_header_def.jpg";
        String html = AssetsUtils.loadText(this, Constants.TEMPLATE_DEF_URL);
        html = html.replace("{Content}", readingModel.getBody());

        title = (isNeedReplaceSymbol) ? Util.replaceSymbol(title) : title;

        AppLogger.e("head img " + readingModel.getMainimage());
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"img-wrap\">")
                .append("<h1 class=\"headline-title\">")
                .append(title).append("</h1>")
                .append("<span class=\"img-source\">")
                .append(readingModel.getUsername()).append("</span>")
                .append("<img src=\"").append(readingModel.getMainimage())
                .append("\" alt=\"\">")
                .append("<div class=\"img-mask\"></div>")
                .append("</div>");

        html = html.replace("{SmartTitle}", sb.toString());
        html = html.replace("{ClassName}", generateClassName());
//        Log.e(TAG, html);
        html = (isNeedConvertTraditionalChinese) ?
                chineseConvertor.s2t(html) : chineseConvertor.t2s(html);
        html = (isNeedReplaceSymbol) ? Util.replaceSymbol(html) : html;
        html = replaceImgTagFromHTML(html);
        mWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }


    public static class JavaScriptObject {

        private Activity mInstance;

        public JavaScriptObject(Activity instance) {
            mInstance = instance;
        }

        @JavascriptInterface
        public void openImage(String url) {

//            if (mInstance != null && !mInstance.isFinishing()) {
//                Intent intent = new Intent(mInstance, NewsDetailImageActivity.class);
//                intent.putExtra("imageUrl", url);
//                mInstance.startActivity(intent);
//            }
        }
    }

    private class Refresher extends AsyncTask<String, Void, List<ReadingModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<ReadingModel> doInBackground(String... params) {

            results = ReadingApi.fetchReadingPage(page_id);
            return results;
        }

        @Override
        protected void onPostExecute(List<ReadingModel> result) {
            super.onPostExecute(result);
            if (results != null && results.size() > 0) {
                readingModel = results.get(0);
                readingModel.save();
                setWebView();

                AppLogger.e(readingModel.toString());


                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33FF5722")));
                        getSupportActionBar().setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#33FF5722")));
                    }
                }, 1000);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //对用户按home icon的处理，本例只需关闭activity，就可返回上一activity，即主activity。
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getPageScrollHeight() {
        return mWebView.getHeight() / 2;
    }

    boolean isPaging = false;

    synchronized public void scrollPageBy(int offset) {
        if (isPaging) return;
        int contentHeight = (int) (mWebView.getContentHeight() * mWebView.getScale());
        int maxScrollHeight = contentHeight - mWebView.getHeight();
        int scrollY = mWebView.getScrollY() + offset;

        if (scrollY < 0) {
            scrollY = 0;
        }

        if (scrollY >= maxScrollHeight) {
            scrollY = maxScrollHeight;
        }

        ObjectAnimator animator = ObjectAnimator.ofInt(mWebView, "scrollY", scrollY);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(250);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isPaging = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isPaging = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                isPaging = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    public void nextPage() {
        scrollPageBy(getPageScrollHeight());
    }

    public void prevPage() {
        scrollPageBy(getPageScrollHeight() * -1);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();

        boolean isTurningPageByVolumeKey =
                Settings.getInstance(this).getBoolean(Settings.VOLUME_KEY, true);

        if (isTurningPageByVolumeKey
                && (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    nextPage();
                    break;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    prevPage();
                    break;
            }

            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    private ArrayList<String> mImageList = new ArrayList<String>();

    /**
     * 替换html中的<img标签的属性
     *
     * @param html
     * @return
     */
    private String replaceImgTagFromHTML(String html) {

        Document doc = Jsoup.parse(html);

        Elements es = doc.getElementsByTag("img");
        for (Element e : es) {
            String imgUrl = e.attr("src");
            mImageList.add(imgUrl);

            String localImgPath = Util.getCacheImgFilePath(this, imgUrl);

            e.attr("src_link", "file://" + localImgPath);
            e.attr("ori_link", imgUrl);

            if (!imgUrl.equals(mImageList.get(0))) {
                e.attr("src", "");
            }

            if (!imgUrl.equals(mImageList.get(0)) && !e.attr("class").equals("avatar")) {
                e.attr("onclick", "openImage('" + localImgPath + "')");
            }
        }

        return doc.html();
    }

    @SuppressLint("NewApi")
    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String urlStrArray[] = new String[mImageList.size()];
            mImageList.toArray(urlStrArray);

            Util.readNetworkState(TypeActivity.this);
            if (isAutoPic && !Util.isWIFI) {
                //无图
            } else {
                //有图
                startImageTask(urlStrArray);
            }
        }
    };

    private void startImageTask(String[] urlStrArray) {
        new ImageDownloadTask(TypeActivity.this) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String javascript = "img_replace_all();";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    // In KitKat+ you should use the evaluateJavascript method
                    mWebView.evaluateJavascript(javascript, new ValueCallback<String>() {
                        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                        @Override
                        public void onReceiveValue(String s) {
                            JsonReader reader = new JsonReader(new StringReader(s));

                            // Must set lenient to parse single values
                            reader.setLenient(true);

                            try {
                                if (reader.peek() != JsonToken.NULL) {
                                    if (reader.peek() == JsonToken.STRING) {
                                        String msg = reader.nextString();
                                        if (msg != null) {
//						                   Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                AppLogger.e("ReadingActivity: IOException", e);
                            } finally {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    // NOOP
                                }
                            }
                        }
                    });
                } else {
                    mWebView.loadUrl("javascript:" + javascript);
                }

                mWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        picture = mWebView.capturePicture();
                        new Thread(genScreenShots).start();
                    }
                },2000);

            }


            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                String javascript = "img_replace_by_url('" + values[0] + "')";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(javascript, new ValueCallback<String>() {

                        @Override
                        public void onReceiveValue(String s) {
                            JsonReader reader = new JsonReader(new StringReader(s));

                            // Must set lenient to parse single values
                            reader.setLenient(true);

                            try {
                                if (reader.peek() != JsonToken.NULL) {
                                    if (reader.peek() == JsonToken.STRING) {
                                        String msg = reader.nextString();
                                        if (msg != null) {
//						                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                AppLogger.e("ReadingActivity: IOException", e);
                            } finally {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    // NOOP
                                }
                            }
                        }

                    });
                } else {
                    mWebView.loadUrl("javascript:" + javascript);
                }

            }
        }.executeOnExecutor(MyAsyncTask.DOWNLOAD_THREAD_POOL_EXECUTOR, urlStrArray);
    }

    Picture picture;
    private final Runnable genScreenShots = new Runnable() {
        @Override
        public void run() {
            Bitmap bitmap = null;
            try {
                Thread.sleep(2000);
                if (!isTempScreenShotsFileCached()) {
                    File screenShotsFile = getTempScreenShotsFile();
                    FileOutputStream fileOutPutStream = new FileOutputStream(screenShotsFile);
//                    Picture picture = mWebView.capturePicture();
                    int height = picture.getHeight(), width = picture.getWidth();

                    int factor = width / 440;

                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    Rect rect = new Rect (0,0,width,height);
                    RectF rectf = new RectF(0,0,440,height/factor);

                    Canvas canvas = new Canvas(bitmap);

                    picture.draw(canvas);
//                    mWebView.draw(canvas);


                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutPutStream);
                        fileOutPutStream.flush();
                        fileOutPutStream.close();
                        AppLogger.e("Generated screenshots at " + screenShotsFile.getAbsolutePath());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                // @todo mark do not generate again.
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    };

    public boolean isTempScreenShotsFileCached() {
        File tempScreenShotsFile = getTempScreenShotsFile();
        Boolean is5MinutesAgo = (System.currentTimeMillis() - tempScreenShotsFile.lastModified()) < FIVE_MINUTES;
        return tempScreenShotsFile.exists() && tempScreenShotsFile.length() > 0 && is5MinutesAgo;
    }


    public File getTempScreenShotsFile() {
        return new File(Environment.getExternalStorageDirectory().toString(), page_id + ".png");
    }
}
