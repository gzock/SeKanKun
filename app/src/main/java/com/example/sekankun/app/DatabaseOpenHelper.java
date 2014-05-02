package com.example.sekankun.app;

/**
 * Created by Gzock on 14/02/17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Property;
import android.util.Log;

import java.util.Properties;
import android.widget.SimpleCursorAdapter;



/**
 * データベース処理クラス
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    // データベース名の定数
    private static final String DB_NAME = "SEKOUKANRI_DB";

    /**
     * 初期投入サンプルデータ
     */
    /*
    private String[][] datas = new String[][]{
            {"光度　人", "××商事", "開発部", "部長","141-0031", "品川区西五反田2-19-3", "090-1111-1111", "03-1111-1111", "code@shoji.co.jp"},
            {"高度　仁", "○○コミュニケーションズ", "開発部", "課長","141-0032", "品川区大崎1-2-1", "090-2222-2222", "03-2222-2222", "code@comunications.co.jp"},
            {"光度　陣", "×○システムズ", "開発部", null,"153-0043", "目黒区東山1-2-1", "090-3333-3333", "03-3333-3333", "code@systems.co.jp"},
            {"荒土　尋", "○×工務店", "開発部", null,"160-0014", "新宿区内藤町11-4", "090-4444-4444", "03-4444-4444", "code@koumuten.co.jp"}
    };
    */

    /**
     * コンストラクタ
     */
    public DatabaseOpenHelper(Context context) {
        // 指定したデータベース名が存在しない場合は、新たに作成されonCreate()が呼ばれる
        // バージョンを変更するとonUpgrade()が呼ばれる
        super(context, DB_NAME, null, 1);
    }

    /**
     * データベースの生成に呼び出されるので、 スキーマの生成を行う
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();

        try{
            // テーブルの生成
            StringBuilder createSql = new StringBuilder();
            createSql.append("create table " + SekouKanriDB.TABLE_NAME + " (");
            createSql.append(SekouKanriDB.COLUMN_ID + " integer primary key autoincrement not null,");
            createSql.append(SekouKanriDB.COLUMN_BUILDING_NAME + " text not null,");
            createSql.append(SekouKanriDB.COLUMN_FLOOR_NAME + " text,");
            createSql.append(SekouKanriDB.COLUMN_ROOM_NAME + " text,");
            createSql.append(SekouKanriDB.COLUMN_TARGET_NAME + " text,");
            createSql.append(SekouKanriDB.COLUMN_BEFORE_AFTER + " text,");
            createSql.append(SekouKanriDB.COLUMN_PICTURE + " integer,");
            createSql.append(SekouKanriDB.COLUMN_TYPE + " text,");
            createSql.append(SekouKanriDB.COLUMN_OTHER + " text");
            createSql.append(")");

            db.execSQL( createSql.toString());

            // サンプルデータの投入
            String[][] datas = SekouKanriDB.getDbData();
            for( String[] data: datas){
                ContentValues values = new ContentValues();
                values.put(SekouKanriDB.COLUMN_BUILDING_NAME, data[ 0]);
                values.put(SekouKanriDB.COLUMN_FLOOR_NAME, data[ 1]);
                values.put(SekouKanriDB.COLUMN_ROOM_NAME, data[ 2]);
                values.put(SekouKanriDB.COLUMN_TARGET_NAME, data[ 3]);
                values.put(SekouKanriDB.COLUMN_BEFORE_AFTER, data[ 4]);
                values.put(SekouKanriDB.COLUMN_PICTURE, data[ 5]);
                values.put(SekouKanriDB.COLUMN_TYPE, data[ 6]);
                db.insert(SekouKanriDB.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * データベースの更新
     *
     * 親クラスのコンストラクタに渡すversionを変更したときに呼び出される
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // データベースの更新
    }


    public String getUpdateQuery(String rcvStr, String flag) {
        String queryStr = null;

        //Android JavaのSwitchでは文字列指定禁止らしい・・・
        //なのでif-elseで代用
        //Switchのほうが絶対キレイだよねー
        if(SekouKanriDB.getCurrentClass().equals("building_name")) {
            if(flag.equals("up")){
                queryStr = "select _id,floor_name from SekouKanriDB where _id in (select min(_id) from SekouKanriDB where building_name like '" + rcvStr + "' group by floor_name)";
                SekouKanriDB.incCurrentClassNum();;
            } else if(flag.equals("down")){
            }
            SekouKanriDB.setCurrentBuilding(rcvStr);
            Log.d("MyApp", "Query -> " + queryStr);
            Log.d("MyApp", "CurrentBuilding -> " + SekouKanriDB.getCurrentBuilding());

        } else if(SekouKanriDB.getCurrentClass().equals("floor_name")) {
            if(flag.equals("up")){
                queryStr = "select _id,room_name from SekouKanriDB where _id in (select min(_id) from SekouKanriDB where building_name like '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name like '" + rcvStr + "' group by room_name)";
                //queryStr = "select _id,room_name from SekouKanriDB where building_name like '" + this.getCurrentBuilding() + "' and floor_name like '" + rcvStr + "'";
                SekouKanriDB.incCurrentClassNum();
            } else if(flag.equals("down")){
                queryStr = "select _id, building_name from SekouKanriDB where _id in (select min(_id) from SekouKanriDB group by building_name)";
                SekouKanriDB.decCurrentClassNum();
            }
            SekouKanriDB.setCurrentFloor(rcvStr);
            Log.d("MyApp", "Query -> " + queryStr);
            Log.d("MyApp", "CurrentBuilding -> " + SekouKanriDB.getCurrentBuilding());
            Log.d("MyApp", "CurrentFloor -> " + SekouKanriDB.getCurrentFloor());

        } else if(SekouKanriDB.getCurrentClass().equals("room_name")) {
            if(flag.equals("up")){
                queryStr = "select _id,target_name,before_after,picture from SekouKanriDB where building_name like '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name like '" + SekouKanriDB.getCurrentFloor() + "' and room_name like '" + rcvStr + "'";
                //queryStr = "select _id,target_name,before_after from SekouKanriDB where _id in (select min(_id) from SekouKanriDB where building_name like '" + this.getCurrentBuilding() +  "' and floor_name like '" + this.getCurrentFloor() + "' and room_name like '" + rcvStr + "' group by target_name)";
                SekouKanriDB.incCurrentClassNum();

            } else if(flag.equals("down")){
                //queryStr = "select _id,floor_name from SekouKanriDB where building_name like '" + this.getCurrentBuilding() + "'";
                queryStr = "select _id,floor_name from SekouKanriDB where _id in (select min(_id) from SekouKanriDB where building_name like '" + SekouKanriDB.getCurrentBuilding() + "' group by floor_name)";
                SekouKanriDB.decCurrentClassNum();
            }
            SekouKanriDB.setCurrentRoom( rcvStr );
            Log.d("MyApp", "Query -> " + queryStr);
            Log.d("MyApp", "CurrentBuilding -> " + SekouKanriDB.getCurrentBuilding());
            Log.d("MyApp", "CurrentFloor -> " + SekouKanriDB.getCurrentFloor());
            Log.d("MyApp", "CurrentRoom -> " + rcvStr);

        } else if(SekouKanriDB.getCurrentClass().equals("target_name")){
            queryStr = "select _id,room_name from SekouKanriDB where _id in (select min(_id) from SekouKanriDB where building_name like '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name like '" + SekouKanriDB.getCurrentFloor() + "' group by room_name)";
            //queryStr = "select _id,room_name from SekouKanriDB where building_name like '" + this.getCurrentBuilding() + "' and floor_name like '" + this.getCurrentFloor() + "'";
            SekouKanriDB.decCurrentClassNum();
            SekouKanriDB.setCurrentTarget( rcvStr );
            Log.d("MyApp", "Query -> " + queryStr);
            Log.d("MyApp", "CurrentBuilding -> " + SekouKanriDB.getCurrentBuilding());
            Log.d("MyApp", "CurrentFloor -> " + SekouKanriDB.getCurrentFloor());
            Log.d("MyApp", "CurrentRoom -> " + rcvStr);
        }

        return queryStr;
    }

    public String getSekouBeforeAfter() {
        String queryStr = null;
        queryStr = "select _id,before from SekouKanriDB where _id in (select min(_id) from SekouKanriDB where building_name like '" + SekouKanriDB.getCurrentBuilding() + "' and floor_name like '" + SekouKanriDB.getCurrentFloor() + "' group by room_name)";
        return queryStr;
    }

}