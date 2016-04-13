package com.example.filip.firstview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton newTaskButton = (FloatingActionButton) findViewById(R.id.newTaskButton);
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(NavDrawerActivity.this, AddNewTaskActivity.class);
                NavDrawerActivity.this.startActivityForResult(myIntent, 1);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

//        ArrayAdapter adapter = new ArrayAdapter(getActionBar().getThemedContext(), android.R.layout.simple_list_item_1, data);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        try {
            fragmentManager.beginTransaction().replace(R.id.fragment_space, MyTasks.class.newInstance()).commit();
//            Log.i(LoginScreenActivity.TAG, "Tried");
        } catch (Exception e) {
            e.printStackTrace();
//            Log.i(LoginScreenActivity.TAG, "catched");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            LoginScreenActivity.LogOff(getApplicationContext());
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        boolean isFragment = false;
        Class fragmentClass = null;
        Fragment myFrag = null;


        int id = item.getItemId();

        if (id == R.id.feedback) {
            Intent myIntent = new Intent(NavDrawerActivity.this, Feedback.class);
            NavDrawerActivity.this.startActivity(myIntent);

        } else if (id == R.id.settings) {
            Intent myIntent = new Intent(NavDrawerActivity.this, Settings.class);
            NavDrawerActivity.this.startActivity(myIntent);

        }
         else if (id == R.id.adams_family) {
            fragmentClass = AdamsFamily.class;
            isFragment = true;
        }
         else if (id == R.id.my_tasks) {
            fragmentClass = MyTasks.class;
            isFragment = true;
        }
        else if (id == R.id.log_out_ad){
            LoginScreenActivity.LogOff(getApplicationContext());
            finish();
        }

        if (isFragment) {

            try {
                myFrag = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_space, myFrag).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
