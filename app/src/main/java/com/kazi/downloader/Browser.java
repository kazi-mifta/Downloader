package com.kazi.downloader;

import android.app.Activity;

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

import com.kazi.downloader.databinding.ActivityBrowserBinding;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;


/**
 * Created by Kazi on 7/16/2017.
 */

public class Browser extends Activity{

    private ActivityBrowserBinding binding;
    public String url;

    private ThinDownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle saveedInstanceState){

        super.onCreate(saveedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_browser);


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
            setupTxtUrl();
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
        binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        WebSettings websettings = binding.webView.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setDomStorageEnabled(true);
        websettings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.1.2; C1905 Build/15.1.C.2.8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36");
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

               Uri downloadUri = Uri.parse(url);
               Uri destinationUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/"+URLUtil.guessFileName(url, contentDisposition, mimeType));

               binding.downloadInfo.setVisibility(View.VISIBLE);
               binding.downloadProgressView.setProgress(0);
               DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                       .addCustomHeader("Auth-Token", "YourTokenApiKey")
                       .setRetryPolicy(new DefaultRetryPolicy())
                       .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                       .setDownloadListener(new DownloadStatusListener() {
                           @Override
                           public void onDownloadComplete(int id) {

                               Toast.makeText(getApplicationContext(), "Downloading Finished", Toast.LENGTH_SHORT).show();
                               binding.downloadInfo.setVisibility(View.GONE);
                           }

                           @Override
                           public void onDownloadFailed(int id, int errorCode, String errorMessage) {

                           }

                           @Override
                           public void onProgress(int id, long totalBytes, long downlaodedBytes, int progress) {
                               binding.downloadProgressView.setProgress(progress);
                               binding.downloadedBytes.setText("" + downlaodedBytes+" kb");
                               binding.totalBytes.setText("" + totalBytes+" kb");
                           }

                       });
               downloadManager = new ThinDownloadManager();
               int downloadId = downloadManager.add(downloadRequest);


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
