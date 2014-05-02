package com.example.sekankun.app;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Gzock on 14/02/25.
 */
public class DatabaseAccessor {

    private DatabaseOpenHelper helper = null;

    public DatabaseAccessor(Context context) {
        helper = new DatabaseOpenHelper(context);
    }

    /**
     * SekouKanriDBの保存
     * rowidがnullの場合はinsert、rowidが!nullの場合はupdate
     * @param SekouKanriDB 保存対象のオブジェクト
     * @return 保存結果
     */
    public void picShotOk( Integer rowId){
        SQLiteDatabase db = helper.getWritableDatabase();
        SekouKanriDB result = null;
        try {
            ContentValues values = new ContentValues();
            //values.put( SekouKanriDB.COLUMN_ID, SekouKanriDB.getPersonName());
            //values.put( SekouKanriDB.COLUMN_BUILDING_NAME, SekouKanriDB.getCompanyName());
            //values.put( SekouKanriDB.COLUMN_FLOOR_NAME, SekouKanriDB.getOrganizationName());
            //values.put( SekouKanriDB.COLUMN_ROOM_NAME, SekouKanriDB.getPositionName());
            //values.put( SekouKanriDB.COLUMN_TARGET_NAME, SekouKanriDB.getZipCode());
            //values.put( SekouKanriDB.COLUMN_BEFORE_AFTER, SekouKanriDB.getAddress());
            values.put( SekouKanriDB.COLUMN_PICTURE, 1);
            //values.put( SekouKanriDB.COLUMN_TYPE, SekouKanriDB.getTel2());
            //values.put( SekouKanriDB.COLUMN_OTHER, SekouKanriDB.getMail());

            Log.d("DB", "RowID -> " + rowId);
            db.update( SekouKanriDB.TABLE_NAME, values, SekouKanriDB.COLUMN_ID + "=" + rowId, null);
            db.close();

        } catch (Exception e){
            Log.d("MyApp", e.toString());
        }
        return;
    }

    public void addInsert( String building_name, String floor_name, String room_name, String target_name, String beforeAfter ){
        SQLiteDatabase db = helper.getWritableDatabase();
        SekouKanriDB result = null;
        try {
            ContentValues values = new ContentValues();
            values.put( SekouKanriDB.COLUMN_BUILDING_NAME, building_name);
            values.put( SekouKanriDB.COLUMN_FLOOR_NAME, floor_name);
            values.put( SekouKanriDB.COLUMN_ROOM_NAME, room_name);
            values.put( SekouKanriDB.COLUMN_TARGET_NAME, target_name);
            values.put( SekouKanriDB.COLUMN_BEFORE_AFTER, beforeAfter);
            values.put( SekouKanriDB.COLUMN_PICTURE, beforeAfter);
            //vlues.put( SekouKanraiDB.COLUMN_PICTURE, 1);
            //values.put( SekouKanriDB.COLUMN_TYPE, SekouKanriDB.getTel2());
            //values.put( SekouKanriDB.COLUMN_OTHER, SekouKanriDB.getMail());

            db.insert(SekouKanriDB.TABLE_NAME, null, values);

        } catch (Exception e){
            Log.d("MyApp", e.toString());
        } finally {
            db.close();
        }
        return;
    }

    public void editUpdate( Integer rowId, String building_name, String floor_name, String room_name, String target_name, String beforeAfter){
        SQLiteDatabase db = helper.getWritableDatabase();
        SekouKanriDB result = null;
        try {
            ContentValues values = new ContentValues();
            //values.put( SekouKanriDB.COLUMN_ID, SekouKanriDB.getPersonName());
            values.put( SekouKanriDB.COLUMN_BUILDING_NAME, building_name);
            values.put( SekouKanriDB.COLUMN_FLOOR_NAME, floor_name);
            values.put( SekouKanriDB.COLUMN_ROOM_NAME, room_name);
            values.put( SekouKanriDB.COLUMN_TARGET_NAME, target_name);
            values.put( SekouKanriDB.COLUMN_BEFORE_AFTER, beforeAfter);
            //values.put( SekouKanriDB.COLUMN_PICTURE, 1);
            //values.put( SekouKanriDB.COLUMN_TYPE, SekouKanriDB.getTel2());
            //values.put( SekouKanriDB.COLUMN_OTHER, SekouKanriDB.getMail());

            Log.d("DB", "RowID -> " + rowId);
            db.update( SekouKanriDB.TABLE_NAME, values, SekouKanriDB.COLUMN_ID + "=" + rowId, null);

        } catch (Exception e){
            Log.d("MyApp", e.toString());
        } finally {
            db.close();
        }
        return;
    }

    public void delete( Integer rowId){
        SQLiteDatabase db = helper.getWritableDatabase();
        SekouKanriDB result = null;
        try {
            ContentValues values = new ContentValues();

            Log.d("DB", "RowID -> " + rowId);
            db.delete( SekouKanriDB.TABLE_NAME, SekouKanriDB.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)});

        } catch (Exception e){
            Log.d("MyApp", e.toString());
        } finally {
            db.close();
        }
        return;
    }

    /**
     * レコードの削除
     * @param SekouKanriDB 削除対象のオブジェクト
     */
    /*
    public void delete(SekouKanriDB SekouKanriDB) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete( SekouKanriDB.TABLE_NAME, SekouKanriDB.COLUMN_ID + "=?", new String[]{ String.valueOf( SekouKanriDB.getRowid())});
        } finally {
            db.close();
        }
    }
    */

    /**
     * idでSekouKanriDBをロードする
     * @param rowId PK
     * @return ロード結果
     */
    /*
    public SekouKanriDB load(Long rowId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        SekouKanriDB SekouKanriDB = null;
        try {
            Cursor cursor = db.query( SekouKanriDB.TABLE_NAME, null, SekouKanriDB.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)}, null, null, null);
            cursor.moveToFirst();
            SekouKanriDB = getSekouKanriDB( cursor);
        } finally {
            db.close();
        }
        return SekouKanriDB;
    }
    */

    /**
     * 一覧を取得する
     * @return 検索結果
     */
    /*
    public List<SekouKanriDB> list() {
        SQLiteDatabase db = helper.getReadableDatabase();

        List<SekouKanriDB> SekouKanriDBList;
        try {
            Cursor cursor = db.query( SekouKanriDB.TABLE_NAME, null, null, null, null, null, SekouKanriDB.COLUMN_ID);
            SekouKanriDBList = new ArrayList<SekouKanriDB>();
            cursor.moveToFirst();
            while( !cursor.isAfterLast()){
                SekouKanriDBList.add( getSekouKanriDB( cursor));
                cursor.moveToNext();
            }
        } finally {
            db.close();
        }
        return SekouKanriDBList;
    }
    */

    /**
     * カーソルからオブジェクトへの変換
     * @param cursor カーソル
     * @return 変換結果
     */

    /*
    private SekouKanriDB getSekouKanriDB( Cursor cursor){
        SekouKanriDB SekouKanriDB = new SekouKanriDB();

        SekouKanriDB.setRowid( cursor.getLong(0));
        SekouKanriDB.setPersonName( cursor.getString(1));
        SekouKanriDB.setCompanyName( cursor.getString(2));
        SekouKanriDB.setOrganizationName( cursor.getString(3));
        SekouKanriDB.setPositionName( cursor.getString(4));
        SekouKanriDB.setZipCode( cursor.getString(5));
        SekouKanriDB.setAddress( cursor.getString(6));
        SekouKanriDB.setTel1( cursor.getString(7));
        SekouKanriDB.setTel2( cursor.getString(8));
        SekouKanriDB.setMail( cursor.getString(9));
        return SekouKanriDB;
    }
    */

}
