package com.systemonline.fanscoupon.coupon_tabs;

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
import com.systemonline.fanscoupon.Model.BrandBranch;
import com.systemonline.fanscoupon.Model.Coupon;
import com.systemonline.fanscoupon.Model.FilterCheckboxStatus;
import com.systemonline.fanscoupon.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class CouponTab extends BaseFragment {

    public static int couponsLimit = 2;
    public static int couponPage = 1;
    public static int couponPageOnlyYou = 1;
    public static int myCouponsOffset;
    public static int selectedCopTypeID;
    //    public static int selectedCopTypeIDOnlyYou = 95;
//    public static int lastSelectedCouponType = -1;
    public static int coupIdTemp;
    public static boolean myCoupon;
    public static String coupSlugTemp;
    public static String selectedCoupType = "loyalty";
    public static String selectedCoupTypeOnlyYou = "loyalty";
    public static ArrayList<FilterCheckboxStatus> verySpecialsCheckboxStatuses = new ArrayList<>();
    public static ArrayList<FilterCheckboxStatus> favouriteBrandCheckboxStatuses = new ArrayList<>();
    public static ArrayList<FilterCheckboxStatus> interestedINCheckboxStatuses = new ArrayList<>();
    public static ArrayList<FilterCheckboxStatus> couponCategoriesCheckboxStatuses = new ArrayList<>();
    public static Coupon couponQualification;
    //    public static ArrayList<Coupon> allCoupons;
    public static ArrayList<ArrayList<Coupon>> allCoupons2;
    //    public static ArrayList<Coupon> forYou;
    public static ArrayList<ArrayList<Coupon>> forYou2;
    public static ArrayList<Coupon> myCoupons;
    public static ArrayList<BrandBranch> couponsBranches = new ArrayList<>();
    public static ArrayList<String> addToWalletSelectedCouponConditions = new ArrayList<>();
    public static String addToWalletSelectedCoupName = "";
    public static int sortState = 1;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.cop_tabs_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        getActivity().setTitle(getString(R.string.coupons));
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return x;
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

        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
//                    return new AllCouponsFragment();
                    return new AllCouponsFragment2();
                case 1:
                    return new OnlyForYouCouponsFragment2();
                case 2:
                    return new MyCouponsFragment();
            }
            return null;
        }


        @Override
        public int getCount() {

            return 3;

        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getResources().getString(R.string.all_cop);
                case 1:
                    return getResources().getString(R.string.only_for_you);
                case 2:
                    return getResources().getString(R.string.my_cop);
            }
            return null;
        }
    }
}