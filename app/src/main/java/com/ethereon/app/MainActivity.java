package com.ethereon.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.ethereon.app.ui.AppCreatorFragment;
import com.ethereon.app.build.BuildProgressFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment;

            switch (item.getItemId()) {
                case R.id.nav_chat:
                    selectedFragment = new ChatFragment();
                    break;
                case R.id.nav_debug:
                    selectedFragment = new DebugFragment();
                    break;
                case R.id.nav_settings:
                    selectedFragment = new SettingsFragment();
                    break;
                case R.id.nav_expansions:
                    selectedFragment = new ExpansionsFragment();
                    break;
                case R.id.nav_creator:
                    selectedFragment = new AppCreatorFragment();
                    break;
                case R.id.nav_progress:
                    selectedFragment = new BuildProgressFragment();
                    break;
                case R.id.nav_roadmap:
                default:
                    selectedFragment = new RoadmapFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        });

        // Load the default fragment (Roadmap)
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_roadmap);
        }
    }
}