package com.example.sekankun.app;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;


public class PlateEditOnImageView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate_edit_on_image_view);

        //施工看板
        TextView constructName = (TextView)findViewById(R.id.constructName);
        constructName.setText("テスト");

        TextView constructField = (TextView)findViewById(R.id.constructField);
        constructField.setText(SekouKanriDB.getCurrentBuilding() + " " + SekouKanriDB.getCurrentFloor() + " " + SekouKanriDB.getCurrentRoom());

        TextView constructContent = (TextView)findViewById(R.id.constructContent);
        constructContent.setText(SekouKanriDB.getCurrentTarget());

        TextView constructBeAf = (TextView)findViewById(R.id.constructBeAf);
        constructBeAf.setText(SekouKanriDB.getWhichBeforeAfter());


        String pictureTargetName = null;

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
                Log.d("MyApp","Not Exist Picture...");
            }
        }

        // ドラッグ対象Viewとイベント処理クラスを紐付ける
        LinearLayout dragView = (LinearLayout) findViewById(R.id.plateBase);
        DragViewListener listener = new DragViewListener(dragView);
        dragView.setOnTouchListener(listener);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.plate_edit_on_image_view, menu);
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

}
