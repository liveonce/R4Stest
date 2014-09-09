package pl.michalkasza.r4stest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import pl.michalkasza.r4stest.db.MySQLiteHelper;

public class DisplayFragment extends Fragment {
    ListView listView ;
    ArrayAdapter<String> adapter;
    ArrayList link_list;
    MySQLiteHelper db;
    Button button;


    public DisplayFragment() {}
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_display, container, false);
        button = (Button) rootView.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clearMethod();
            }

        });
        db = new MySQLiteHelper(getActivity());
        link_list = db.getAllLinks();
        listView = (ListView) rootView.findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(getActivity(),R.layout.row, R.id.label, link_list);
        listView.setAdapter(adapter);

        return rootView;
    }

    public void clearMethod() {
        db.deleteAll();
        link_list = db.getAllLinks();
        listView = (ListView) this.getActivity().findViewById(R.id.listView);
        adapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<String>(getActivity(),R.layout.row, R.id.label, link_list);
        listView.setAdapter(adapter);
    }
}