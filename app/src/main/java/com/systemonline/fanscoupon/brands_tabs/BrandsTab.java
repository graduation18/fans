package com.systemonline.fanscoupon.brands_tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Model.Brand;
import com.systemonline.fanscoupon.Model.BrandFilter;
import com.systemonline.fanscoupon.Model.Filter;
import com.systemonline.fanscoupon.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class BrandsTab extends BaseFragment {

    public static int brandsLimit = 4;
    public static int brandPage = 0;
    public static int myBrandsLimit = 4;
    public static int myBrandPage = 0;
    public static int brandSortState = 0;
    public static int myBrandSortState = 0;
    public static ArrayList<BrandFilter> brandFilters = new ArrayList<>();
    public static ArrayList<BrandFilter> myBrandFilters = new ArrayList<>();
    public static ArrayList<ArrayList<Filter>> brandAllFilters = new ArrayList<>();
    public static ArrayList<ArrayList<Filter>> myBrandAllFilters = new ArrayList<>();
    public static ArrayList<Brand> allBrands;
    public static ArrayList<Brand> myBrands;
    public static Brand selectedBrand;
    public TabLayout tabLayout;
    public ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View brandsView = inflater.inflate(R.layout.brands_tabs_layout, null);
        tabLayout = (TabLayout) brandsView.findViewById(R.id.tabs);
        getActivity().setTitle(getString(R.string.brands));
        viewPager = (ViewPager) brandsView.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
//                TabLayout.Tab tab = tabLayout.getTabAt(1);
//                tab.select();
            }
        });


        return brandsView;

    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AllBrandsFragment();
                case 1:
                    return new MyBrandsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return 2;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getResources().getString(R.string.all_brands);
                case 1:
                    return getResources().getString(R.string.my_brands);
            }
            return null;
        }
    }
}