package com.example.chirp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.chirp.chats.ChatFragment;
import com.example.chirp.feed_page.FriendContentFeedFragment;
import com.example.chirp.friends.NewFriendActivity;
import com.example.chirp.games.GamesMenuActivity;
import com.example.chirp.posts.NewPostActivity;
import com.example.chirp.profile.ProfileActivity;
import com.example.chirp.trending_page.TrendingFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tabMain);
        viewPager = findViewById(R.id.vpMain);

        setViewPager();
    }
    class Adapter extends FragmentPagerAdapter {

        public Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return new TrendingFragment();
                case 1:
                    return new FriendContentFeedFragment();
                case 2:
                    return new ChatFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabLayout.getTabCount();
        }
    }


    private void setViewPager(){

        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.trending_tab));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.friend_feed_tab));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_chat));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Adapter  adapter = new Adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.mnuProfile)
        {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }else if(id==R.id.addFriend){
            startActivity(new Intent(MainActivity.this, NewFriendActivity.class));

        }else if(id==R.id.addPost){
            startActivity(new Intent(MainActivity.this, NewPostActivity.class));
        }else if(id==R.id.playGame){
            startActivity(new Intent(MainActivity.this, GamesMenuActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private  boolean doubleBackPressed = false;

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        if(tabLayout.getSelectedTabPosition()>0)
        {
            tabLayout.selectTab(tabLayout.getTabAt(0));
        }
        else
        {
            if(doubleBackPressed)
            {
                finishAffinity();
            }
            else
            {
                doubleBackPressed=true;
                Toast.makeText(this, R.string.press_back_to_exit, Toast.LENGTH_SHORT).show();
                //delay
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(() -> doubleBackPressed=false, 2000);

            }
        }
    }
}




