package pl.michalkasza.r4stest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import pl.michalkasza.r4stest.db.MySQLiteHelper;

public class DisplayFragment extends Fragment implements View.OnClickListener{
    ListView listView ;
    MySQLiteHelper db;
    ImageButton button;

    public DisplayFragment() {}
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_display, container, false);
        button = (ImageButton) rootView.findViewById(R.id.imageButtonClear);
        button.setOnClickListener(this);

        db = new MySQLiteHelper(getActivity());
        ArrayList link_list = db.getAllLinks();

        listView = (ListView) rootView.findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, link_list);
        listView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        db.deleteAll();
    }
}