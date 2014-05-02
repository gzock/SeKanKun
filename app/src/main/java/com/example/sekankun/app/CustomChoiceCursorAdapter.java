package com.example.sekankun.app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Gzock on 14/03/15.
 */
public class CustomChoiceCursorAdapter extends SimpleCursorAdapter {
    static class ViewHolder {
        public RadioButton choiceRadio;
        public TextView textView;
        public TextView textView2;
        public RadioButton mRadioButton;

    }
    //private RadioButton mRadioButton;
    private Integer choiceIndex = 0;

    /*
    @Override
    public void setChecked(boolean checked) {
    //    mRadioButton.setChecked(checked);
    }
    @Override
    public boolean isChecked() {
    //    return mRadioButton.isChecked();
    }
    @Override
    public void toggle() {
    }
    */


    public CustomChoiceCursorAdapter(Context context, int layout, Cursor c,
                                     String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v= inflater.inflate(R.layout.choice_row,null,true);

        ViewHolder holder = new ViewHolder();

        //holder.choiceRadio = (RadioButton) v.findViewById(R.id.editChoiceRadioButton);
        holder.textView = (TextView) v.findViewById(R.id.text_Sample);
        holder.textView2 = (TextView) v.findViewById(R.id.subListText);
        holder.mRadioButton = (RadioButton) v.findViewById(R.id.editChoiceRadioButton);


        v.setTag(holder);

        return v;
    }

    //newViewで作ったやつが渡ってくるらしい
    //targetでDBのflagもらってきて、getStringして、ifで振り分けて、imageViewに入れるか？
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        String str = null;
        String str2 = null;

        Double flagOnNum = null;
        Double flagOffNum = null;

        Cursor perCursor = null;



        //精度は別にいらないので、Doubleで
        //精度が欲しいならBigDecimal
        Long picPer = null;

        final DatabaseOpenHelper helper = new DatabaseOpenHelper(context);

        if(SekouKanriDB.getCurrentClass().equals("building_name")) {
            perCursor = helper.getReadableDatabase().rawQuery("select picture,count(*) from SekouKanriDB where building_name = '" + cursor.getString(1) + "' group by picture order by 1 desc",null);
        } else if(SekouKanriDB.getCurrentClass().equals("floor_name")) {
            perCursor = helper.getReadableDatabase().rawQuery("select picture,count(*) from SekouKanriDB where building_name = '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name = '" + cursor.getString(1) + "' group by picture order by 1 desc", null);
        } else if(SekouKanriDB.getCurrentClass().equals("room_name")) {
            perCursor = helper.getReadableDatabase().rawQuery("select picture,count(*) from SekouKanriDB where building_name = '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name = '" + SekouKanriDB.getCurrentFloor() + "' and room_name = '"  + cursor.getString(1) +  "' group by picture order by 1 desc",null);
        }

        if(cursor.getColumnCount() == 2) {
            perCursor.moveToNext();

            if(perCursor.getCount() == 1){
                if(perCursor.getString(0).equals("0")){
                    flagOnNum  = 0.0;
                    flagOffNum = Double.valueOf(perCursor.getString(1));
                } else if (perCursor.getString(0).equals("1")) {
                    flagOffNum  = 0.0;
                    flagOnNum = Double.valueOf(perCursor.getString(1));
                }

            } else if(perCursor.getCount() == 2){
                flagOnNum = Double.valueOf(perCursor.getString(1));
                perCursor.moveToNext();
                flagOffNum = Double.valueOf(perCursor.getString(1));
            }
            picPer = Math.round( (flagOnNum / ( flagOnNum + flagOffNum)) * 100);
            Log.d("MyApp", "Target -> " + cursor.getString(1));
            Log.d("MyApp", "Count -> " + perCursor.getCount());
            Log.d("MyApp","Percent -> " + picPer + "%");
            Log.d("MyApp", "String0 -> " + perCursor.getString(0));
            Log.d("MyApp", "String1 -> " + perCursor.getString(1));
            Log.d("MyApp", "flagOffNum -> " + flagOffNum);
            Log.d("MyApp", "flagOnNum  -> " + flagOnNum);

            str = cursor.getString(1);
            holder.textView.setText(str);
            holder.textView2.setText("進捗率 : " + String.valueOf(picPer) + "%");

        } else if(cursor.getColumnCount() == 4) {
            str = cursor.getString(1);
            str2 = cursor.getString(2);
            holder.textView.setText(str);
            holder.textView2.setText(str2);

            //カラム3には写真フラグが入ってるので、1で撮影済なら、画像を出力
            if(cursor.getString(3).equals("1")){
                //holder.imageView.setImageResource(R.drawable.ok_m);
            }
        }

        // SQLiteのテーブルの"sample_row"という列のデータを取得してセット
        //String s = cursor.getString(1);
        Log.d("MyApp", "Custom -> " + str);
        Log.d("MyApp", "Custom2 -> " + str2);
        Log.d("MyApp", "textView -> " + holder.textView.getText());
        holder.mRadioButton.setChecked(false);

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // viewのセットなどはスーパークラスのメソッドに任せる
        View view = super.getView(position, convertView, parent);

            /*
             * それぞれのTextViewにpositionTagと
             * MyListViewのlistenerをつける
             */

        Log.d("MyApp","Postion -> " + position);
        //mRadioButton = (RadioButton) view.findViewById(R.id.editChoiceRadioButton);
        /*
        mRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //changeChoice(item,position);  // 下に記述した共通メソッド
                Log.d("MyApp", "RadioChoiceChanged...");
                Log.d("MyApp","Postion -> " + position);
                Log.d("MyApp","ChoiceIndex -> " + choiceIndex);
                mRadioButton.setChecked(position == choiceIndex);
                choiceIndex = position;
                //mRadioButton.setChecked(!mRadioButton.isChecked());
            }
        });
        */

        /*
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mRadioButton = (RadioButton) v.findViewById(R.id.editChoiceRadioButton);
                Log.d("MyApp", "ViewChoiceChanged...");
                Log.d("MyApp","Postion -> " + position);
                Log.d("MyApp","ChoiceIndex -> " + choiceIndex);
                mRadioButton.setChecked(position == choiceIndex);
                //mRadioButton.setChecked((!mRadioButton.isChecked()));
                choiceIndex = position;
                //mRadioButton.setChecked(!mRadioButton.isChecked());
            }
        });
        */


        return view;
    }

}
