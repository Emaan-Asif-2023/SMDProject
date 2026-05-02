package com.example.project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Set up ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Disable swipe if you want navigation only through bottom tabs
        viewPager.setUserInputEnabled(false);

        // Set up BottomNavigationView with ViewPager
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_search) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.navigation_bookings) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.navigation_saved) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (itemId == R.id.navigation_settings) {
                viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });

        // Sync BottomNavigationView when ViewPager page changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_search);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_bookings);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_saved);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
                        break;
                }
            }
        });
    }
}