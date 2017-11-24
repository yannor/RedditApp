package hogent.reddit.activities.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import javax.xml.datatype.Duration;

import hogent.reddit.R;
import hogent.reddit.activities.MainActivity;
import hogent.reddit.database.DatabaseHandler;
import hogent.reddit.domain.Post;
import hogent.reddit.utils.TouchListener;

public class Fragment_Detail extends Fragment {

    TextView tvTitle, tvText, tvUnavailable;
    ImageView ivMedia;
    Button btnOriginal;
    private Post post;
    private DatabaseHandler db;

    public Fragment_Detail() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__detail, container, false);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvText = (TextView) view.findViewById(R.id.tvText);
        tvUnavailable = (TextView) view.findViewById(R.id.tvUnavailable);
        ivMedia = (ImageView) view.findViewById(R.id.ivMedia);
        btnOriginal = (Button) view.findViewById(R.id.btnOriginalLink);

        final Bundle args = getArguments();
        post = (Post) args.get("post");

        if (args.getInt("saved") == 1) {
            setHasOptionsMenu(false);
        } else {
            setHasOptionsMenu(true);
        }

        tvTitle.setText(post.getTitle());
        tvText.setText(post.getText());

        Picasso.with(getActivity()).load(post.getUrlPicture()).resize(1000, 1000).error(R.drawable.redditbig).into(ivMedia);
        if (ivMedia.equals(R.drawable.redditbig)) {
            tvUnavailable.setVisibility(View.VISIBLE);
        } else {
            tvUnavailable.setVisibility(View.GONE);
        }


        btnOriginal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(post.getUrlPicture()));
                startActivity(browserIntent);
            }
        });

        view.setOnTouchListener(new TouchListener(this.getActivity()) {
                                    public void onSwipeRight() {
                                        int position = args.getInt("position");
                                        if (position == 0) {
                                            makeToast("Dit is eerste", false);
                                        } else {
                                            ((MainActivity) getActivity()).openPostAtPos(position - 1);
                                        }
                                    }

                                    public void onSwipeLeft() {
                                        ((MainActivity) getActivity()).openPostAtPos(args.getInt("position") + 1);
                                    }
                                }
        );
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    //region menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_post, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save_post:
                savePost();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void savePost() {

        db.savePost(post);
        makeToast("Post saved", false);
    }

    //endregion

    public void makeToast(String text, boolean longDur) {
        Toast toast;
        if (longDur) {
             toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        } else {
             toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        }

        toast.show();
    }
}
