package com.mssinfotech.itav;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.google.android.gcm.GCMRegistrar;

import java.lang.reflect.Method;

import static com.mssinfotech.itav.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.mssinfotech.itav.CommonUtilities.EXTRA_MESSAGE;
import static com.mssinfotech.itav.CommonUtilities.SENDER_ID;
import static com.mssinfotech.itav.CommonUtilities.SERVER_URL;
/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeActivity extends AppCompatActivity {
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private String currentUrl;
    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private WebView mWebView;
    ProgressBar progressBar;
    final int SELECT_PHOTO = 1;
    public static String name,email,regId,isLogIn="no",fullname,id,username,avatar,password,language;
    public double latitude,longitude;
    private DBHelper mydb;
    GPSTracker gps;
    Geocoder geocoder;
    // Internet detector
    ProgressDialog mProgressDialog;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        else
            Toast.makeText(this.getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_home);
        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        AppRater.app_launched(this);
        final ProgressBar Pbar;
        Pbar = (ProgressBar) findViewById(R.id.pB1);
        Animation translatebu= AnimationUtils.loadAnimation(this, R.anim.animationfile);
        ImageView tv=(ImageView)findViewById(R.id.imageView);
        tv.startAnimation(translatebu);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Registration is not present, register now with GCM
            GCMRegistrar.register(this, SENDER_ID);
        }
        mydb = new DBHelper(this);
        //language = mydb.getLang();
        // Check if GCM configuration is set
        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
            // GCM sernder id / server url is missing
            alert.showAlertDialog(HomeActivity.this, "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);
            // stop executing code by return
            return;
        }

        //if(isNetworkAvailable())
        {
            gps = new GPSTracker(this);
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            mWebView = (WebView) findViewById(R.id.activity_main_r);
            //mWebView.setVisibility(View.INVISIBLE);
            progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);

            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            mWebView.setScrollbarFadingEnabled(true);
            mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            mWebView.getSettings().setAllowFileAccess(true);
            mWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
            initWebView(mWebView);
            String url="";
            if(mydb.numberOfRowsLang() > 0) {
                language=mydb.getLang();
                if (mydb.numberOfRows() > 0) {
                    Cursor rs = mydb.getMyData();
                    rs.moveToFirst();
                    fullname = rs.getString(rs.getColumnIndex(DBHelper.FULLNAME));
                    id = rs.getString(rs.getColumnIndex(DBHelper.ID));
                    username = rs.getString(rs.getColumnIndex(DBHelper.USERNAME));
                    avatar = rs.getString(rs.getColumnIndex(DBHelper.AVATAR));
                    password = rs.getString(rs.getColumnIndex(DBHelper.PASSWORD));
                    url = "file:///android_asset/index.html";
                    isLogIn = "yes";
                } else {
                    url = "file:///android_asset/first.html";
                    isLogIn = "no";
                }
            }else{
                url = "file:///android_asset/first.html";
                isLogIn = "no";
            }
            mWebView.loadUrl(url);
            //String url="file:///android_asset/index.html";
            mWebView.setWebViewClient(new HelloWebViewClient());
            mWebView.setWebChromeClient(new WebChromeClient()
            {
                public void onProgressChanged(WebView view, int progress)
                {
                    //Make the bar disappear after URL is loaded, and changes string to Loading...
                    setTitle("Loading...");
                    //progressDoalog.show();
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                    setProgress(progress * 100); //Make the bar disappear after URL is loaded
                    Pbar.setProgress(progress);
                    //progressDoalog.incrementProgressBy(1);
                    // Return the app name after finish loading
                    if(progress == 100) {
                        setTitle(R.string.app_name);
                        Pbar.setVisibility(ProgressBar.GONE);
                        RelativeLayout.LayoutParams bparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        bparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        bparams.addRule(RelativeLayout.ALIGN_TOP, mWebView.getId());
                        mWebView.setLayoutParams(bparams);
                        //mWebView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                        //progressDoalog.dismiss();
                    }
                }
                // For 3.0+ Devices (Start)
                // onActivityResult attached before constructor
                protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
                {
                    mUploadMessage = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");
                    startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
                }


                // For Lollipop 5.0+ Devices
                public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams)
                {
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                        uploadMessage = null;
                    }

                    uploadMessage = filePathCallback;

                    Intent intent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        intent = fileChooserParams.createIntent();
                    }
                    try
                    {
                        startActivityForResult(intent, REQUEST_SELECT_FILE);
                    } catch (ActivityNotFoundException e)
                    {
                        uploadMessage = null;
                        Log.d("test file upload","Cannot Open File Chooser");
                        //Toast.makeText(getActivity().getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    return true;
                }

                //For Android 4.1 only
                protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
                {
                    mUploadMessage = uploadMsg;
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
                }

                protected void openFileChooser(ValueCallback<Uri> uploadMsg)
                {
                    mUploadMessage = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");
                    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
                }
            });
        }
        //else{
        //    setContentView(R.layout.offline);
        //}
    }
    private class HelloWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            currentUrl=url;
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                view.reload();
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //animate(view);
            if(isLogIn.equals("no")){
                mWebView.loadUrl("javascript: updateReg('"+regId+"')");
            }else {
                mWebView.loadUrl("javascript: setMyDetail('" + fullname + "','" + username + "','" + avatar + "','" + password + "','" + id + "');");
            }
            mWebView.loadUrl("javascript: setLatLang('"+latitude+"','"+longitude+"'); setLanguage('"+language+"'); ImportJs('language/"+language+".js');");
            //Log.d(CommonUtilities.TAG,"language/"+language+".js");
            super.onPageFinished(view, url);
            SmsReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    try {
                        mWebView.loadUrl("javascript: UpdateMsg('" + messageText + "');");
                        //Log.d("Text",messageText);
                        // mWebView.loadUrl("javascript:{ var str = '"+messageText+"';var arr = str.split(');document.getElementById('password-otp').value = arr[1];}");

                        //Toast.makeText(HomeActivity.this,"Message: "+messageText,Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        //Log.d("Error:","sms : "+e);
                    }
                }
            });
            //view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            setContentView(R.layout.offline);
            Log.d(CommonUtilities.TAG,failingUrl+"-"+description);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub

            super.onPageStarted(view, url, favicon);
        }
    }
    private void animate(final WebView view) {
        Animation anim = AnimationUtils.loadAnimation(getBaseContext(),
                android.R.anim.slide_in_left);
        view.startAnimation(anim);
    }
    //flipscreen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("new brodcast mss","extras"+intent);
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());
            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
            // Showing received message
            //lblMessage.append(newMessage + "\n");
            Toast.makeText(getApplicationContext(), newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    class MyJavascriptInterface {

        Context mContext;

        /** Instantiate the interface and set the context */
        MyJavascriptInterface(Context c)
        {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast)
        {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void reload(String url)
        {
            if(url.equals("")){
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }else{
                mWebView.loadUrl(url);
            }
            return;
            //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void changeLanguage(String mynewurl)
        {
            mydb.ChangeLanguage();
            if(mynewurl.equals("0")) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }else{
                mWebView.loadUrl(mynewurl);
            }
            return;
        }
        @JavascriptInterface
        public void saveUser(String id,String uname,String pass,String avatar, String fullname){
            mydb.insertUser(id, uname, pass, avatar, fullname);
        }
        @JavascriptInterface
        public void setLanguage(String name){
            mydb.insertLang("1", name);
            //Intent 	intent = new Intent(HomeActivity.this,HomeActivity.class);
            //startActivity(intent);
            return;
        }
        @JavascriptInterface
        public void log(String message){
            Log.d(CommonUtilities.TAG,message);
        }
        @JavascriptInterface
        public String choosePhoto()
        {
            // TODO Auto-generated method stub
            String file = "test";
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*;capture=camera");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            return file;
        }
        @JavascriptInterface
        public void ShareIt(String message){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        @JavascriptInterface
        public void logout(){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    HomeActivity.this);
            // set title
            alertDialogBuilder.setTitle("Exit");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Do you really want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            mydb.deleteAll();
                            Intent 	intent = new Intent(HomeActivity.this,HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return;
        }
        @JavascriptInterface
        public void close(){
            showExitDialog();
        }
        @JavascriptInterface
        public void exitNow(){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    HomeActivity.this);
            // set title
            alertDialogBuilder.setTitle("Exit");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Do you really want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            HomeActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }


    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) { //if back key is pressed
        if((keyCode == KeyEvent.KEYCODE_BACK)&& mWebView.canGoBack())
        {
            /*
            //Toast.makeText(this.getApplicationContext(), "url"+currentUrl, Toast.LENGTH_LONG).show();
            if(currentUrl=="http://www.mssinfotech.in/itav/accountstatement.php"){
                mWebView.loadUrl("http://www.mssinfotech.in/itav/");
            }else if(currentUrl=="http://www.mssinfotech.in/itav/"){
                return super.onKeyDown(keyCode, event);
            }else {

            }*/
            //mWebView.loadUrl("javascript: $('.loading-mask').removeClass('stop-loading');");
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
    @Override
    public void onBackPressed() {
        showExitDialog();
    }
    private void showExitDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Confirm...");

        // Setting Dialog Message
        alertDialog.setMessage("Exit Application?");
        alertDialog.setCancelable(false);

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.info);

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // Write your code here to execute after dialog closed
                //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                HomeActivity.this.finish();
                System.exit(0);
            }
        });

        // Setting OK Button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    private final static Object methodInvoke(Object obj, String method, Class<?>[] parameterTypes, Object[] args) {
        try {
            Method m = obj.getClass().getMethod(method, new Class[] { boolean.class });
            m.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void initWebView(WebView webView) {

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);
        // settings.setPluginsEnabled(true);
        methodInvoke(settings, "setPluginsEnabled", new Class[] { boolean.class }, new Object[] { true });
        // settings.setPluginState(PluginState.ON);
        methodInvoke(settings, "setPluginState", new Class[] { WebSettings.PluginState.class }, new Object[] { WebSettings.PluginState.ON });
        // settings.setPluginsEnabled(true);
        methodInvoke(settings, "setPluginsEnabled", new Class[] { boolean.class }, new Object[] { true });
        // settings.setAllowUniversalAccessFromFileURLs(true);
        methodInvoke(settings, "setAllowUniversalAccessFromFileURLs", new Class[] { boolean.class }, new Object[] { true });
        // settings.setAllowFileAccessFromFileURLs(true);
        methodInvoke(settings, "setAllowFileAccessFromFileURLs", new Class[] { boolean.class }, new Object[] { true });

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);

        // webView.setDownloadListener(downloadListener);
    }
    public void refresh_Click(View v){
        Intent 	intent = new Intent(HomeActivity.this,HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        return;
    }
}
