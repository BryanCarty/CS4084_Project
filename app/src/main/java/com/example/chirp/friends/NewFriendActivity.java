package com.example.chirp.friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.example.chirp.MainActivity;
import com.example.chirp.R;
import com.example.chirp.feed_page.FriendContentFeedFragment;
import com.example.chirp.games.GamesMenuActivity;
import com.example.chirp.posts.NewPostActivity;
import com.example.chirp.profile.ProfileActivity;
import com.example.chirp.trending_page.TrendingFragment;
import com.google.android.material.tabs.TabLayout;

/**
 * This activity contains a TabLayout.
 * This activity is responsible for allowing the user to
 * jump between the fragment responsible for sending friend
 * requests and the fragment responsible for accepting/declining
 * received requests.
 */
public class NewFriendActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;


    /**
     * The below code executes when the activity is successfully created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        tabLayout = findViewById(R.id.tabFriends);
        viewPager = findViewById(R.id.vpFriends);
        setViewPager();
    }

    class Adapter extends FragmentPagerAdapter {

        public Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        /**
         * The below code is responsible for returning the fragment
         * associated with the currently active tab.
         * @param position
         * @return
         */
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    FindFriendsFragment friendFrag = new FindFriendsFragment();
                    return  friendFrag;
                case 1:
                    RequestsFragment requestsFragment = new RequestsFragment();
                    return requestsFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return tabLayout.getTabCount();
        }
    }


    /**
     * The below code configures the TabLayout
     */
    private void setViewPager(){

        //adds the tab layouts
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.fragment_find_friends_tab));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.fragment_requests_tab));


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        NewFriendActivity.Adapter adapter = new NewFriendActivity.Adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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


}