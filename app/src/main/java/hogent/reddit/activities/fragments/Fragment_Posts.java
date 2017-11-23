package hogent.reddit.activities.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import hogent.reddit.R;
import hogent.reddit.activities.MainActivity;
import hogent.reddit.adapters.PostsAdapter;
import hogent.reddit.database.DatabaseHandler;
import hogent.reddit.database.SessionManager;
import hogent.reddit.domain.Post;
import hogent.reddit.service.APIInterface;
import hogent.reddit.service.RedditService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Posts extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    //region variables

    // Own classes
    private RedditService rs = new RedditService();
    private DatabaseHandler db;
    private PostsAdapter adapter;
    private SessionManager session;

    // Basic Variables
    private ArrayList<Post> listPosts;
    private String subName;
    private int countBatch;

    // Layout
    private ListView lvPosts;
    private View loadingPanel;


    Bundle argsLoader = new Bundle();
    //endregion

    String TAG = "Fragment_SavedPosts";

    public Fragment_Posts() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__posts, container, false);
        setHasOptionsMenu(true);
        lvPosts = (ListView) view.findViewById(R.id.listVPosts);
        lvPosts.setOnItemClickListener(this);
        lvPosts.setOnScrollListener(this);
        adapter = new PostsAdapter(getActivity(), listPosts);
        lvPosts.setAdapter(adapter);
        loadingPanel = view.findViewById(R.id.loadingPanel);
        session = new SessionManager(getActivity());
        countBatch = session.getBatch();


        Log.i(TAG, "Posts loaded: " + countBatch);

        if (argsLoader.getInt("loader") == 1) {
            loadingPanel.setVisibility(View.VISIBLE);

        } else {
            loadingPanel.setVisibility(View.GONE);
        }

        argsLoader.putInt("loader", 1);
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        argsLoader.putInt("loader", 1);
        Bundle args = getArguments();
        subName = args.get("subName").toString();
        getActivity().setTitle(subName);
        db = new DatabaseHandler(getActivity());
        listPosts = new ArrayList<>();


        int opened = Integer.parseInt(getArguments().get("opened").toString());
        if (opened == 1) {
            loadLastState(db.getSubReddit(subName).getFirstNameList(), db.getSubReddit(subName).getSizeList());
        } else {
            initListPosts();
        }
        // new setup().execute();

    }


    @Override
    public void onStop() {
        super.onStop();
        db.updateListInfo(subName, listPosts.get(0).getFullName(), listPosts.size(), lvPosts.getFirstVisiblePosition());
    }



    //region scrolling

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        int threshold = 1;
        int count = lvPosts.getCount();

        if (i == SCROLL_STATE_IDLE) {
            if (lvPosts.getLastVisiblePosition() >= count - threshold) {
                //TODO make it smoother
                loadNextBatch();
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
    }

    //endregion

    //region loadingPosts

    public void initListPosts() {
        listPosts.clear();
        APIInterface api = rs.getClient().create(APIInterface.class);

        Call<JsonElement> call = api.getPosts(subName, countBatch - 1);

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonObject resp = response.body().getAsJsonObject();
                JsonObject data = resp.getAsJsonObject("data");
                JsonArray children = data.getAsJsonArray("children");

                for (int x = 0; x < children.size(); x++) {
                    JsonObject record = (JsonObject) children.get(x);
                    JsonObject post = record.getAsJsonObject("data");

                    String titel = post.get("title").getAsString();
                    String auteur = post.get("author").getAsString();
                    String thumbnail = post.get("thumbnail").getAsString();
                    String fullName = post.get("name").getAsString();


                    // JsonObject preview =  post.getAsJsonObject("preview");

                    /*
                        JsonArray images = preview.get("images").getAsJsonArray();

                        JsonObject source =  images.get(0).getAsJsonObject();
                        JsonObject url = source.getAsJsonObject("source");
                        urlPicture = url.get("url").getAsString();
                    */

                    String urlPicture = post.get("url").getAsString();

                    String upvotes = post.get("ups").getAsString();
                    String text = post.get("selftext").getAsString();

                    Post p = new Post(titel, auteur, thumbnail, urlPicture, upvotes, text, subName, fullName);
                    listPosts.add(p);

                }
                adapter.notifyDataSetChanged();
                loadingPanel.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.i("Reddit", "Call failed");
            }

        });

    }

    public void loadNextBatch() {
        APIInterface api = rs.getClient().create(APIInterface.class);

        Call<JsonElement> call = api.getPostsAdvanced(subName, listPosts.get(listPosts.size() - 1).getFullName(), countBatch);

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonObject resp = response.body().getAsJsonObject();
                JsonObject data = resp.getAsJsonObject("data");
                JsonArray children = data.getAsJsonArray("children");

                for (int x = 0; x < children.size(); x++) {
                    JsonObject record = (JsonObject) children.get(x);
                    JsonObject post = record.getAsJsonObject("data");

                    String titel = post.get("title").getAsString();
                    String auteur = post.get("author").getAsString();
                    String thumbnail = post.get("thumbnail").getAsString();
                    String fullName = post.get("name").getAsString();


                    // JsonObject preview =  post.getAsJsonObject("preview");

                    /*
                        JsonArray images = preview.get("images").getAsJsonArray();

                        JsonObject source =  images.get(0).getAsJsonObject();
                        JsonObject url = source.getAsJsonObject("source");
                        urlPicture = url.get("url").getAsString();
                    */

                    String urlPicture = post.get("url").getAsString();

                    String upvotes = post.get("ups").getAsString();
                    String text = post.get("selftext").getAsString();

                    Post p = new Post(titel, auteur, thumbnail, urlPicture, upvotes, text, subName, fullName);
                    listPosts.add(p);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.i("Reddit", "Call failed");
            }

        });

    }

    public void loadLastState(String first, int limit) {

        APIInterface api = rs.getClient().create(APIInterface.class);


        // Load all the posts before the last one and after the first one
        Call<JsonElement> call = api.getPostsAdvanced(subName, first, limit);

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonObject resp = response.body().getAsJsonObject();
                JsonObject data = resp.getAsJsonObject("data");
                JsonArray children = data.getAsJsonArray("children");
                for (int x = 0; x < children.size(); x++) {
                    JsonObject record = (JsonObject) children.get(x);
                    JsonObject post = record.getAsJsonObject("data");

                    String titel = post.get("title").getAsString();
                    String auteur = post.get("author").getAsString();
                    String thumbnail = post.get("thumbnail").getAsString();
                    String fullName = post.get("name").getAsString();



                    String urlPicture = post.get("url").getAsString();

                    String upvotes = post.get("ups").getAsString();
                    String text = post.get("selftext").getAsString();

                    Post p = new Post(titel, auteur, thumbnail, urlPicture, upvotes, text, subName, fullName);
                    listPosts.add(p);

                }
                adapter.notifyDataSetChanged();
                int posEnded = db.getSubReddit(subName).getPositionList();
                lvPosts.setSelection(posEnded);
                loadingPanel.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.i("Reddit", "Call failed");
            }

        });

    }

    //endregion

    //region navigating


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("listPosts", listPosts);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Post post = (Post) lvPosts.getItemAtPosition(i);
        argsLoader.putInt("loader", 0);

        ((MainActivity)getActivity()).openPost(post);
       /* Class fragClass = Fragment_Detail.class;
        argsLoader.putInt("loader", 0);

        try {
            Fragment frag = (Fragment) fragClass.newInstance();
            Bundle args = new Bundle();
            args.putParcelable("post", post);
            args.putInt("saved", 0);
            frag.setArguments(args);
            switchFragment(frag);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("StartFrag", "Failed");
        }*/
    }


    //endregion

    //region menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_posts, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        loadingPanel.setVisibility(View.VISIBLE);

        switch (item.getItemId()) {
            case R.id.action_refresh:
                initListPosts();
                return true;
            case R.id.action_refresh_bar:
                initListPosts();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    //endregion


}

