package com.debuggers.apna_tutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.debuggers.apna_tutor.fragments.HomeFragment;
import com.debuggers.apna_tutor.fragments.LibraryFragment;
import com.debuggers.apna_tutor.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
Toolbar toolbar;
BottomNavigationView bottomNavigationView;
FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);// down 3 option
        getSupportActionBar().setTitle(""); // Upper search menu

        bottomNavigationView=findViewById(R.id.bottom_menu);
        frameLayout=findViewById(R.id.frame_layout);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              switch(item.getItemId()) // nicher 3te
              {
                  case R.id.Home_button:
                      HomeFragment homeFragment=new HomeFragment();
                      selectedFragment(homeFragment);
                      break;
                  case R.id.library_button:
                      LibraryFragment libraryFragment=new LibraryFragment();
                      selectedFragment(libraryFragment);
                      break;
                  case R.id.Profile_button:
                      ProfileFragment profileFragment=new ProfileFragment();
                      selectedFragment(profileFragment);
                      break;
              }

                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.Home_button); // default
    }

    private void selectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // Search er botam
        switch (item.getItemId()) {
            case R.id.Search:
                break;
        }
        return false;
    }
}