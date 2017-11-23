package hogent.reddit.database;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Yannick on 18/08/2017.
 */

public class SessionManager {

    public static final int LOGGED_IN_USING_APP = 1;
    public static final int LOGGED_IN_USING_FB = 2;
    public static final int LOGGED_IN_USING_GOOGLE = 3;

    private static final String SESSION_NAME_PREF = "redditPrefs";

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private Context ctxt;

    private static final String BATCHLOAD = "batchLoad";
    private static final String FIRST_RUN = "firstRun";


    public SessionManager(Context context) {
        this.ctxt = context;
        pref = ctxt.getSharedPreferences(SESSION_NAME_PREF, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setBatch(int batch) {
        editor.putInt(BATCHLOAD, batch);
        editor.commit();
    }

    public int getBatch() {
        return pref.getInt(BATCHLOAD, 10);
    }

    public void firstRun() {
        editor.putBoolean(FIRST_RUN, false);
        editor.commit();
    }

    public boolean isFirstRun() {
        return pref.getBoolean(FIRST_RUN, true);
    }

}


