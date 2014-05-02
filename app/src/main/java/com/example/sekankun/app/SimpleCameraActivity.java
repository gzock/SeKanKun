package com.example.sekankun.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.widget.FrameLayout;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.TextView;

import org.w3c.dom.Text;


public class SimpleCameraActivity extends ActionBarActivity {

    // カメラインスタンス
    private Camera mCam = null;

    // カメラプレビュークラス
    private CameraPreview mCamPreview = null;

    // 画面タッチの2度押し禁止用フラグ
    private boolean mIsTake = false;

    private  String pictureTargetName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_camera);

        Bundle extras;
        extras = getIntent().getExtras();
        pictureTargetName = extras.getString("pictureTargetName");

        // カメラインスタンスの取得
        try {
            mCam = Camera.open();
            Log.d("MyCamera", "Camera Open");
        } catch (Exception e) {
            // エラー
            Log.d("MyACamera", "error");
            this.finish();

        }

        Camera.Parameters params = mCam.getParameters();

        //Preview解像度取得
        List<Camera.Size> supportedSizes = params.getSupportedPreviewSizes();
        if (supportedSizes != null && supportedSizes.size() > 1) {
            for(int i=0;i<supportedSizes.size();i++){
                Camera.Size size = supportedSizes.get(i);
                //解像度表示
                Log.d("preview","width:"+size.width+" height:"+size.height);
                //解像度を設定する
                //params.setPreviewSize(size.width, size.height);
            }
        }
        //Picture解像度取得
        List<Camera.Size> supportedPictureSizes = params.getSupportedPictureSizes();
        if (supportedPictureSizes != null && supportedPictureSizes.size() > 1) {
            for(int i=0;i<supportedPictureSizes.size();i++){
                Camera.Size size = supportedPictureSizes.get(i);
                //解像度表示
                Log.d("picture","width:"+size.width+" height:"+size.height);
                //解像度を設定する
                //params.setPictureSize(size.width, size.height);
            }
        }

        outside : for(int i=0;i<supportedSizes.size();i++){
            Camera.Size size = supportedSizes.get(i);
            for(int j=0;j<supportedPictureSizes.size();j++){
                Camera.Size size2 = supportedPictureSizes.get(j);

                if(size.width == size2.width) {
                    if(size.height == size2.height) {
                        Log.d("MyApp", "採用 -> " + "picture" + "width:" + size.width + " height:" + size.height);
                        //params.setPreviewSize(size.width, size.height);
                        //params.setPictureSize(size.width, size.height);
                        break outside;
                    }
                }

            }
        }


        // FrameLayout に CameraPreview クラスを設定
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        mCamPreview = new CameraPreview(this, mCam);
        preview.addView(mCamPreview);




        // mCamPreview に タッチイベントを設定
        mCamPreview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!mIsTake) {
                        // 撮影中の2度押し禁止用フラグ
                        mIsTake = true;
                        // 画像取得
                        mCam.takePicture(null, null, mPicJpgListener);

                        //テスト
                        //ボツネタにするにせよ、あとでまとめよう
                        /*

                        ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
                        root.setDrawingCacheEnabled(true);
                        //this.setDrawingCacheEnabled(true);
                        Bitmap bitmap1 = Bitmap.createBitmap(root.getDrawingCache());

                        View temp = findViewById(android.R.id.content);
                        View temp2 = findViewById(R.id.constructPanel);
                        //getScreenBitmap(temp);

                        String saveDir = Environment.getExternalStorageDirectory().getPath() + "/test";
                        String imgPath = saveDir + "/" + "kanban" + ".jpg";
                        savePicture(getViewBitmap(temp), imgPath);
                        */


                    }
                }
                return true;
            }
        });



        //再撮影ボタン
        findViewById(R.id.reCamButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCam.startPreview();
                findViewById(R.id.reCamButton).setEnabled(false);
            }
        });

        //採用ボタン
        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ダイアログの表示
                AlertDialog.Builder ad = new AlertDialog.Builder(SimpleCameraActivity.this);
                final AlertDialog.Builder ad2 = new AlertDialog.Builder(SimpleCameraActivity.this);
                ad.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ad2.setTitle("施工看板の追加を行いますか？");
                        ad2.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                setResult(2);
                                finish();
                            }
                        });
                        ad2.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                setResult(1);
                                finish();
                            }
                        });
                        ad2.create();
                        ad2.show();
                    }
                });
                ad.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mCam.startPreview();
                    }
                });
                ad.setTitle("この写真を採用しますか？");
                ad.create();
                ad.show();
            }
        });

        //撮影終了ボタン
        findViewById(R.id.camFinButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ダイアログの表示
                AlertDialog.Builder ad=new AlertDialog.Builder(SimpleCameraActivity.this);
                ad.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
                ad.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mCam.startPreview();
                    }
                });
                ad.setTitle("施工写真を撮影しておりません。撮影を終了してもよろしいですか？");
                ad.create();
                ad.show();
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    public void savePicture(Bitmap mBitmap, String path) {
        try {
            // sdcardフォルダを指定
            File root = Environment.getExternalStorageDirectory();

            // 日付でファイル名を作成　
            //Date mDate = new Date();
            //SimpleDateFormat fileName = new SimpleDateFormat("yyyyMMdd_HHmmss");

            // 保存処理開始
            FileOutputStream fos = null;
            fos = new FileOutputStream(path);

            // jpegで保存
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // 保存処理終了
            fos.close();
        } catch (Exception e) {
            Log.e("Error", "" + e.toString());
        }
    }

    public  void makePicture(Bitmap baseBmp, Bitmap overBmp) {
        Canvas offScreen = new Canvas(baseBmp);

        offScreen.drawBitmap(overBmp,0,100,(Paint)null );


    }

    public Bitmap getViewBitmap(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        if(cache == null){
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cache);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public Bitmap getScreenBitmap(View view){
        return getViewBitmap(view.getRootView());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // カメラ破棄インスタンスを解放
        if (mCam != null) {
            mCam.release();
            mCam = null;
        }
    }

    /**
     * JPEG データ生成完了時のコールバック
     */
    private Camera.PictureCallback mPicJpgListener = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null) {
                return;
            }

            String saveDir = Environment.getExternalStorageDirectory().getPath() + "/test";

            // SD カードフォルダを取得
            File file = new File(saveDir);

            // フォルダ作成
            if (!file.exists()) {
                if (!file.mkdir()) {
                    Log.e("Debug", "Make Dir Error");
                }
            }

            // 画像保存パス
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            //String imgPath = saveDir + "/" + sf.format(cal.getTime()) + ".jpg";
            String imgPath = saveDir + "/" + pictureTargetName + ".jpg";

            //String imgPath = saveDir + "/" + "base" + ".jpg";
            //String imgPath2 = saveDir + "/" + "kanban" + ".jpg";
            //String imgPath3 = saveDir + "/" + pictureTargetName + ".jpg";

            // ファイル保存
            FileOutputStream fos;
            try {
                //第二引数をfalseにすると上書き許可
                fos = new FileOutputStream(imgPath, false);
                fos.write(data);
                fos.close();

                // アンドロイドのデータベースへ登録
                // (登録しないとギャラリーなどにすぐに反映されないため)
                //registAndroidDB(imgPath);

            } catch (Exception e) {
                Log.e("Debug", e.getMessage());
            }

            fos = null;

            //Bitmap baseBmp = BitmapFactory.decodeFile(imgPath);
            //Bitmap overBmp = BitmapFactory.decodeFile(imgPath2);
            //Bitmap overBmp2 = BitmapFactory.decodeFile(imgPath2);

            try {
                //テスト
                //ボツネタにするにせよ、あとでまとめよう
                /*
                ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
                overBmp2.compress(Bitmap.CompressFormat.JPEG, 90, baoStream );
                baoStream.flush();
                byte[] _bArray = baoStream.toByteArray();
                baoStream.close();
                InvalidBufferFromColor(overBmp2.getHeight(), overBmp2.getWidth(), _bArray, 0xff000000);
                Bitmap overBmp4 = BitmapFactory.decodeByteArray(_bArray,0,_bArray.length);
                savePicture(overBmp4, imgPath2);

                FrameLayout imageView = (FrameLayout) findViewById(R.id.cameraPreview);
                //Bitmap newBitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
                Bitmap newBitmap = Bitmap.createBitmap(baseBmp.getWidth(), baseBmp.getHeight(), Bitmap.Config.ARGB_8888);

                Canvas offScreen = new Canvas(newBitmap);

                //offScreen.drawBitmap(baseBmp,0,0,(Paint)null );
                //offScreen.drawBitmap(overBmp,0,0,(Paint)null );

                offScreen.drawBitmap(baseBmp,null,new Rect(0, 0, baseBmp.getWidth(), baseBmp.getHeight()), null );
                offScreen.drawBitmap(overBmp4,null,new Rect(0, 0, baseBmp.getWidth(), baseBmp.getHeight()), null );

                savePicture(newBitmap, imgPath3);

                // takePicture するとプレビューが停止するので、再度プレビュースタート
                //mCam.startPreview();
                */

                mIsTake = false;

                findViewById(R.id.reCamButton).setEnabled(true);
                findViewById(R.id.submitButton).setEnabled(true);



            } catch (Exception e) {
                Log.d("error", e.toString());
            }

        };
    };



    //戻るボタン制御
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            //ダイアログの表示
            AlertDialog.Builder ad=new AlertDialog.Builder(SimpleCameraActivity.this);
            ad.setMessage("test");
            ad.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //ここはクラス内で実行
                    int REQUEST_ACTION_PICK = 1;

                    //実行フロー
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //これだとギャラリー専門が開きます。
                    //Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    //createChooserを使うと選択ダイアログのタイトルを変更する事ができます。
                    startActivityForResult(Intent.createChooser(intent,"select"), REQUEST_ACTION_PICK);
                    //デフォルトで「アプリ選択」と出ます。
                    //startActivityForResult(intent, REQUEST_ACTION_PICK);
                    //OKならActivity終了
                    finish();


                }
            });
            ad.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    //NOならそのまま何もしない
                }
            });
            ad.create();
            ad.show();
            return false;
        } else{
            //これで通常の戻るボタン制御
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.simple_camera, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_simple_camera, container, false);
            return rootView;
        }
    }

}
