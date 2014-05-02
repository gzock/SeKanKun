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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class EditListView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list_view);

        Bundle extras;
        extras = getIntent().getExtras();

        final DatabaseOpenHelper helper = new DatabaseOpenHelper( this);

        if(extras.getBoolean("addFlag") == false && extras.getBoolean("editFlag") == true) {
            final Cursor cursor = helper.getReadableDatabase().rawQuery("select * from SekouKanriDB where _id = " + extras.getString("selectedID"), null);
            Log.d("MyApp", "SelectedID -> " + extras.get("selectedID"));
            cursor.moveToFirst();

            final EditText editText = (EditText) findViewById(R.id.editBuildingTextField);
            editText.setText(cursor.getString(1));
            final EditText editText2 = (EditText) findViewById(R.id.editFloorTextField);
            editText2.setText(cursor.getString(2));
            final EditText editText3 = (EditText) findViewById(R.id.editRoomTextField);
            editText3.setText(cursor.getString(3));
            final EditText editText4 = (EditText) findViewById(R.id.editTargetTextField);
            editText4.setText(cursor.getString(4));
            final EditText editText5 = (EditText) findViewById(R.id.editBeAfTextField);
            editText5.setText(cursor.getString(5));


            findViewById(R.id.editSubmitButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //ダイアログの表示
                    AlertDialog.Builder ad=new AlertDialog.Builder(EditListView.this);
                    ad.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DatabaseAccessor da = new DatabaseAccessor(EditListView.this);
                            da.editUpdate( Integer.valueOf( cursor.getString(0) ),editText.getText().toString(), editText2.getText().toString(), editText3.getText().toString(), editText4.getText().toString(), editText5.getText().toString() );
                            //setResult(1);
                            finish();
                        }
                    });
                    ad.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    ad.setTitle("以上の内容で項目を更新しますか？");
                    ad.create();
                    ad.show();
                }
            });
        } else if(extras.getBoolean("addFlag") == true && extras.getBoolean("editFlag") == false) {
            final EditText editText = (EditText) findViewById(R.id.editBuildingTextField);
            editText.setText(SekouKanriDB.getCurrentBuilding());
            final EditText editText2 = (EditText) findViewById(R.id.editFloorTextField);
            editText2.setText(SekouKanriDB.getCurrentFloor());
            final EditText editText3 = (EditText) findViewById(R.id.editRoomTextField);
            editText3.setText(SekouKanriDB.getCurrentRoom());
            final EditText editText4 = (EditText) findViewById(R.id.editTargetTextField);
            //editText4.setText(cursor.getString(4));
            final EditText editText5 = (EditText) findViewById(R.id.editBeAfTextField);
            //editText5.setText(cursor.getString(5));

            findViewById(R.id.editSubmitButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //ダイアログの表示
                    AlertDialog.Builder ad=new AlertDialog.Builder(EditListView.this);
                    ad.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if(editText4.getText().toString() != null && editText5.getText().toString() != null) {
                                DatabaseAccessor da = new DatabaseAccessor(EditListView.this);
                                da.addInsert( editText.getText().toString(), editText2.getText().toString(), editText3.getText().toString(), editText4.getText().toString(), editText5.getText().toString() );
                                //setResult(1);
                                finish();
                            } else {
                                Toast.makeText(EditListView.this, "入力が未完了です。", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    ad.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    ad.setTitle("以上の内容で項目を追加しますか？");
                    ad.create();
                    ad.show();
                }
            });
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
        getMenuInflater().inflate(R.menu.edit_list_view, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_edit_list_view, container, false);
            return rootView;
        }
    }

}
