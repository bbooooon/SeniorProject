package com.example.seniorpj100per.FoodList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.seniorpj100per.HamburgerActivity;
import com.example.seniorpj100per.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smew on 28/1/2561.
 */

public class FoodListFragment extends Fragment {

    ListViewAdapter adapter;
    ListView lv_foodList;

    private DatabaseReference journalCloudEndPoint;
    private DatabaseReference mDatabase;
    private List<String> str;
    private List<KcalTable1> mJournalEntries;

    String username = "";

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_foodlist, container, false);

        lv_foodList = (ListView) v.findViewById(R.id.lv_foodlist);

        HamburgerActivity activity = (HamburgerActivity) getActivity();
        Bundle results = activity.getMyData();
        username = results.getString("username");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        journalCloudEndPoint = mDatabase.child("kcaltable1");
        mJournalEntries = new ArrayList<>();
        str = new ArrayList<>();

        journalCloudEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    KcalTable1 note = noteSnapshot.getValue(KcalTable1.class);
                    mJournalEntries.add(note);
                }
                mJournalEntries.remove(0);
                adapter = new ListViewAdapter(mJournalEntries,getContext());
                lv_foodList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error", databaseError.getMessage());
            }
        });

        lv_foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ResultActivity.class);
                intent.putExtra("position", position+1);
                intent.putExtra("username",username);
                intent.putExtra("foodlist","foodlist");
                intent.putExtra("filename",mJournalEntries.get(position).getFoodname_th()+".jpg");
                startActivity(intent);
            }
        });

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
