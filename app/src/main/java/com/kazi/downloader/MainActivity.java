package com.kazi.downloader;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileBrowser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.io.File;
import java.util.concurrent.TimeUnit;

import ua.com.crosp.solutions.library.prettytoast.PrettyToast;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String url=null;
    public String googleUrl= "www.google.com";
    public boolean mSlideState = false;

    public PrettyToast prettyToast;

    private PrefManager prefManager;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrettyToast.initIcons();


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        EditText text = (EditText)findViewById(R.id.editText);
        setSupportActionBar(toolbar);


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        prefManager = new PrefManager(this);

        if(prefManager.isFirstTimeLaunch()){


            prettyToast = new PrettyToast.Builder(getApplicationContext())
                    .withCustomView(LayoutInflater.from(MainActivity.this).inflate(R.layout.navigation_layout, null, false))
                    .withDuration(Toast.LENGTH_LONG)
                    .build();

            for(int i=0;i<6;i++){
                prettyToast.show();
            }

            prefManager.setIsFirstTimeLaunch(false);
        }
        else {

            if(text.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                text.isFocusable();
                text.isFocusableInTouchMode();
            }
        }








        drawer.setDrawerListener(new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                0,
                0){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mSlideState=false;//is Closed
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mSlideState=true;//is Opened
            }});







    }



    public void openBrowser(View view){

        EditText text = (EditText)findViewById(R.id.editText);
        url=text.getText().toString();


        Intent i=new Intent(this,Browser.class);
        Bundle bundle= new Bundle();
        bundle.putString("url",url);
        i.putExtras(bundle);
        startActivity(i);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {


        } else if (id == R.id.nav_browser) {


            Intent i=new Intent(this,Browser.class);
            Bundle bundle= new Bundle();
            bundle.putString("url",googleUrl);
            i.putExtras(bundle);
            startActivity(i);

        } else if (id == R.id.nav_files) {

            Intent i = new Intent(this, FileBrowser.class); //works for all 3 main classes (i.e FileBrowser, FileChooser, FileBrowserWithCustomHandler)
            i.putExtra(Constants.INITIAL_DIRECTORY, new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()).getAbsolutePath());
            startActivity(i);

        }else if (id == R.id.nav_help) {

            Intent i=new Intent(this,Help.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        boolean handled = false;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        EditText text = (EditText)findViewById(R.id.editText);
        text.setSelection(text.getText().length());
        text.requestFocus();

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

                Intent k=new Intent(this,Browser.class);
                Bundle bundle= new Bundle();
                bundle.putString("url",googleUrl);
                k.putExtras(bundle);
                startActivity(k);


                handled = true;
                break;
            case KeyEvent.KEYCODE_MENU:

                if(mSlideState){
                    drawer.closeDrawer(Gravity.START);
                    text.requestFocus();
                }else{
                    drawer.openDrawer(Gravity.START);
                    text.requestFocus();
                }

                handled = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                // ... handle right action
                Intent j=new Intent(this,Help.class);
                startActivity(j);

                handled = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                // ... handle right action

                Intent i = new Intent(this, FileBrowser.class); //works for all 3 main classes (i.e FileBrowser, FileChooser, FileBrowserWithCustomHandler)
                i.putExtra(Constants.INITIAL_DIRECTORY, new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()).getAbsolutePath());
                startActivity(i);



                handled = true;
                break;
        }
        return handled || super.onKeyDown(keyCode, event);
    }




}
