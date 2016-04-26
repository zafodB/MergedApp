package com.example.filip.firstview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private NavigationView navigationView;
    private Menu myMenu;
    private SubMenu menuGroups;
    private SubMenu menuRest;


    final private int ID_SETTINGS_MENU = 1;
    final private int ID_FEEDBACK_MENU = 2;
    final private int ID_LOG_OFF_MENU = 3;
    final private int[] ID_GROUPS = {4, 5, 6, 7, 8, 9, 10};

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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        loadUpButtons();

        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        try {
            fragmentManager.beginTransaction().replace(R.id.fragment_space, TasksListFragment.newInstance
                    (getApplicationContext(), ApplicationMain.getUserGroups().get(0))).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        loadUpButtons();
        super.onResume();
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


        int id = item.getItemId();

        if (id == ID_FEEDBACK_MENU) {
            Intent myIntent = new Intent(NavDrawerActivity.this, Feedback.class);
            NavDrawerActivity.this.startActivity(myIntent);

        } else if (id == ID_SETTINGS_MENU) {
            Intent myIntent = new Intent(NavDrawerActivity.this, Settings.class);
            NavDrawerActivity.this.startActivity(myIntent);

        } else if (id == ID_LOG_OFF_MENU) {
            LoginScreenActivity.LogOff(getApplicationContext());
            finish();
        } else {

            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_space, TasksListFragment.newInstance
                    (getApplicationContext(), item.getTitle().toString())).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadUpButtons() {
        myMenu = navigationView.getMenu();

        myMenu.clear();
        menuGroups = myMenu.addSubMenu("Groups");

        menuGroups.clear();
        int i = 0;
        for (String s : ApplicationMain.getUserGroups()) {
            menuGroups.add(R.id.menu_group_groups, ID_GROUPS[i], Menu.NONE, s).setIcon(android.R.drawable
                    .btn_star_big_on);
            i++;
        }

        menuRest = myMenu.addSubMenu("Others");
        menuRest.add(Menu.NONE, ID_SETTINGS_MENU, Menu.NONE, "Settings").setIcon(android.R.drawable
                .ic_menu_preferences);
        menuRest.add(Menu.NONE, ID_FEEDBACK_MENU, Menu.NONE, "Send Feedback").setIcon(android.R.drawable.ic_menu_send);
        menuRest.add(Menu.NONE, ID_LOG_OFF_MENU, Menu.NONE, "Log off").setIcon(android.R.drawable.ic_lock_idle_lock);


//        Log.i(ApplicationMain.LOGIN_TAG, String.valueOf(ApplicationMain.userGroups.size()) + " is the size of
// groups");
//        Log.i(ApplicationMain.LOGIN_TAG, "loadUp buttons");
    }

}
