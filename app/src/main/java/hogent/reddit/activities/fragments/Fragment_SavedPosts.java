package hogent.reddit.activities.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import hogent.reddit.R;
import hogent.reddit.adapters.SavedPostsAdapter;
import hogent.reddit.database.DatabaseHandler;
import hogent.reddit.domain.Post;

/**
 * Created by Yannick on 18/08/2017.
 */

public class Fragment_SavedPosts extends Fragment implements AdapterView.OnItemClickListener {

    //region variables

    // Own classes
    private DatabaseHandler db;
    private SavedPostsAdapter adapter;

    // Basic Variables
    private List<Post> listPosts;

    // Layout
    private ListView lvPosts;
    private View loadingPanel;


    Bundle argsLoader = new Bundle();
    //endregion

    String TAG = "Fragment_SavedPosts";

    public Fragment_SavedPosts() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__posts, container, false);
        setHasOptionsMenu(true);


        lvPosts = (ListView) view.findViewById(R.id.listVPosts);
        lvPosts.setOnItemClickListener(this);
        adapter = new SavedPostsAdapter(getActivity(), listPosts);
        lvPosts.setAdapter(adapter);

        loadingPanel = view.findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.GONE);

        registerForContextMenu(lvPosts);
        loadAllSavedPosts();
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        getActivity().setTitle("Saved posts");
        db = new DatabaseHandler(getActivity());
        listPosts = new ArrayList<>();


    }


    public void loadAllSavedPosts() {
        listPosts.clear();

        for (Post p : db.getAllSavedPosts()) {
            listPosts.add(p);
            adapter.notifyDataSetChanged();
        }


    }


    //region navigating

    private void switchFragment(Fragment frag) {

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, frag, frag.getTag())
                .addToBackStack(frag.getTag())
                .commit();

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Post post = (Post) lvPosts.getItemAtPosition(i);

        Class fragClass = Fragment_Detail.class;

        try {
            Fragment frag = (Fragment) fragClass.newInstance();
            Bundle args = new Bundle();
            args.putInt("saved", 1);
            args.putParcelable("post", post);
            frag.setArguments(args);
            switchFragment(frag);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("StartFrag", "Failed");
        }
    }

    //endregion


    //region Handling long holding items in list

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listVPosts) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(listPosts.get(info.position).getTitle());
            String[] menuItems = getResources().getStringArray(R.array.extraOptions);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.extraOptions);
        String menuOptionChosen = menuItems[menuItemIndex];
        Post postChosen = listPosts.get(info.position);

        handleExtraOptionsMenu(menuOptionChosen, postChosen);
        return true;
    }

    public void handleExtraOptionsMenu(String menuOptionChosen, Post postChosen) {
        // If others are needed, option to provide
        switch (menuOptionChosen) {
            case "Delete":
                dialogBoxDeletingPost(postChosen);
                break;
            default:
                break;
        }
    }

    public void dialogBoxDeletingPost(Post p) {
        final String fullName = p.getFullName();
        final Post post = p;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete " + p.getTitle());
        builder.setMessage("Delete this post from your saved posts?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                db.deleteSavedPost(fullName);
                listPosts.remove(post);
                adapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        builder.show();


    }

    //endregion

}

