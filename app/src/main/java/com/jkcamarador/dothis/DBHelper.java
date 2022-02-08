package com.jkcamarador.dothis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "DoThisDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        //DB.execSQL("create Table Userdetails(name TEXT primary key, contact TEXT, dob TEXT)");
        DB.execSQL("create Table dothis_tasks(task_id INTEGER PRIMARY KEY AUTOINCREMENT, task_name TEXT,  task_dl TEXT, task_diff INTEGER,  task_note TEXT, task_weight DOUBLE, task_icon INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists dothis_tasks");
    }

    public void deleteAllTask() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("drop Table if exists dothis_tasks");
        DB.execSQL("create Table dothis_tasks(task_id INTEGER PRIMARY KEY AUTOINCREMENT, task_name TEXT,  task_dl TEXT, task_diff INTEGER,  task_note TEXT, task_weight DOUBLE, task_icon INTEGER);");
    }

    public Boolean insertTaskData(String task_name, String task_dl, int task_diff, String task_note, double task_weight, int task_icon) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task_name", task_name);
        contentValues.put("task_dl", task_dl);
        contentValues.put("task_diff", task_diff);
        contentValues.put("task_note", task_note);
        contentValues.put("task_weight", task_weight);
        contentValues.put("task_icon", task_icon);
        long result=DB.insert("dothis_tasks", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean updateTaskData(int task_id, String task_name, String task_dl, int task_diff, String task_note, double task_weight, int task_icon) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task_name", task_name);
        contentValues.put("task_dl", task_dl);
        contentValues.put("task_diff", task_diff);
        contentValues.put("task_note", task_note);
        contentValues.put("task_weight", task_weight);
        contentValues.put("task_icon", task_icon);
        Cursor cursor = DB.rawQuery("Select * from dothis_tasks where task_id = ?", new String[]{String.valueOf(task_id)});
        if (cursor.getCount() > 0) {
            long result = DB.update("dothis_tasks", contentValues, "task_id=?", new String[]{String.valueOf(task_id)});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }}

    public Boolean deleteTaskData(int task_id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from dothis_tasks where task_id = ?", new String[]{String.valueOf(task_id)});
        if (cursor.getCount() > 0) {
            long result = DB.delete("dothis_tasks", "task_id=?", new String[]{String.valueOf(task_id)});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }

    public Cursor getTaskData () {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from dothis_tasks", null);
        return cursor;

    }

    public Cursor searchTaskData (int task_id) {
        SQLiteDatabase DB = this.getWritableDatabase();

        //Cursor cursor = DB.rawQuery("Select * from dothis_tasks", null);


        Cursor cursor = DB.rawQuery("SELECT * FROM dothis_tasks WHERE task_id ="+task_id,null);

        return cursor;

    }

    public void updateWeightTaskData(int task_id, String task_name, String task_dl, int task_diff, String task_note, double task_weight, int task_icon) {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("UPDATE dothis_tasks SET task_name='"+String.valueOf(task_name)+"', task_dl='"+String.valueOf(task_dl)+
                "', task_diff='"+String.valueOf(task_diff)+"', task_note='"+String.valueOf(task_note)+
                "', task_weight='"+String.valueOf(task_weight)+
                "', task_icon='"+String.valueOf(task_icon)+
                "'WHERE task_id='"+Integer.parseInt(String.valueOf(task_id))+"'");
    }

    public void checkExpiredTask(int task_id) {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("SELECT * FROM dothis_tasks WHERE task_id ="+task_id,null);

        //cursor = db.rawQuery("SELECT * FROM dothis_tasks WHERE task_id ="+Integer.parseInt(listOfTaskIDExpired.get(ctr)),null);
        if(cursor.moveToFirst()){
            DB.execSQL("DELETE FROM dothis_tasks WHERE task_id ="+task_id);
        }
    }

    public Cursor taskWeightSortingAlgorithm(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM dothis_tasks order by task_weight",null);

        return cursor;
    }
}

