package hogent.reddit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import hogent.reddit.database.tables.TableSavedPosts;
import hogent.reddit.database.tables.TableSubReddits;
import hogent.reddit.domain.Post;
import hogent.reddit.domain.SubReddit;

/**
 * Created by Yannick on 21/03/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // DatabaseHandler Version
    public static final int DATABASE_VERSION = 7;

    // DatabaseHandler Name
    public static final String DATABASE_NAME = "redditRest";


    private TableSubReddits tableSubReddits;
    private TableSavedPosts tableSaved;


    public DatabaseHandler(Context context) {
        super(context, DatabaseHandler.DATABASE_NAME, null, DatabaseHandler.DATABASE_VERSION);
        tableSubReddits = new TableSubReddits(this);
        tableSaved = new TableSavedPosts(this);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        tableSubReddits.onCreate(db);
        tableSaved.onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        tableSubReddits.onUpgrade(db, oldVersion, newVersion);
        tableSaved.onUpgrade(db, oldVersion, newVersion);
    }

    public void empty() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 0, 0);
    }


//region SubReddist handling

    public List<SubReddit> getAllSubReddits() {
        return tableSubReddits.getAllSubReddits();
    }

    public SubReddit getSubReddit(String name) {
        return tableSubReddits.getSubReddit(name);
    }

    public void addSubReddit(SubReddit subReddit) {
        tableSubReddits.addSubReddit(subReddit);
    }


    public void deleteSubReddit(String name) {
        tableSubReddits.deleteSubReddit(name);
    }

    public void openSubRedditFirstTime(String name) {
        tableSubReddits.openSubFirstTime(name);
    }

    public void updateListInfo(String name, String first, int sizeList, int positionList) {
        tableSubReddits.updateListInfo(name, first, sizeList, positionList);
    }

    //endregion

    //region Saved posts handling

    public List<Post> getAllSavedPosts() {
        return tableSaved.getAllSavedPosts();
    }

    public Post getSavedPost(String fullName) {
        return tableSaved.getSavedPost(fullName);
    }

    public void savePost(Post p) {
        tableSaved.savePost(p);
    }

    public void deleteSavedPost(String fullName) {
        tableSaved.deleteSavedPost(fullName);
    }
    //endregion


}
