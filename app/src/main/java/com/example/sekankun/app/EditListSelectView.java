package com.example.sekankun.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView;

public class EditListSelectView extends ActionBarActivity {
    int checkedItemPosition = 0;
    View tempView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list_select_view);

        final DatabaseOpenHelper helper = new DatabaseOpenHelper( this);
        final Cursor cursor = helper.getReadableDatabase().rawQuery(SekouKanriDB.getTempSqlQuery(),null);

        ListView listView = (ListView) findViewById(R.id.editListView);

        /*
        CustomSimpleCursorAdapter adapter = new CustomSimpleCursorAdapter(
                EditListSelectView.this,R.layout.row,
                //android.R.layout.simple_list_item_1, // レイアウト
                cursor, // カーソル
                new String[] {SekouKanriDB.getCurrentClass()}, // 表示するデータのカラム名
                new int[] {R.id.text_Sample},
                0);

        //listView.setMultiChoiceModeListener(new CallBack());
        SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(
                EditListSelectView.this,
                android.R.layout.simple_list_item_single_choice,
                cursor,
                new String[] {SekouKanriDB.getCurrentClass()},
                new int[] {android.R.id.text1}
        );
        */

        final CustomChoiceCursorAdapter adapter = new CustomChoiceCursorAdapter(
                EditListSelectView.this,R.layout.choice_row,
                //android.R.layout.simple_list_item_1, // レイアウト
                cursor, // カーソル
                new String[] {SekouKanriDB.getCurrentClass()}, // 表示するデータのカラム名
                new int[] {R.id.text_Sample},
                0); // 表示先のViewのID

        listView.setAdapter(adapter);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //リスト項目クリック時の処理
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                //adapter.notifyDataSetChanged();

                /*
                ListView listView = (ListView) parent;
                // クリックされたアイテムを取得します
                RadioButton item = (RadioButton) listView.getItemAtPosition(checkedItemPosition);
                RadioButton old = (RadioButton)item.findViewById(R.id.editChoiceRadioButton);
                old.setChecked(false);
                */

                //adapter.setChecked(false);
                if(tempView != null){
                    RadioButton oldChk = (RadioButton) tempView.findViewById(R.id.editChoiceRadioButton);
                    oldChk.setChecked(false);
                }

                Log.d("MyApp", "Radio...");
                RadioButton chk = (RadioButton) view.findViewById(R.id.editChoiceRadioButton);
                chk.setChecked(true);
                tempView = view;

                if(checkedItemPosition >= 0 ){
                    //Cursor rBtn = (Cursor)adapter.getItem(checkedItemPosition);
                    //rBtn.fi
                    //rBtn.setChecked(!rBtn.isChecked());
                }
                // チェックボックスを反転する（アダプタの onCheckedChanged() が呼ばれる）
                //chk.setChecked(!chk.isChecked());

                checkedItemPosition = position;
                //adapter.notifyDataSetChanged();
                //((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
            }
        });


        findViewById(R.id.listAddButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 選択アイテムを取得
                Intent intent = new Intent(EditListSelectView.this, EditListView.class);
                intent.putExtra("addFlag", true);
                intent.putExtra("editFlag", false);
                startActivityForResult(intent, 2);;
            }
        });

        findViewById(R.id.listEditButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 選択アイテムを取得
                ListView listView1 = (ListView)findViewById(R.id.editListView);
                SparseBooleanArray checked = listView1.getCheckedItemPositions();

                // チェックされたアイテムの文字列を生成
                // checked には、「チェックされているアイテム」ではなく、
                // 「一度でもチェックされたアイテム」が入ってくる。
                // なので、現在チェックされているかどうかを valutAt の戻り値
                // で判定する必要がある！！！
                StringBuilder sb = new StringBuilder();
                cursor.moveToFirst();
                for (int i=0; i<checked.size(); i++) {
                    if (checked.valueAt(i)) {
                        //行移動
                        //keyAtでチェックされたアイテムの位置がわかるので、それを使って、cursorをmoveする
                        cursor.moveToPosition(checked.keyAt(i));
                        Intent intent = new Intent(EditListSelectView.this, EditListView.class);
                        intent.putExtra("addFlag", false);
                        intent.putExtra("editFlag", true);
                        intent.putExtra("selectedID", cursor.getString(0));
                        startActivityForResult(intent, 2);;
                    }
                }
             }
        });

        findViewById(R.id.listDelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView listView1 = (ListView)findViewById(R.id.editListView);
                SparseBooleanArray checked = listView1.getCheckedItemPositions();
                cursor.moveToFirst();
                for (int i=0; i<checked.size(); i++) {
                    if (checked.valueAt(i)) {
                        //行移動
                        //keyAtでチェックされたアイテムの位置がわかるので、それを使って、cursorをmoveする
                        cursor.moveToPosition(checked.keyAt(i));

                        //ダイアログの表示
                        AlertDialog.Builder ad=new AlertDialog.Builder(EditListSelectView.this);
                        ad.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DatabaseAccessor da = new DatabaseAccessor(EditListSelectView.this);
                                da.delete( Integer.valueOf(cursor.getString(0)));

                                //setResult(1);
                                finish();
                            }
                        });
                        ad.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                        ad.setTitle("選択した項目を削除しますか？");
                        ad.create();
                        ad.show();

                    }
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyApp", "OnActivityResult");

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(requestCode == 2) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_list_select_view, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_edit_list_select_view, container, false);
            return rootView;
        }
    }

}
