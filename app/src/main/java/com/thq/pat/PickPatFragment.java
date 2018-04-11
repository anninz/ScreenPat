package com.thq.pat;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;


public class PickPatFragment extends BaseActivity implements Fragment_0.FragmentInteraction {

    private Toolbar mToolbar;
    private ViewPager mPage;
    private SlidingTabLayout mTabLayout;
    private List mListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_viewpager);
        mTabLayout = (SlidingTabLayout) findViewById(R.id.tab_layout);
        mPage = (ViewPager) findViewById(R.id.viewPager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//替换ActionBar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//关联menu布局文件
//mToolbar.inflateMenu(R.menu.menu_main);
//mToolbar.setTitle("Title");
//设置导航按钮
//        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        mListFragment = new ArrayList<>();
        mListFragment.add(new Fragment_0());
        mListFragment.add(new Fragment_1());

        PagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPage.setAdapter(adapter);

        mToolbar.setContentInsetsAbsolute(0, 0);
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setTitleTextColor(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        mTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        mTabLayout.setTabTitleTextSize(18);
        mTabLayout.setViewPager(mPage);

    }



//    @Override
//    public void onPageScrollStateChanged(int arg0)
//    {
//
//    }
//
//
//    @Override
//    public void onPageScrolled(int arg0, float arg1, int arg2)
//    {
//
//    }
//
//
//    @Override
//    public void onPageSelected(int Index)
//    {
//        //设置当前要显示的View
//        mViewPager.setCurrentItem(Index);
//        //选中对应的Tab
//        mActionBar.selectTab(mTabs.get(Index));
//    }
//
//    @Override
//    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
//
//        if(mViewPager!=null)
//        {
//            mViewPager.setCurrentItem(tab.getPosition());
//        }
//
//    }
//
//    @Override
//    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
//
//    }
//
//    @Override
//    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
//
//    }
//
    @Override
    public void process(String str) {
        if (str.equals("change")) {
            isHost = true;
        } else {
            loadResources(str);
        }
    }
}