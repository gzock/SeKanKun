package com.example.sekankun.app;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;
import java.math.*;

/**
 * Created by Gzock on 14/02/23.
 */
public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {
    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView textView2;
        public TextView textView3;
    }

    public CustomSimpleCursorAdapter(Context context, int layout, Cursor c,
                             String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v= inflater.inflate(R.layout.row,null,true);

        ViewHolder holder = new ViewHolder();

        holder.imageView = (ImageView) v.findViewById(R.id.icon_Sample);
        holder.textView = (TextView) v.findViewById(R.id.text_Sample);
        holder.textView2 = (TextView) v.findViewById(R.id.subListText);
        holder.textView3 = (TextView) v.findViewById(R.id.subListText2);


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
        Double flagOnNum2 = null;
        Double flagOffNum2 = null;

        Cursor perCursor = null;
        Cursor perCursor2 = null;

        //精度は別にいらないので、Doubleで
        //精度が欲しいならBigDecimal
        Long picPer = null;
        Long picPer2 = null;

        final DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
        //final DatabaseOpenHelper helper2 = new DatabaseOpenHelper(context);

        if(SekouKanriDB.getCurrentClass().equals("building_name")) {
            perCursor = helper.getReadableDatabase().rawQuery("select picture,count(*) from SekouKanriDB where building_name = '" + cursor.getString(1) + "' and before_after = '施工前' group by picture order by 1 desc",null);
            perCursor2 = helper.getReadableDatabase().rawQuery("select picture,count(*) from SekouKanriDB where building_name = '" + cursor.getString(1) + "' and before_after = '施工後' group by picture order by 1 desc",null);
        } else if(SekouKanriDB.getCurrentClass().equals("floor_name")) {
            perCursor = helper.getReadableDatabase().rawQuery("select picture,count(*) from SekouKanriDB where building_name = '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name = '" + cursor.getString(1) +  "' and before_after = '施工前' group by picture order by 1 desc",null);
            perCursor2 = helper.getReadableDatabase().rawQuery("select picture,count(*) from SekouKanriDB where building_name = '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name = '" + cursor.getString(1) +  "' and before_after = '施工後' group by picture order by 1 desc",null);
        } else if(SekouKanriDB.getCurrentClass().equals("room_name")) {
            perCursor = helper.getReadableDatabase().rawQuery("select picture,count(*) from SekouKanriDB where building_name = '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name = '" + SekouKanriDB.getCurrentFloor() + "' and room_name = '"  + cursor.getString(1) +  "' and before_after = '施工前' group by picture order by 1 desc",null);
            perCursor2 = helper.getReadableDatabase().rawQuery("select picture,count(*) from SekouKanriDB where building_name = '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name = '" + SekouKanriDB.getCurrentFloor() + "' and room_name = '"  + cursor.getString(1) +  "' and before_after = '施工後' group by picture order by 1 desc",null);
        }

        if(cursor.getColumnCount() == 2) {
            perCursor.moveToNext();
            perCursor2.moveToNext();

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
            } else {
                flagOnNum = 0.0;
                flagOffNum = 0.0;
            }

            if(perCursor2.getCount() == 1){
                if(perCursor2.getString(0).equals("0")){
                    flagOnNum2  = 0.0;
                    flagOffNum2 = Double.valueOf(perCursor2.getString(1));
                } else if (perCursor2.getString(0).equals("1")) {
                    flagOffNum2  = 0.0;
                    flagOnNum2 = Double.valueOf(perCursor2.getString(1));
                }

            } else if(perCursor2.getCount() == 2){
                flagOnNum2 = Double.valueOf(perCursor2.getString(1));
                perCursor2.moveToNext();
                flagOffNum2 = Double.valueOf(perCursor2.getString(1));
            } else {
                flagOnNum2 = 0.0;
                flagOffNum2 = 0.0;
            }


            picPer = Math.round( (flagOnNum / ( flagOnNum + flagOffNum)) * 100);
            picPer2 = Math.round( (flagOnNum2 / ( flagOnNum2 + flagOffNum2)) * 100);
            Log.d("MyApp", "Target -> " + cursor.getString(1));
            Log.d("MyApp", "Count -> " + perCursor.getCount());
            Log.d("MyApp","Percent -> " + picPer + "%");
            Log.d("MyApp", "String0 -> " + perCursor.getString(0));
            Log.d("MyApp", "String1 -> " + perCursor.getString(1));
            Log.d("MyApp", "flagOffNum -> " + flagOffNum);
            Log.d("MyApp", "flagOnNum  -> " + flagOnNum);

            str = cursor.getString(1);
            holder.textView.setText(str);
            holder.textView2.setText("施工前進捗率 : " + String.valueOf(picPer) + "%");
            holder.textView3.setText("施工後進捗率 : " + String.valueOf(picPer2) + "%");

        } else if(cursor.getColumnCount() == 4) {
            str = cursor.getString(1);
            str2 = cursor.getString(2);
            holder.textView.setText(str);
            holder.textView2.setText(str2);

            //カラム3には写真フラグが入ってるので、1で撮影済なら、画像を出力
            if(cursor.getString(3).equals("1")){
                holder.imageView.setImageResource(R.drawable.ok_m);
            }
        }

        // SQLiteのテーブルの"sample_row"という列のデータを取得してセット
        //String s = cursor.getString(1);
        Log.d("MyApp", "Custom -> " + str);
        Log.d("MyApp", "Custom2 -> " + str2);
        Log.d("MyApp", "textView -> " + holder.textView.getText());

        helper.close();


    }
}
