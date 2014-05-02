package com.example.sekankun.app;

import java.io.Serializable;

/**
 * Created by Gzock on 14/02/17.
 */
@SuppressWarnings("serial")
public class SekouKanriDB {
    // テーブル名
    public static final String TABLE_NAME = "SekouKanriDB";

    // カラム名
    public static final String COLUMN_ID                = "_id";
    public static final String COLUMN_BUILDING_NAME     = "building_name";
    public static final String COLUMN_FLOOR_NAME        = "floor_name";
    public static final String COLUMN_ROOM_NAME         = "room_name";
    public static final String COLUMN_TARGET_NAME       = "target_name";
    public static final String COLUMN_BEFORE_AFTER      = "before_after";
    public static final String COLUMN_PICTURE           = "picture";
    public static final String COLUMN_TYPE              = "type";
    public static final String COLUMN_OTHER             = "other";

    // プロパティ
    private static Integer rowid = null;
    public static void setRowid(Integer id){rowid = id; }
    public static Integer getRowid() { return rowid; }

    private static String[][] dbData;
    public static void setDbData(String[][] strs) { dbData = strs; }
    public static String[][] getDbData() { return dbData; }

    private static String[] currentClass = {"building_name", "floor_name", "room_name", "target_name"};
    private static Integer currentClassNum = 0;
    public static void incCurrentClassNum() { currentClassNum++; }
    public static void decCurrentClassNum() { currentClassNum--; }
    public static String getCurrentClass() { return currentClass[ currentClassNum ]; }

    private static String currentBuilding;
    public static void setCurrentBuilding(String str) { currentBuilding = str; }
    public static String getCurrentBuilding() { return currentBuilding; }

    private static String currentFloor;
    public static void setCurrentFloor(String str) { currentFloor = str; }
    public static String getCurrentFloor() { return currentFloor; }

    private static String currentRoom;
    public static void setCurrentRoom(String str) { currentRoom = str; }
    public static String getCurrentRoom() { return currentRoom; }

    private static String currentTarget;
    public static void setCurrentTarget(String str) { currentTarget = str; }
    public static String getCurrentTarget() { return currentTarget; }

    private static String whichBeforeAfter;
    public static void setWhichBeforeAfter(String str) { whichBeforeAfter = str; }
    public static String getWhichBeforeAfter() { return whichBeforeAfter; }

    private static String tempSqlQuery;
    public static void setTempSqlQuery(String str) { tempSqlQuery = str; }
    public static String getTempSqlQuery() { return tempSqlQuery; }

    /**
     * ListView表示の際に利用するのでユーザ名+会社名を返す
     */
    /*
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( getPersonName());
        if( getCompanyName() != null){
            builder.append(":");
            builder.append(getCompanyName());
        }
        return builder.toString();
    }
    */
}
