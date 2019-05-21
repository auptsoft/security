package weplay.auptsoft.locationtracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import weplay.auptsoft.locationtracker.fragments.LoginInFragment;
import weplay.auptsoft.locationtracker.fragments.SignUpFragment;


/**
 * Created by Andrew on 5.2.19.
 */

public class StartupActivity extends AppCompatActivity{
    Toolbar toolbar;
    public static ViewPager viewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Let's get Started...");

        viewPager = (ViewPager)findViewById(R.id.startup_view_pager);
        tabLayout = (TabLayout)findViewById(R.id.startup_tab);

        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(getBaseContext(), tab.getText(), Toast.LENGTH_SHORT).show();
                //getSupportActionBar().setTitle(tab.getText());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
        });
    }

    protected class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        String[] titles = {"Register", "Login"};
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new SignUpFragment();
                default:
                    return new LoginInFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
