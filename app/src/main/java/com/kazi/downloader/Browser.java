package com.kazi.downloader;

import android.app.Activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.webkit.WebSettings;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileBrowser;
import com.kazi.downloader.databinding.ActivityBrowserBinding;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;
import com.thin.downloadmanager.util.Log;

import java.io.File;


/**
 * Created by Kazi on 7/16/2017.
 */

public class Browser extends Activity{

    private ActivityBrowserBinding binding;
    public String url;


    private ThinDownloadManager downloadManager;
    private int downloadId;

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
           public void onDownloadStart(String url, String userAgent, String contentDisposition, final String mimeType, long contentLength) {

               Uri downloadUri = Uri.parse(url);
               final Uri destinationUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+ "/"+URLUtil.guessFileName(url, contentDisposition, mimeType));
               final String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);

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

                               File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                               Intent target = new Intent(Intent.ACTION_VIEW);
                               target.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                               startActivity(target);

                           }


                           @Override
                           public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                               Toast.makeText(getApplicationContext(), "Downloading Failed : " + errorCode, Toast.LENGTH_LONG).show();
                               binding.downloadInfo.setVisibility(View.GONE);
                           }

                           @Override
                           public void onProgress(int id, long totalBytes, long downlaodedBytes, int progress) {
                               binding.downloadProgressView.setProgress(progress);

                           }

                       });
               downloadManager = new ThinDownloadManager();
               downloadId = downloadManager.add(downloadRequest);

               Toast.makeText(getApplicationContext(), "Downloading File",
                       Toast.LENGTH_LONG).show();



               binding.cancelButton.setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick(final View v){
                        int status = downloadManager.cancel(downloadId);
                        if(status == 1){
                            Toast.makeText(getApplicationContext(), "Downloading Canceled",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {Toast.makeText(getApplicationContext(), "Canceling Failed",
                                Toast.LENGTH_SHORT).show();

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

                Intent i=new Intent(this,MainActivity.class);
                startActivity(i);

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
                int status = downloadManager.cancel(downloadId);
                if(status == 1){
                    Toast.makeText(getApplicationContext(), "Downloading Canceled",
                            Toast.LENGTH_SHORT).show();
                }
                else {Toast.makeText(getApplicationContext(), "Canceling Failed",
                        Toast.LENGTH_SHORT).show();

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

}
