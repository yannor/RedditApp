package hogent.reddit.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import hogent.reddit.database.DatabaseHandler;
import hogent.reddit.domain.SubReddit;

/**
 * Created by Yannick on 21/03/2017.
 */

public class TableSubReddits {

    private DatabaseHandler handler;

    //region Constants
    public static final String TABLE_SUBREDDITS = "SUBREDDITS";

    public static final String KEY_SUBREDDIT_ID = "ID";
    public static final String KEY_SUBREDDIT_NAAM = "NAAM";
    public static final String KEY_SUBREDDIT_OPENED = "OPENED";

    public static final String KEY_SUBREDDIT_SIZELIST = "SIZELIST";
    public static final String KEY_SUBREDDIT_FIRSTNAME = "FIRST";
    public static final String KEY_SUBREDDIT_POSITIONLIST = "POSITIONLIST";
    //endregion


    public TableSubReddits(DatabaseHandler handler) {
        this.handler = handler;
    }

    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_SUBREDDITS = "CREATE TABLE " + TABLE_SUBREDDITS + "("
                + KEY_SUBREDDIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + KEY_SUBREDDIT_NAAM + " TEXT NOT NULL UNIQUE, " + KEY_SUBREDDIT_OPENED + " INTEGER, " +
                KEY_SUBREDDIT_FIRSTNAME + " TEXT, " + KEY_SUBREDDIT_SIZELIST + " INTEGER, " + KEY_SUBREDDIT_POSITIONLIST + " INTEGER);";

        db.execSQL(CREATE_TABLE_SUBREDDITS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBREDDITS);
        onCreate(db);
    }


    public SubReddit getSubReddit(String naam) {
        SQLiteDatabase db = this.handler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SUBREDDITS, new String[]{KEY_SUBREDDIT_ID,
                        KEY_SUBREDDIT_NAAM, KEY_SUBREDDIT_OPENED, KEY_SUBREDDIT_FIRSTNAME, KEY_SUBREDDIT_SIZELIST, KEY_SUBREDDIT_POSITIONLIST}, KEY_SUBREDDIT_NAAM + "= ?",
                new String[]{String.valueOf(naam)}, null, null, null, null);
        SubReddit sub;
        if (cursor != null) {
            cursor.moveToFirst();
            sub = new SubReddit(cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5));
        } else {
            sub = null;
        }
        return sub;
    }

    public List<SubReddit> getAllSubReddits() {
        List<SubReddit> subList = new ArrayList<SubReddit>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SUBREDDITS;

        SQLiteDatabase db = this.handler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SubReddit sub = new SubReddit(cursor.getString(1));

                subList.add(sub);
            } while (cursor.moveToNext());
        }
        return subList;
    }

    public void addSubReddit(SubReddit sub) {
        SQLiteDatabase db = this.handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBREDDIT_NAAM, sub.getName());
        values.put(KEY_SUBREDDIT_OPENED, 0);

        // Inserting Row
        db.insert(TABLE_SUBREDDITS, null, values);

        db.close(); // Closing database connection
    }

    public void deleteSubReddit(String naam) {
        SQLiteDatabase db = this.handler.getWritableDatabase();

        db.execSQL("delete from " + TABLE_SUBREDDITS + " WHERE " + KEY_SUBREDDIT_NAAM + " = '" + naam + "'");

        db.close(); // Closing database connection
    }

    public void openSubFirstTime(String naam) {
        SQLiteDatabase db = this.handler.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_SUBREDDITS + " SET " + KEY_SUBREDDIT_OPENED + " = 1 WHERE " + KEY_SUBREDDIT_NAAM + " = '" + naam + "'");

        db.close(); // Closing database connection
    }

    public void updateListInfo(String name, String first, int sizeList, int positionList) {
        SQLiteDatabase db = this.handler.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_SUBREDDITS + " SET " + KEY_SUBREDDIT_FIRSTNAME + " = '" + first + "', " + KEY_SUBREDDIT_SIZELIST + " = '" + sizeList + "', " +
                KEY_SUBREDDIT_POSITIONLIST + " = '" + positionList + "' WHERE " + KEY_SUBREDDIT_NAAM + " = '" + name + "'");

        db.close(); // Closing database connection
    }

}
