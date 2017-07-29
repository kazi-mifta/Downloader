package com.kazi.downloader;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileBrowser;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String url=null;
    public String googleUrl= "www.google.com";
    public boolean mSlideState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        EditText text = (EditText)findViewById(R.id.editText);
        text.setSelection(text.getText().length());



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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            Intent i=new Intent(this,MainActivity.class);
            startActivity(i);

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

        } /*else if (id == R.id.nav_favourites) {

        } else if (id == R.id.nav_settings) {

        }*/ else if (id == R.id.nav_help) {

            Intent i=new Intent(this,Help.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void clickEventSlide(){


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        if(mSlideState){
            drawer.closeDrawer(Gravity.START);

        }else{
            drawer.openDrawer(Gravity.START);

        }
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
