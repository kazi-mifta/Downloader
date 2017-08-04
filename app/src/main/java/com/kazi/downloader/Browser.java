package com.kazi.downloader;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.webkit.WebSettings;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kazi.downloader.databinding.ActivityBrowserBinding;

import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.listener.FetchListener;
import com.tonyodev.fetch.request.Request;

import java.io.File;


/**
 * Created by Kazi on 7/16/2017.
 */


public class Browser extends Activity{

    private ActivityBrowserBinding binding;
    public String url;

    public String dirPath;
    public String fileName;

    public int i = 0;

    public Toast mToast = null;

    public long downloadId;

    public Fetch fetch;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle saveedInstanceState){

        super.onCreate(saveedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_browser);




        Bundle bundle = getIntent().getExtras();
        url=bundle.getString("url");

        if(url.matches("")) {
            setupWebView();
            setupTxtUrl();
        }
        else{
            setupWebView();
            loadFromDownloader(url);
            setupTxtUrl();

        }

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

    }


    public void back(View v) {
        if (binding.webView.canGoBack())
            binding.webView.goBack();
    }

    public void forward(View v) {
        if (binding.webView.canGoForward())
            binding.webView.goForward();
    }

    public void refresh(View v) {
        binding.webView.reload();
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private String buildUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("https://"))
            return url;
        return "http://".concat(url);
    }

    private void setupWebView() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            binding.webView.setFocusable(true);
            binding.webView.setFocusableInTouchMode(true);
            binding.webView.getSettings().setJavaScriptEnabled(true);
            binding.webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            binding.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            binding.webView.getSettings().setDomStorageEnabled(true);
            binding.webView.getSettings().setDatabaseEnabled(true);
            binding.webView.getSettings().setAppCacheEnabled(true);
            binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.progressBar.setProgress(0);
                binding.txtUrl.setText(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        binding.webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                binding.progressBar.setProgress(newProgress);
            }
        });


    //Initiazlizing Fetch
        new Fetch.Settings(this)
                .setAllowedNetwork(Fetch.NETWORK_ALL)
                .enableLogging(true)
                .setConcurrentDownloadsLimit(1)
                .apply();

        fetch = Fetch.newInstance(this);
        fetch.removeAll();



        binding.webView.setDownloadListener(new DownloadListener() {
            @Override
           public void onDownloadStart(String url, String userAgent, String contentDisposition, final String mimeType, long contentLength) {

                i=0;

               Uri downloadUri = Uri.parse(url);

               final Uri destinationUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
               dirPath = destinationUri.toString();

               fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);



               Request request = new Request(url,dirPath,fileName);
               downloadId = fetch.enqueue(request);

               if(downloadId != Fetch.ENQUEUE_ERROR_ID) {
                   //Download was successfully queued for download.

                   if(mToast!= null){
                       mToast.cancel();
                   }
                   mToast = Toast.makeText(getApplicationContext(), "Downloading File",
                           Toast.LENGTH_LONG);
                   mToast.show();

                   binding.downloadInfo.setVisibility(View.VISIBLE);
                   binding.downloadProgressView.setProgress(0);
               }

               fetch.addFetchListener(new FetchListener() {

                   @Override
                   public void onUpdate(long id, int status, int progress, long downloadedBytes, long fileSize, int error) {

                       if(downloadId == id && status == Fetch.STATUS_DOWNLOADING) {

                           binding.downloadProgressView.setProgress(progress);

                       }else if( downloadId==id && status == Fetch.STATUS_DONE){

                           if(mToast!= null){
                               mToast.cancel();
                           }
                           mToast = Toast.makeText(getApplicationContext(), "Downloading Finished", Toast.LENGTH_SHORT);
                           mToast.show();

                           binding.downloadInfo.setVisibility(View.GONE);

                           if(i == 0) {
                               File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                               Intent target = new Intent(Intent.ACTION_VIEW);
                               target.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                               startActivity(target);
                               i++;
                           }

                         }else{
                           //An error occurred
                           if(mToast!= null){
                               mToast.cancel();
                           }
                           mToast = Toast.makeText(getApplicationContext(), "Downloading Failed : " + status, Toast.LENGTH_LONG);
                           mToast.show();

                           binding.downloadInfo.setVisibility(View.GONE);

                       }
                   }
               });

               binding.cancelButton.setOnClickListener( new View.OnClickListener(){
                   @Override
                   public void onClick(final View v){

                       fetch.remove(downloadId);
                       fetch.removeAll();
                       binding.downloadInfo.setVisibility(View.GONE);

                       File file = new File(dirPath, fileName);
                       boolean deleted = file.delete();


                       if (deleted == true) {
                           if(mToast!= null){
                               mToast.cancel();
                           }
                           mToast = Toast.makeText(getApplicationContext(), "Downloading Canceled & File Deleted",
                                   Toast.LENGTH_SHORT);
                            mToast.show();
                       }
                       else {
                           if(mToast!= null){
                               mToast.cancel();
                           }
                           mToast = Toast.makeText(getApplicationContext(), "Downloading Canceled",
                                   Toast.LENGTH_SHORT);
                           mToast.show();
                       }
                   }
               });


           }});
    }



    private void setupTxtUrl() {
        binding.txtUrl.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    binding.webView.loadUrl(buildUrl(binding.txtUrl.getText().toString()));
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    public void loadFromDownloader(String url) {


        binding.webView.loadUrl(buildUrl(url));
        setupTxtUrl();


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        boolean handled = false;


        switch (keyCode){

            case KeyEvent.KEYCODE_BACK:
                // ... handle right action
                super.onBackPressed();

                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                // ... handle right action

                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                // ... handle right action
                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                // ... handle selections
                handled = true;
                break;
            case KeyEvent.KEYCODE_BUTTON_A:
                // ... handle selections
                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                // ... handle left action

                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                // ... handle right action

                handled = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                // ... handle right action
            {
                fetch.remove(downloadId);
                fetch.removeAll();
                binding.downloadInfo.setVisibility(View.GONE);

                File file = new File(dirPath, fileName);
                boolean deleted = file.delete();

                if (deleted == true) {
                    if(mToast!= null){
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getApplicationContext(), "Downloading Canceled & File Deleted",
                            Toast.LENGTH_SHORT);
                    mToast.show();
                }
                else {
                    if(mToast!= null){
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getApplicationContext(), "Downloading Canceled",
                            Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }

                handled = true;
                break;
            case KeyEvent.KEYCODE_MENU:
                // ... handle right action

                handled = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                // ... handle right action

                handled = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                // ... handle right action


                handled = true;
                break;
        }
        return handled || super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        fetch.release();
    }

}
