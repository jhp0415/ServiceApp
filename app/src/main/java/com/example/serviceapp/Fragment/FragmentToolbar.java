package com.example.serviceapp.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;

public class FragmentToolbar extends Fragment
                implements NavigationView.OnNavigationItemSelectedListener {

    MainActivity activity;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;

    public static FragmentToolbar newInstance(){
        return new FragmentToolbar();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_toolbar, container, false);
        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        // 네비게이션 드러워
//        LayoutInflater mainInflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        LayoutInflater mainInflater = LayoutInflater.from(getContext());
        LinearLayout layout = (LinearLayout) mainInflater.inflate( R.layout.activity_main, null );
        mDrawerLayout = (DrawerLayout) layout.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) layout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 검색창
        TextView textView = toolbar.findViewById(R.id.toolbar_title);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fragmentManager = activity.getSupportFragmentManager();
//                fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container1, SearchToolbar.getInstance(),"visible");
//                fragmentTransaction.replace(R.id.fragment_container2, SearchFragment.getInstance(),"visible");
//                fragmentTransaction.addToBackStack("SearchFragment");
//                fragmentTransaction.commit();
                //TODO: PoiActivity 실행하기

            }
        });

        activity = (MainActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Toast.makeText(activity.getApplicationContext(), "홈 버튼", Toast.LENGTH_SHORT).show();
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_wishlist) {
            // Handle the camera action
            Toast.makeText(activity.getApplicationContext(), "카메라버튼", Toast.LENGTH_SHORT).show();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
