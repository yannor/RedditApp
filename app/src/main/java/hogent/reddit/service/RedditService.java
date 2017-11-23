package hogent.reddit.service;

/**
 * Created by Yannick on 19/03/2017.
 */


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RedditService {


    private static final String API_BASE_URL = "http://reddit.com";

    private Retrofit retrofit;

    public RedditService() {

    }

    public Retrofit getClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
