package com.kazi.downloader;

import android.app.Activity;
import android.app.DownloadManager;
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
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ayz4sci.androidfactory.DownloadProgressView;
import com.kazi.downloader.databinding.ActivityBrowserBinding;


/**
 * Created by Kazi on 7/16/2017.
 */

public class Browser extends Activity{

    private ActivityBrowserBinding binding;
    public String url;
    private DownloadProgressView downloadProgressView;
    private long downloadID;

    @Override
    protected void onCreate(Bundle saveedInstanceState){

        super.onCreate(saveedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_browser);
        downloadProgressView = (DownloadProgressView) findViewById(R.id.downloadProgressView);

        Bundle bundle = getIntent().getExtras();
        url=bundle.getString("url");

        if(url.matches("")) {
            setupWebView();
            //loadFromDownloader(url);
            setupTxtUrl();
        }
        else{
            setupWebView();
            loadFromDownloader(url);
            //setupTxtUrl();
        }

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

        binding.webView.getSettings().setSaveFormData(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            binding.webView.getSettings().setSavePassword(false);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setVerticalScrollBarEnabled(false);
        binding.webView.setHorizontalScrollBarEnabled(false);
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


        binding.webView.setDownloadListener(new DownloadListener() {
           @Override
           public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
               DownloadManager.Request request = new DownloadManager.Request(
                       Uri.parse(url));


               request.setMimeType(mimeType);

               String cookies = CookieManager.getInstance().getCookie(url);

               request.addRequestHeader("cookie", cookies);

               request.addRequestHeader("User-Agent", userAgent);

               request.setDescription("Downloading file...");

               request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                       mimeType));

               request.allowScanningByMediaScanner();

               request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
               request.setDestinationInExternalPublicDir(
                       Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                               url, contentDisposition, mimeType));
               DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
               downloadID = dm.enqueue(request);

               downloadProgressView.show(downloadID, new DownloadProgressView.DownloadStatusListener() {
                   @Override
                   public void downloadFailed(int reason) {
                       System.err.println("Failed :" + reason);
                   }

                   @Override
                   public void downloadSuccessful() {

                   }

                   @Override
                   public void downloadCancelled() {

                   }
               });

               Toast.makeText(getApplicationContext(), "Downloading File",
                       Toast.LENGTH_LONG).show();
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

    public void loadFromDownloader(String url){
        binding.webView.loadUrl(buildUrl(url));
    }

}
