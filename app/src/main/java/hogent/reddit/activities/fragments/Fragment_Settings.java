package hogent.reddit.activities.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hogent.reddit.R;
import hogent.reddit.activities.MainActivity;
import hogent.reddit.database.SessionManager;

/**
 * Created by Yannick on 18/08/2017.
 */

public class Fragment_Settings extends Fragment {
    private SessionManager session;

    private EditText txtBatch;
    private Button btnSave, btnCancel;

    public Fragment_Settings() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        session = new SessionManager(getActivity());

        if (session.isFirstRun()) {
            session.firstRun();
            session.setBatch(10);
        }

        txtBatch = (EditText) view.findViewById(R.id.txtBatch);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        txtBatch.setText("" + session.getBatch());


        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int newBatch = Integer.parseInt(txtBatch.getText().toString());
                if (newBatch <= 0 || txtBatch.getText().toString().matches("")) {
                    Toast toast = Toast.makeText(getContext(), "Changes unvalid", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    //to close keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

                    //Find the currently focused view, so we can grab the correct window token from it.
                    View view = getActivity().getCurrentFocus();
                    //If no view currently has focus, create a new one, just so we can grab a window token from it
                    if (view == null) {
                        view = new View(getActivity());
                    }


                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    session.setBatch(newBatch);
                    Toast toast = Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_SHORT);
                    toast.show();
                    ((MainActivity) getActivity()).openSubreddits();
                }

            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).openSubreddits();
            }
        });


        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Settings");

    }


    //region navigating

    private void switchFragment(Fragment frag) {

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, frag, frag.getTag())
                .addToBackStack(frag.getTag())
                .commit();

    }

    //endregion





}
