package io.h3kk3.tunnusmuistio.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.database.Cursor;


import java.sql.SQLException;

public class MainActivityFragment extends Fragment {
    private ArrayAdapter<String> mIbanmemoAdapter;
    private TunnusDbAdapter mDbAdapter;
    public static final int INSERT_ID = Menu.FIRST;
    private int mMemoNum = 1;

    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        try {
            mDbAdapter = new TunnusDbAdapter(MainActivityFragment.this.getActivity());
            mDbAdapter.open();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor c = mDbAdapter.getAll();

        String[] from = new String[] { TunnusDbAdapter.COLUMN_NICK };
        int[] to = new int[] { R.id.list_item_name_iban_pair_textview };

        SimpleCursorAdapter listdata = new SimpleCursorAdapter(MainActivityFragment.this.getActivity(), R.layout.list_item_name_iban_pair, c, from, to, 1);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_friendlist);
        listView.setAdapter(listdata);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor c = ((SimpleCursorAdapter) adapterView.getAdapter()).getCursor();
                c.moveToPosition(position);
                String friend = c.getString(1);
                String friendIban = c.getString(2);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivityFragment.this.getActivity());
                alertDialogBuilder.setMessage(friend + " - " + friendIban);

                alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        return rootView;
    }
}




