package com.mssinfotech.itav;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

   public static final String DATABASE_NAME = "MyDBName.db";
   public static final String LOGIN_TABLE = "login";
   public static final String LANG_TABLE = "language";
   public static final String ID = "id";
   public static final String LANG = "language";
   public static final String USERNAME = "username";
   public static final String PASSWORD = "password";
   public static final String AVATAR = "avatar";
   public static final String FULLNAME = "fullname";
   public DBHelper(Context context)
   {
      super(context, DATABASE_NAME , null, 1);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      // TODO Auto-generated method stub
      db.execSQL("CREATE TABLE IF NOT EXISTS " + LOGIN_TABLE + "(id text,username text,password text,avatar text,fullname text)");
      db.execSQL("CREATE TABLE IF NOT EXISTS " + LANG_TABLE + "(id text,language text)");
      Log.d("create", "table ceated");
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // TODO Auto-generated method stub
      db.execSQL("DROP TABLE IF EXISTS "+LOGIN_TABLE);
      onCreate(db);
      Log.d("create", "table droped");
      onCreate(db);
   }
   public void ChangeLanguage(){
      SQLiteDatabase db = this.getWritableDatabase();
      db.execSQL("DROP TABLE IF EXISTS "+LANG_TABLE);
      onCreate(db);
      Log.d("create", "table droped");
      onCreate(db);
   }
   public boolean insertLang(String id,String lang){
      SQLiteDatabase db = this.getWritableDatabase();
      db.execSQL("DROP TABLE IF EXISTS " + LANG_TABLE);
      onCreate(db);
      ContentValues contentValues = new ContentValues();
      contentValues.put(ID, id);
      contentValues.put(LANG, lang);
      db.insert(LANG_TABLE, null, contentValues);
      Log.d("Inserted", "Record LANG Inserted");
      return true;
   }
   public boolean insertUser(String id, String username, String password, String avatar, String fullname) {
      SQLiteDatabase db = this.getWritableDatabase();
      db.execSQL("DROP TABLE IF EXISTS " + LOGIN_TABLE);
      onCreate(db);
      ContentValues contentValues = new ContentValues();
      contentValues.put(ID, id);
      contentValues.put(USERNAME, username);
      contentValues.put(PASSWORD, password);
      contentValues.put(AVATAR, avatar);
      contentValues.put(FULLNAME, fullname);
      db.insert(LOGIN_TABLE, null, contentValues);
      Log.d("Inserted", "Record TABLE_NAME Inserted id:"+id+"\nuser:"+username+"\npass:"+password+"\navatar:"+avatar+"\nfullname:"+fullname);
      return true;
   }
   public String getLang(){
      String s="";
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select "+LANG+" from "+ LANG_TABLE , null);
      if (res != null) {
         res.moveToNext();
         Log.d("select", "record fetch");
         s = String.valueOf(res.getString(0));
      }
      return  s;
   }
   public String getId(){
     String s="";
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from "+ LOGIN_TABLE , null);
      if (res != null) {
         res.moveToNext();
         Log.d("select", "record fetch");
         s = String.valueOf(res.getString(0));
      }
      return  s;
   }

   public void deleteAll()
   {
      SQLiteDatabase db = this.getWritableDatabase();
      db.execSQL("DELETE FROM "+LOGIN_TABLE);
      db.close();
   }
   public Cursor getMyData(){
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from "+LOGIN_TABLE, null );
      Log.d("select", "record fetch");
      return res;
   }
   public Cursor getUserData(int id){
      SQLiteDatabase db = this.getReadableDatabase();
      onCreate(db);
      Cursor res =  db.rawQuery( "select * from "+LOGIN_TABLE+" where id="+id+"", null );
      return res;
   }

   public int updateUserData(String key,String value){
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "update "+LOGIN_TABLE + " set " + key + "='"+value+"'" , null );
      Log.d("update", "record updated");
      return 1;
   }
   public int numberOfRowsLang(){
      SQLiteDatabase db = this.getReadableDatabase();
      onCreate(db);
      int numRows = (int) DatabaseUtils.queryNumEntries(db, LANG_TABLE);
      return numRows;
   }
   public int numberOfRows(){
      SQLiteDatabase db = this.getReadableDatabase();
      onCreate(db);
      int numRows = (int) DatabaseUtils.queryNumEntries(db, LOGIN_TABLE);
      return numRows;
   }

   public void deleteAllUser(){
      SQLiteDatabase db = this.getWritableDatabase();
      db.execSQL("DROP TABLE IF EXISTS "+LOGIN_TABLE);
      onCreate(db);
   }
   public Integer deleteUser (Integer id) {
      SQLiteDatabase db = this.getWritableDatabase();
      return db.delete(LOGIN_TABLE,
      "id = ? ", 
      new String[] { Integer.toString(id) });
   }
}