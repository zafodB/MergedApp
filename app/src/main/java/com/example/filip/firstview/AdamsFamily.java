package com.example.filip.firstview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by filip on 07/04/2016.
 */
public class AdamsFamily extends ListFragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup myView = (ViewGroup) inflater.inflate(R.layout.adams_family_fragment, null);

        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        String[] taskList = new String[]{"task1", "task2", "task3", "task4", "task5", "task6", "task7", "task8", "task9", "task10", "task11", "task12", "task13", "task14", "task15", "task16", "task17", "task18", "task19", "task20"};

        ListAdapter mojAdapter = new CustomAdapter(getContext() , taskList);

        setListAdapter(mojAdapter);

        super.onActivityCreated(savedInstanceState);
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        Toast.makeText(getActivity() , "launch new activity", Toast.LENGTH_LONG).show();
//        super.onListItemClick(l, v, position, id);
//    }

    public static Fragment newInstance(Context context) {
        AdamsFamily instance = new AdamsFamily();

        return instance;
    }


}
