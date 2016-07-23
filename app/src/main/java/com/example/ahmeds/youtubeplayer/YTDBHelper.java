package com.example.ahmeds.youtubeplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 100488982 on 11/21/2015.
 */
public class YTDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "videosDB.db";
    public static final String TABLE_VIDEOS = "videos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_EMBED = "embed";
    public static final String COLUMN_IMAGE = "image";

    public static List<YTVideo> productList=new ArrayList<YTVideo>();

    //We need to pass database information along to superclass
    public YTDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_VIDEOS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_EMBED + " TEXT, " +
                COLUMN_IMAGE + " BLOB " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
        onCreate(db);
    }

    //Add a new row to the database
    public void addVideo(YTVideo video){
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, video.get_title());
        values.put(COLUMN_AUTHOR, video.get_author());
        values.put(COLUMN_EMBED, video.get_embed());
        values.put(COLUMN_IMAGE, video.get_image());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_VIDEOS, null, values);
        db.close();
    }

    //Delete a row from the database
    public void deleteVideo(String vidName){
        if(vidName!="" && vidName!=null) {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM " + TABLE_VIDEOS + " WHERE " + COLUMN_TITLE + "=\"" + vidName + "\";");
        }
    }

    //Convert db to listview
    public List<YTVideo> getVideos(){
        List<YTVideo> prodList = new ArrayList<YTVideo>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_VIDEOS + " WHERE 1";

        //Cursor points to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        //Position after the last row means the end of the results
        while (!c.isAfterLast()) {
            prodList.add(new YTVideo(c.getString(c.getColumnIndex(COLUMN_TITLE)),c.getString(c.getColumnIndex(COLUMN_AUTHOR)),c.getString(c.getColumnIndex(COLUMN_EMBED)),c.getBlob(c.getColumnIndex(COLUMN_IMAGE))));
            c.moveToNext();
        }
        db.close();
        return prodList;
    }


}