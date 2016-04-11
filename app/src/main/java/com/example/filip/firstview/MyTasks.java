package com.example.filip.firstview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by filip on 07/04/2016.
 */
public class MyTasks extends Fragment {public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup myView = (ViewGroup) inflater.inflate(R.layout.other_fragment, null);
    return myView;
}

    public static Fragment newInstance(Context context) {
        MyTasks instance = new MyTasks();

        return instance;
    }
}
