package hogent.reddit.service;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Yannick on 19/03/2017.
 */

public interface APIInterface {

    @GET("/r/{subreddit}.json")
    Call<JsonElement> getPosts(@Path("subreddit") String subreddit, @Query("limit") Integer limit);

    @GET("/r/{subreddit}.json")
    Call<JsonElement> getPostsAdvanced(@Path("subreddit") String subreddit, @Query("after") String fullName, @Query("limit") Integer limit);

    // @GET("/r/{subreddit}.json")
    //Call<JsonElement> getPostsLastState(@Path("subreddit") String subreddit, @Query("before") String fullNameFirst, @Query("after") String fullNameLast);


}
