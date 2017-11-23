package hogent.reddit.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import hogent.reddit.database.DatabaseHandler;
import hogent.reddit.domain.Post;

/**
 * Created by Yannick on 17/08/2017.
 */

public class TableSavedPosts {


    private DatabaseHandler handler;

    public static final String TABLE_SAVED = "SAVED";

    public static final String KEY_SAVED_ID = "ID";
    public static final String KEY_SAVED_TITLE = "TITLE";
    public static final String KEY_SAVED_AUTHOR = "OPENED";

    public static final String KEY_SAVED_PICTURE = "PICTURE";
    public static final String KEY_SAVED_THUMBNAIL = "THUMBNAIL";
    public static final String KEY_SAVED_TEXT = "TEXT";

    public static final String KEY_SAVED_SUBREDDIT = "SUBREDDIT";
    public static final String KEY_SAVED_FULLNAME = "FULLNAME";

    public TableSavedPosts(DatabaseHandler handler) {
        this.handler = handler;
    }

    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_SAVED = "CREATE TABLE " + TABLE_SAVED + "("
                + KEY_SAVED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + KEY_SAVED_TITLE + " TEXT NOT NULL, " + KEY_SAVED_AUTHOR + " TEXT, " +
                KEY_SAVED_PICTURE + " TEXT, " + KEY_SAVED_THUMBNAIL + " TEXT, " + KEY_SAVED_TEXT + " TEXT, "
                + KEY_SAVED_SUBREDDIT + " TEXT NOT NULL , " + KEY_SAVED_FULLNAME + " TEXT UNIQUE NOT NULL);";

        db.execSQL(CREATE_TABLE_SAVED);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED);
        onCreate(db);
    }


    public Post getSavedPost(String fullName) {
        SQLiteDatabase db = this.handler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SAVED, new String[]{
                        KEY_SAVED_TITLE, KEY_SAVED_AUTHOR, KEY_SAVED_PICTURE, KEY_SAVED_THUMBNAIL, KEY_SAVED_TEXT, KEY_SAVED_SUBREDDIT, KEY_SAVED_FULLNAME}, KEY_SAVED_FULLNAME + "= ?",
                new String[]{String.valueOf(fullName)}, null, null, null, null);
        Post post;
        if (cursor != null) {
            cursor.moveToFirst();
            post = new Post(cursor.getString(1), cursor.getString(2), cursor.getString(4), cursor.getString(3), cursor.getString(5), cursor.getString(6), cursor.getString(7));
        } else {
            post = null;
        }
        return post;
    }

    public List<Post> getAllSavedPosts() {
        List<Post> savedPosts = new ArrayList<Post>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SAVED;

        SQLiteDatabase db = this.handler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Post p = new Post(cursor.getString(1), cursor.getString(2), cursor.getString(4), cursor.getString(3), cursor.getString(5), cursor.getString(6), cursor.getString(7));
                savedPosts.add(p);
            } while (cursor.moveToNext());
        }
        return savedPosts;
    }

    public void savePost(Post p) {
        SQLiteDatabase db = this.handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SAVED_TITLE, p.getTitle());
        values.put(KEY_SAVED_AUTHOR, p.getAuthor());
        values.put(KEY_SAVED_FULLNAME, p.getFullName());
        values.put(KEY_SAVED_PICTURE, p.getUrlPicture());
        values.put(KEY_SAVED_SUBREDDIT, p.getSubReddit());
        values.put(KEY_SAVED_TEXT, p.getText());
        values.put(KEY_SAVED_THUMBNAIL, p.getThumbnail());


        // Inserting Row
        int insertWorks = (int) db.insertWithOnConflict(TABLE_SAVED, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (insertWorks == -1) {
            db.update(TABLE_SAVED, values, KEY_SAVED_FULLNAME + "=?", new String[]{p.getFullName()});
        }
        db.close(); // Closing database connection


    }

    public void deleteSavedPost(String fullName) {
        SQLiteDatabase db = this.handler.getWritableDatabase();

        db.execSQL("delete from " + TABLE_SAVED + " WHERE " + KEY_SAVED_FULLNAME + " = '" + fullName + "'");

        db.close(); // Closing database connection
    }
}
