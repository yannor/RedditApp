package hogent.reddit.activities.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import hogent.reddit.R;
import hogent.reddit.activities.MainActivity;
import hogent.reddit.database.DatabaseHandler;
import hogent.reddit.domain.SubReddit;


public class Fragment_SubReddits extends Fragment {
    //region Variables
    private DatabaseHandler db;
    private ArrayAdapter<String> adapter;


    private List<String> subReddits;

    @Bind(R.id.subRedditList)
    ListView lvSubRedditList;
    //endregion

    public Fragment_SubReddits() {
        // Required empty public constructor
    }


    public static Fragment_SubReddits newInstance() {
        Fragment_SubReddits fragment = new Fragment_SubReddits();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__sub_reddits, container, false);

        db = new DatabaseHandler(getActivity());

        lvSubRedditList = (ListView) view.findViewById(R.id.subRedditList);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBoxAddingSub();
            }
        });


        setHasOptionsMenu(true);
        //setUpListSubReddits();
        registerForContextMenu(lvSubRedditList);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Subreddits");
        subReddits = new ArrayList<>();
        setUpListSubReddits();
    }

    //region Handling list subreddits

    public void setUpListSubReddits() {

        for (SubReddit s : db.getAllSubReddits()) {
            subReddits.add(s.getName());
        }

        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, subReddits);

        // Assign adapter to ListView
        lvSubRedditList.setAdapter(adapter);

        // ListView Item Click Listener
        lvSubRedditList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String sub = (String) lvSubRedditList.getItemAtPosition(position);
                Toast toast = Toast.makeText(getContext(), sub, Toast.LENGTH_SHORT);
                toast.show();


                int opened = 1;

                if (db.getSubReddit(sub).getOpened() == 0) {
                    db.openSubRedditFirstTime(sub);
                    opened = 0;
                }

                Class fragClass = Fragment_Posts.class;
                try {
                    Fragment frag = (Fragment) fragClass.newInstance();
                    Bundle args = new Bundle();
                    args.putString("subName", sub);
                    args.putInt("opened", opened);
                    frag.setArguments(args);
                    switchFragment(frag);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("StartFrag", "Failed");
                }


            }
        });
    }

    public void dialogBoxAddingSub() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add subreddit");

// Set up the input
        final EditText input = new EditText(getContext());

// Specify the type of input expected;
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subName = input.getText().toString();
                SubReddit sub = new SubReddit(subName);
                boolean exists = false;

                for (SubReddit s : db.getAllSubReddits()) {
                    if (s.getName().toUpperCase().equals(subName.toUpperCase())) {
                        exists = true;
                    }
                }
                if (exists) {
                    Toast toast = Toast.makeText(getContext(), "Subreddit already added", Toast.LENGTH_LONG);
                    toast.show();
                    dialogBoxAddingSub();
                } else {
                    //TODO check the given name if that subreddit exists
                    db.addSubReddit(sub);
                    subReddits.add(sub.getName());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void dialogBoxDeletingSub(String nameSubreddit) {
        final String nameSub = nameSubreddit;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete " + nameSub);
        builder.setMessage("Are you sure you want to delete this subreddit?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                db.deleteSubReddit(nameSub);
                subReddits.remove(nameSub);
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

    //region Handling long holding items in list

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.subRedditList) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(subReddits.get(info.position));
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
        String subRedditChosen = subReddits.get(info.position);

        handleExtraOptionsMenu(menuOptionChosen, subRedditChosen);
        return true;
    }

    public void handleExtraOptionsMenu(String menuOptionChosen, String subRedditChosen) {
        // If others are needed, option to provide
        switch (menuOptionChosen) {
            case "Delete":
                dialogBoxDeletingSub(subRedditChosen);
                break;
            default:
                break;
        }
    }


    //endregion

    //region menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_subreddits, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                ((MainActivity) getActivity()).openSettings();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    //endregion



    private void switchFragment(Fragment frag) {

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, frag, frag.getTag())
                .addToBackStack(frag.getTag())
                .commit();

    }


}