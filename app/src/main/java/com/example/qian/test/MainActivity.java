package com.example.qian.test;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.w3c.dom.Text;

import MyFragments.FragmentBoxOffice;
import MyFragments.FragmentSearch;
import MyFragments.FragmentUpcoming;
import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import me.tatarka.support.os.PersistableBundle;
import network.VolleySingleton;
import services.MyService;
import tabs.SlidingTabLayout;

public class MainActivity extends ActionBarActivity {

    private static final int JOB_ID = 100;
    private Toolbar toolbar;
    private ViewPager mPager;
    private SlidingTabLayout mTabs;
    private JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mJobScheduler=JobScheduler.getInstance(this);
        constructJob();
        toolbar= (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavigationDrawerFragment drawerFragment= (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp((DrawerLayout)findViewById(R.id.drawer_layout),toolbar);   //define setUp(), to link fragment and layout file
        mPager= (ViewPager) findViewById(R.id.pager);
        mTabs= (SlidingTabLayout) findViewById(R.id.tabs);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs.setDistributeEvenly(true);   //set distribute tags
//        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
//            @Override
//            public int getIndicatorColor(int position) {
//                return getResources().getColor(R.color.colorAccent);   //set tag underline color
//            }
//        });
        mTabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
        mTabs.setCustomTabView(R.layout.custom_tab_view, R.id.tabText);
        mTabs.setViewPager(mPager);

        ImageView imageView=new ImageView(this);
        imageView.setImageResource(R.drawable.ic_add_black_48dp);

        FloatingActionButton actionButton=new FloatingActionButton.Builder(this)
                .setContentView(imageView).build();
        ImageView sub1=new ImageView(this);
        ImageView sub2=new ImageView(this);
        ImageView sub3=new ImageView(this);
        sub1.setImageResource(R.drawable.ic_archive_black_36dp);
        sub2.setImageResource(R.drawable.ic_archive_black_36dp);
        sub3.setImageResource(R.drawable.ic_archive_black_36dp);
        SubActionButton.Builder itemBuilder=new SubActionButton.Builder(this);

        SubActionButton button_1 =itemBuilder.setContentView(sub1).build();
        SubActionButton button_2 =itemBuilder.setContentView(sub2).build();
        SubActionButton button_3 =itemBuilder.setContentView(sub3).build();
        FloatingActionMenu actionMenu=new FloatingActionMenu.Builder(this)
                .addSubActionView(button_1)
                .addSubActionView(button_2)
                .addSubActionView(button_3)
                .attachTo(actionButton).build();

    }


    private void constructJob(){
        PersistableBundle persistableBundle=new PersistableBundle();
        JobInfo.Builder builder=new JobInfo.Builder(JOB_ID,new ComponentName(this, MyService.class));
        builder.setPeriodic(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);
        mJobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id =item.getItemId();
        if(id==R.id.action_settings){
            return true;
        }
        if(id==R.id.nevigate){
            startActivity(new Intent(this,SubActivity.class));
        }
        return  super.onOptionsItemSelected(item);
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        int icons[]={R.drawable.ic_archive_white_36dp,R.drawable.ic_drafts_white_36dp,R.drawable.ic_report_white_36dp};
        String[] tabs;
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs=getResources().getStringArray(R.array.tabs);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment=null;
            switch (position){
                case 0:
                    myFragment= FragmentSearch.newInstance("", "");
                    break;
                case 1:
                    myFragment= FragmentBoxOffice.newInstance("", "");
                    break;
                case 2:
                    myFragment= FragmentUpcoming.newInstance("", "");
                    break;
            }
            return myFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {      //set image and text of the tag
            Drawable drawable=getResources().getDrawable(icons[position]);
            drawable.setBounds(0,0,36,36);
            ImageSpan imageSpan=new ImageSpan(drawable);
            SpannableString spannableString=new SpannableString(" ");
            spannableString.setSpan(imageSpan,0,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class MyFragment extends Fragment{    //build simple fragment : xx+number
        private TextView textView;
        public static MyFragment getInstance(int position){
            MyFragment myFragment=new MyFragment();   //?
            Bundle args=new Bundle();
            args.putInt("position",position);
            myFragment.setArguments(args);
            return myFragment;

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout=inflater.inflate(R.layout.fragment_my,container,false);
            textView= (TextView) layout.findViewById(R.id.position);
            Bundle bundle=getArguments();
            if(bundle!=null){
                textView.setText("xx"+bundle.getInt("position"));
            }
            RequestQueue requestQueue= VolleySingleton.getsInstance().getRequestQueue();
            StringRequest request=new StringRequest(Request.Method.GET, " ", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            return layout;
        }
    }
}
