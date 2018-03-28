package com.example.user.mybrotherhood;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.example.user.mybrotherhood.Adapters.BrotherhoodRVAdapter;
import com.example.user.mybrotherhood.Dialog.AddBrotherhoodDialog;
import com.example.user.mybrotherhood.ItemTouchHelper.ItemTouchHelperAdapter;
import com.example.user.mybrotherhood.ItemTouchHelper.SimpleItemTouchHelperCallback;
import com.example.user.mybrotherhood.ItemTouchHelper.SimpleTouchHelperListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 3/21/2018.
 */

public class CreateBrotherhoodActivity extends AppCompatActivity implements AddBrotherhoodDialog.AddBrotherhoodDialogListener{

    private List<Brotherhood> brotherhoods;
    private RecyclerView rv;
    private SharedPreferences prefs;
    private static final String PREF_NAME= "brotherhood_names", broNames = "names" ;
    private Set<String> brotherhoodNames = new HashSet<>();
    private ItemTouchHelper touchHelper;
    private RecyclerView.AdapterDataObserver adapterObserver;
    private BrotherhoodRVAdapter adapter;
    private String strItemRemoved;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv_brotherhood_layout);


        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);


        initializeData();
        initializeAdapter();

      /*  // get fragment manager
        FragmentManager fm = getFragmentManager();

// add
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.list_container, new DetailFragment());
// alternatively add it with a tag
// trx.add(R.id.your_placehodler, new YourFragment(), "detail");
        ft.commit();*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the observer once finish to use on this activity
        // to prevent an ongoing listener
        adapter.unregisterAdapterDataObserver(adapterObserver);
    }

    private void initializeData(){
        // TODO - Get the brotherhoods from the DB
        brotherhoods = new ArrayList<>();
        // Get a set of all the names from the shared preference
        Set<String> names = prefs.getStringSet(broNames ,null);
        if (names != null){
            // Add the set to the hashset brotherhoodNames
            brotherhoodNames.addAll(names);
        }

        if(brotherhoodNames != null){
            // Add the names to the recycleview
            for(String name : brotherhoodNames ){
                addBrotherhoodList(name);
            }
        }

    }

    private void initializeAdapter(){
        adapter = new BrotherhoodRVAdapter(brotherhoods);
        rv.setAdapter(adapter);

        //  TEST - Get the item which was removed from the RecyclerView Brotherhood ->
        //  To remove from the storage (DB, Shared preference..)
        adapterObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                strItemRemoved = brotherhoods.get(positionStart).brotherhoodName;
                removeItemSP(strItemRemoved);
            }
        };

        adapter.registerAdapterDataObserver(adapterObserver);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);
    }

    // Start alert after the addFloadButton was touched
    public void addBrotherhood(View view) {
        AddBrotherhoodDialog addBrotherhoodDialog = new AddBrotherhoodDialog();
        addBrotherhoodDialog.show(getFragmentManager(),"addBrother");

    }

    // Once the user created a new brotherhood , save it
    @Override
    public void positiveClicked(String result) {
        // TODO - Add the brotherhood in DB
        // Add the name to the set
        brotherhoodNames.add(result);
        // Then save in the shared preference
        prefs.edit().putStringSet(broNames, brotherhoodNames).apply();
        // Eventually add to the recycleview
        addBrotherhoodList(result);
        Toast.makeText(this,"Added " + result, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void negativeClicked(String result) {
        Toast.makeText(this,"Canceled", Toast.LENGTH_SHORT).show();

    }

    private void addBrotherhoodList(String name){
        brotherhoods.add(new Brotherhood(name));
    }

    // Remove the item from the storage (DB, SharePrefs...)
    // TODO - Remove from DB the item
    private void removeItemSP(String itemName){
        // First remove the item from the RecyclerView
        brotherhoodNames.remove(itemName);
        // Secondly overwrite the new list(StringSet) in the share preference
        prefs.edit().putStringSet(broNames,brotherhoodNames).apply();
    }
}
