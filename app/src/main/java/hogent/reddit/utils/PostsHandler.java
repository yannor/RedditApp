package hogent.reddit.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import hogent.reddit.adapters.PostsAdapter;
import hogent.reddit.database.DatabaseHandler;
import hogent.reddit.database.SessionManager;
import hogent.reddit.domain.Post;
import hogent.reddit.domain.SubReddit;
import hogent.reddit.service.APIInterface;
import hogent.reddit.service.RedditService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yannick on 24/11/2017.
 */

public class PostsHandler {
    private List<Post> listPosts;
    private SessionManager session;

    private int countBatch;

    private RedditService rs = new RedditService();
    private DatabaseHandler db;
    private String subName;
    private PostsAdapter adapter;

    public PostsHandler(Activity main) {
        listPosts = new ArrayList<>();
        db = new DatabaseHandler(main);
        session = new SessionManager(main);
        countBatch = session.getBatch();
        adapter = new PostsAdapter(main, listPosts);
    }

    public List<Post> getListPosts() {
        return listPosts;
    }

    public List<Post> startList(boolean initial, String subName) {
        System.out.println(subName);
        this.subName = subName;
        if(initial) {
            initListPosts();
        }
        else {
            SubReddit sub = db.getSubReddit(subName);
            loadLastState(sub.getFirstNameList(), sub.getSizeList());
        }

        return listPosts;
    }


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
                //loadingPanel.setVisibility(View.GONE);
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
                //lvPosts.setSelection(posEnded);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.i("Reddit", "Call failed");
            }

        });

    }

    public PostsAdapter getAdapater(){
        return this.adapter;
    }
}
