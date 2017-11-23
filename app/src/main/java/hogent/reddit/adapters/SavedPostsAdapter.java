package hogent.reddit.adapters;

/**
 * Created by Yannick on 18/08/2017.
 */


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import hogent.reddit.R;
import hogent.reddit.domain.Post;


public class SavedPostsAdapter extends ArrayAdapter<Post> {

    private Context context;
    private List<Post> listPosts;

    public SavedPostsAdapter(Context context, List<Post> lstPosts) {
        super(context, R.layout.row_post, lstPosts);

        this.context = context;
        this.listPosts = lstPosts;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater.from(getContext()));
        View row = inflater.inflate(R.layout.row_post, parent, false);


        ImageView thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
        TextView author = (TextView) row.findViewById(R.id.author);
        TextView title = (TextView) row.findViewById(R.id.title);

        //upvotes is reused for the subreddit
        TextView subreddit = (TextView) row.findViewById(R.id.upvotes);


        Post post = listPosts.get(position);
        author.setText("Posted by: " + post.getAuthor());
        title.setText(post.getTitle());
        subreddit.setText("Subreddit: " + post.getSubReddit());


        Picasso.with(getContext()).load(post.getThumbnail()).into(thumbnail);

        return row;
    }
}




