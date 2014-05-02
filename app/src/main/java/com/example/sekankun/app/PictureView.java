package com.example.sekankun.app;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;

public class PictureView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String pictureTargetName = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);


        Bundle extras;
        extras = getIntent().getExtras();
        pictureTargetName = extras.getString("pictureTargetName");

        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/test");
        if(dir.exists()){
            Log.d("MyApp", "/test ok");
            Log.d("MyApp", "FileName -> " + pictureTargetName);
            Log.d("MyApp", "DirName -> " + dir.getAbsolutePath());
            File file = new File(dir.getAbsolutePath() + "/" + pictureTargetName);
            if (file.exists()) {
                Log.d("MyApp", "Path -> " + file.getPath());
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                Bitmap _bm = BitmapFactory.decodeFile(file.getPath());
                ((ImageView)findViewById(R.id.imageView)).setImageBitmap(_bm);
            }else{
                //存在しない
            }
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.picture_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_picture_view, container, false);
            return rootView;
        }
    }

}
