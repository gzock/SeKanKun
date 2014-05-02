package com.example.sekankun.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

//Add
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import android.util.Log;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;


public class MainActivity extends ActionBarActivity {

    private List<String> str;

    final DatabaseOpenHelper helper = new DatabaseOpenHelper( this);
    final private Integer cameraActivity = 1;
    final private Integer editListSelectViewActivity = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add
        File dir = new File(Environment.getExternalStorageDirectory().getPath());
        Log.d("MyApp", "First -> " + dir);

        //SDカードが存在した場合
        if(dir.exists()){
            //DB元となるcsvが存在するか？
            File file = new File(dir.getAbsolutePath()+"/test.csv");
            if (file.exists()) {
                try {
                    str = new ArrayList<String>();

                    //csvから一行ずつ読み込み格納
                    FileReader in = new FileReader(file);
                    BufferedReader br = new BufferedReader(in);
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        str.add(line);
                    }
                    br.close();
                    in.close();

                    //一行ずつ格納されたListからカンマで区切って、2次元配列を作成
                    //ここはちょっとどうにかしたい。[][]はなんかね・・・
                    String[][] readDatas = new String[str.size()][7];
                    int num = 0;
                    for(String f : str){
                        String[] tempStr = f.split(",");
                        readDatas[num] = tempStr;
                        num++;
                    }

                    //基本となるhelperの作成と、DBの作成
                    final DatabaseOpenHelper helper = new DatabaseOpenHelper( this);
                    SekouKanriDB.setDbData(readDatas);

                    //リストの初回取得のためのSQLクエリ
                    Cursor cursor = helper.getReadableDatabase().rawQuery("select _id, building_name from SekouKanriDB where _id in (select min(_id) from SekouKanriDB group by building_name)",null);

                    ListView listView = (ListView) findViewById(R.id.listView);

                    try{
                        listView.setAdapter( createCursorAdapter(cursor, new String[] {SekouKanriDB.COLUMN_BUILDING_NAME}, new int[] {R.id.text_Sample} ) );

                        //リスト項目をクリック時に呼び出されるコールバックを登録
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            //リスト項目クリック時の処理
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                                //Cursorで入れてるので、それでキャストしてあげないとダメ
                                //いきなりStringでキャストはダメー
                                ListView list = (ListView)parent;
                                Cursor cursor1 = (Cursor)list.getItemAtPosition(position);

                                try{
                                    //機器毎リストまで進んでいるなら、リストの更新ではなく、各情報をセットした上で施工写真の撮影へ
                                    if(SekouKanriDB.getCurrentClass().equals("target_name")){
                                        SekouKanriDB.setRowid(Integer.valueOf(cursor1.getString(0)));
                                        SekouKanriDB.setCurrentTarget(cursor1.getString(1));
                                        SekouKanriDB.setWhichBeforeAfter(cursor1.getString(2));

                                        new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("施工写真の撮影を行いますか？")
                                            .setPositiveButton(
                                                "はい",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //撮影画像のファイル名をここで作っておく
                                                        String pictureTargetName = SekouKanriDB.getCurrentBuilding() + "_" + SekouKanriDB.getCurrentFloor() + "_" + SekouKanriDB.getCurrentRoom() + "_" + SekouKanriDB.getCurrentTarget() + "_" + SekouKanriDB.getWhichBeforeAfter();
                                                        Log.d("MyAPP", "Jpeg名 -> " + pictureTargetName);

                                                        //カメラ関連のアクティビティへ投げる
                                                        //putExtraで次のアクティビティへさっきのファイル名を渡す
                                                        Intent intent = new Intent(MainActivity.this, SimpleCameraActivity.class);
                                                        intent.putExtra("pictureTargetName", pictureTargetName);
                                                        startActivityForResult(intent, 1);

                                                    }
                                                })
                                            .setNegativeButton(
                                                "いいえ",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                }).show();
                                    } else {
                                        //基本はこっち
                                        //選ばれたリストの更新を行う
                                        //要は階層を一つ降りるということ
                                        String str = cursor1.getString(cursor1.getColumnIndex(SekouKanriDB.getCurrentClass()));
                                        Log.d("MyApp", "Selected Item -> " + str);

                                        //引数がupなら階層を下がる (単語ミスじゃね？ｗ
                                        SekouKanriDB.setTempSqlQuery( helper.getUpdateQuery(str, "up") );
                                        Cursor cursor = helper.getReadableDatabase().rawQuery( SekouKanriDB.getTempSqlQuery(), null);

                                        ListView listView = (ListView) findViewById(R.id.listView);

                                        try{
                                            listView.setAdapter( createCursorAdapter(cursor, new String[] {SekouKanriDB.getCurrentClass()},new int[] {R.id.text_Sample} ) );

                                            //一番上の建物階層以外なら、戻るボタンを有効化する
                                            if(!SekouKanriDB.getCurrentClass().equals("building_name")){
                                                findViewById(R.id.backButton).setEnabled(true);
                                            }
                                        } catch (Exception e) {
                                        }
                                    }

                                } catch (Exception e){
                                    Log.d("MyApp", e.toString());
                                }
                            }
                        });

                        //項目を長押しされたときは、施工写真のプレビューを行う
                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            //リスト項目クリック時の処理
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,long id) {
                                //Cursorで入れてるので、それでキャストしてあげないとダメ
                                //いきなりStringでキャストはダメー
                                ListView list = (ListView)parent;
                                final Cursor cursor1 = (Cursor)list.getItemAtPosition(position);

                                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                                ad.setTitle("施工写真を確認しますか？");
                                ad.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        //選択された項目から、ファイル名を組み立てる
                                        String pictureTargetName = SekouKanriDB.getCurrentBuilding() + "_" + SekouKanriDB.getCurrentFloor() + "_" + SekouKanriDB.getCurrentRoom() + "_" + cursor1.getString(1) + "_" + cursor1.getString(2) + ".jpg";

                                        //プレビュー用のアクティビティへ移動
                                        //組み立てたファイル名を渡す
                                        Intent intent = new Intent(MainActivity.this, PictureView.class);
                                        intent.putExtra("pictureTargetName", pictureTargetName);
                                        startActivityForResult(intent, 2);

                                    }
                                });
                                ad.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //NOならそのまま何もしない
                                    }
                                });
                                ad.create();
                                ad.show();

                                return true;
                            }
                        });


                        //戻るボタンを押された際の動作
                        //階層を上に上がる
                        //戻るボタンが冗長状態。どする？
                        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //戻るので、引数down
                                SekouKanriDB.setTempSqlQuery( helper.getUpdateQuery(SekouKanriDB.getCurrentClass(), "down") );
                                Cursor cursor = helper.getReadableDatabase().rawQuery( SekouKanriDB.getTempSqlQuery(), null);
                                ListView listView = (ListView) findViewById(R.id.listView);

                                try{
                                    listView.setAdapter( createCursorAdapter(cursor, new String[] {SekouKanriDB.getCurrentClass()},new int[] {R.id.text_Sample} ) );

                                    if(SekouKanriDB.getCurrentClass().equals("building_name")){
                                        findViewById(R.id.backButton).setEnabled(false);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        });

                        Log.d("MyApp", "end...");

                    } catch (Exception e) {
                        Log.d("MyApp", e.toString());
                    }

                } catch (Exception e) {
                    Log.d("MyApp", e.toString());
                }
            }else{
                Log.d("MyApp", "File Exists...");
            }
        } else {
            Log.d("MyApp", "Dir Existst...");
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    //戻るボタンが冗長状態。どする？
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            if(!SekouKanriDB.getCurrentClass().equals("building_name")) {
                //戻るので、引数down
                SekouKanriDB.setTempSqlQuery( helper.getUpdateQuery(SekouKanriDB.getCurrentClass(), "down") );
                Cursor cursor = helper.getReadableDatabase().rawQuery( SekouKanriDB.getTempSqlQuery(), null);
                ListView listView = (ListView) findViewById(R.id.listView);

                try{
                    listView.setAdapter( createCursorAdapter(cursor, new String[] {SekouKanriDB.getCurrentClass()},new int[] {R.id.text_Sample} ) );

                    if(SekouKanriDB.getCurrentClass().equals("building_name")){
                        findViewById(R.id.backButton).setEnabled(false);
                    }
                } catch (Exception e) {
                }
            } else {
                return super.onKeyDown(keyCode, event);
            }
            return true;
        }
        return false;
    }

    //ここは考えなおそう
    private CustomSimpleCursorAdapter createCursorAdapter(Cursor cursor, String[] strs, int[] ints) {
        // カーソルからアダプタを生成
        CustomSimpleCursorAdapter adapter = new CustomSimpleCursorAdapter(
                MainActivity.this,R.layout.row,
                //android.R.layout.simple_list_item_1, // レイアウト
                cursor, // カーソル
                strs, // 表示するデータのカラム名
                ints,
                0); // 表示先のViewのID
        return adapter;
    }

    //別のアクティビティから戻ってきた用
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyApp", "OnActivityResult");

        //撮影用のcameraActivityか、項目編集用のeditListSelectViewActivityから戻ってきたら実行
        if(requestCode == cameraActivity || requestCode == editListSelectViewActivity) {
            if(resultCode == 1 || resultCode == 2){
                DatabaseAccessor dba = new DatabaseAccessor(this);
                dba.picShotOk(SekouKanriDB.getRowid());

                if(resultCode == 2) {
                    String pictureTargetName = SekouKanriDB.getCurrentBuilding() + "_" + SekouKanriDB.getCurrentFloor() + "_" + SekouKanriDB.getCurrentRoom() + "_" + SekouKanriDB.getCurrentTarget() + "_" + SekouKanriDB.getWhichBeforeAfter();
                    Intent intent = new Intent(MainActivity.this, PlateEditOnImageView.class);
                    intent.putExtra("pictureTargetName", pictureTargetName + ".jpg");
                    startActivityForResult(intent, 1);
                };
            }

            //リスト更新用
            //更新しないと撮影したり、編集した結果がわからないので
            //もっとうまいやり方があるはず
            Cursor cursor = helper.getReadableDatabase().rawQuery("select _id,target_name,before_after,picture from SekouKanriDB where building_name like '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name like '" + SekouKanriDB.getCurrentFloor() + "' and room_name like '" + SekouKanriDB.getCurrentRoom() + "'", null);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter( createCursorAdapter(cursor, new String[] {SekouKanriDB.getCurrentClass()},new int[] {R.id.text_Sample} ) );

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if( SekouKanriDB.getCurrentClass().equals("target_name") ) {
            menu.findItem(R.id.editList).setEnabled(true);
        } else {
            menu.findItem(R.id.editList).setEnabled(false);
        }
        return true;
    }

    //オプション画面押されたときの動作
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.editList) {
            Intent intent = new Intent(MainActivity.this, EditListSelectView.class);
            startActivityForResult(intent, editListSelectViewActivity);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
