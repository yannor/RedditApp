package hogent.reddit.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import hogent.reddit.R;
import hogent.reddit.activities.fragments.Fragment_Detail;
import hogent.reddit.activities.fragments.Fragment_Posts;
import hogent.reddit.activities.fragments.Fragment_SavedPosts;
import hogent.reddit.activities.fragments.Fragment_Settings;
import hogent.reddit.activities.fragments.Fragment_SubReddits;
import hogent.reddit.adapters.PostsAdapter;
import hogent.reddit.domain.Post;
import hogent.reddit.utils.PostsHandler;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private List<Post> listPosts;
    private PostsHandler postsHandler;

    private Fragment frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        this.frag = new Fragment_Posts();
        this.postsHandler = new PostsHandler(this);
        this.listPosts = postsHandler.getListPosts();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

        FrameLayout content = (FrameLayout) findViewById(R.id.main_content);
        getFragmentManager().beginTransaction().replace(R.id.main_content, new Fragment_SubReddits()).commit();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_saved) {
            openSavedPosts();
        }

        if (id == R.id.nav_settings) {
            openSettings();
        }

        if (id == R.id.nav_subreddits) {
            openSubreddits();
        }
        /**else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

         }**/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void openSubreddits() {
        navigationView.getMenu().getItem(0).setChecked(true);
        FrameLayout content = (FrameLayout) findViewById(R.id.main_content);
        getFragmentManager().beginTransaction().replace(R.id.main_content, new Fragment_SubReddits()).commit();
    }

    public void openSavedPosts() {
        navigationView.getMenu().getItem(1).setChecked(true);
        FrameLayout content = (FrameLayout) findViewById(R.id.main_content);

        getFragmentManager().beginTransaction().replace(R.id.main_content, new Fragment_SavedPosts()).commit();
    }

    public void openSettings() {
        navigationView.getMenu().getItem(2).setChecked(true);
        FrameLayout content = (FrameLayout) findViewById(R.id.main_content);
        getFragmentManager().beginTransaction().replace(R.id.main_content, new Fragment_Settings()).commit();
    }


    public void openPost(Post post, int pos, boolean backStack) {

        Class fragClass = Fragment_Detail.class;

        try {
            Fragment frag = (Fragment) fragClass.newInstance();
            Bundle args = new Bundle();
            args.putParcelable("post", post);
            args.putInt("saved", 0);
            args.putInt("position", pos);
            frag.setArguments(args);
            if(backStack) {
                switchFragment(frag);
            }
            else {
                switchFragmentWithoutStack(frag);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("StartFrag", "Failed");
        }
    }

    public void openPostAtPos(int position) {
        if(position >= listPosts.size()-1) {
            ((Fragment_Posts)frag).loadNextBatch();
            System.out.println("Happened succesfully");
        }

        openPost(listPosts.get(position), position, false);
    }

    public void setListPosts(List<Post> newPosts) {
        this.listPosts = newPosts;
    }

    private void switchFragment(Fragment frag) {

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, frag, frag.getTag())
                .addToBackStack(null)
                .commit();

    }

    private void switchFragmentWithoutStack(Fragment frag) {

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, frag, frag.getTag())
                .addToBackStack(null)
                .commit();

    }

    public List<Post> startList(boolean initial, String subName) {
        listPosts = this.postsHandler.startList(initial, subName);
        return listPosts;
    }

    public List<Post> loadNextBatch () {
        listPosts = this.postsHandler.getListPosts();
        return listPosts;

    }

    public PostsAdapter getAdapater(){
        return this.postsHandler.getAdapater();
    }

    public void setFrag(Fragment frag) {
        this.frag = frag;
    }
}