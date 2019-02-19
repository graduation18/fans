package com.systemonline.fanscoupon.experience_tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.json.JSONTokener;


public class GoodExperienceFragment extends BaseFragment {

    TextView tv_no_exp;
    ListView lvGoodExp;
    Utility _utility;
    Button loadMore;
    private CustomLoading customLoading;
//    boolean loadMoreFlag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.experience_tab_good, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        tv_no_exp = (TextView) root.findViewById(R.id.tv_no_goodExp);
        tv_no_exp.setVisibility(View.GONE);
//        Log.e("good exp", "on create");

        lvGoodExp = (ListView) root.findViewById(R.id.lv_good_exp);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.goodExp_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                customLoading.showProgress(_utility.getCurrentActivity());
                ExperienceTab.requestAllExp(ExperienceTab.expLimit, ExperienceTab.expIndex, 2, GoodExperienceFragment.this);//refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadMore = (Button) root.findViewById(R.id.loadMoreGoodExp);
//
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customLoading.showProgress(_utility.getCurrentActivity());
                ExperienceTab.requestAllExp(ExperienceTab.allExperiences.get(0).size() + 4, ExperienceTab.expIndex, 2, GoodExperienceFragment.this);//load more
            }
        });
        if (ExperienceTab.allExperiences.get(0).isEmpty())
            noExpStatus();
        else
            showExperiences();
        return root;
    }

    private void showExperiences() {
        Log.e("sort and show", "good exp");
        if (ExperienceTab.allExperiences.get(0).isEmpty())
            noExpStatus();
        else {
            tv_no_exp.setVisibility(View.GONE);
            lvGoodExp.setVisibility(View.VISIBLE);
            GoodExperiencesAdapter goodExperiencesAdapter = new GoodExperiencesAdapter(getActivity(), ExperienceTab.allExperiences.get(0));
            lvGoodExp.setAdapter(goodExperiencesAdapter);
            _utility.setListViewHeightBasedOnChildren(lvGoodExp);
//        lvFunExp.setSelectionFromTop(MainActivity.allBrands.size() - 4, 0);
            lvGoodExp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                try {
//                    MainActivity.selectedBranddd = (Brand) MainActivity.allBrands.get(position).clone();
//                } catch (CloneNotSupportedException e) {
//                    Log.e("brand class clone", "exception");
//                    e.printStackTrace();
//                }
////                MainActivity.myCoupon = false;
//                Fragment fragment = new SingleBrandFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
                }
            });
        }

//        loadMore.setVisibility(View.VISIBLE);
    }

    private void noExpStatus() {
        loadMore.setVisibility(View.GONE);
        tv_no_exp.setVisibility(View.VISIBLE);
        lvGoodExp.setVisibility(View.GONE);
    }


    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("good exp response", "-------------");

            ExperienceTab.allExperiences.set(0, ParseData.parseAllExperiences(Result).get(0));

            if (ExperienceTab.allExperiences == null || ExperienceTab.allExperiences.get(0) == null) {
                customLoading.hideProgress();
                _utility.showMessage(getResources().getString(R.string.ws_err));
//

            } else {
                Log.e("all good size", ">> " + ExperienceTab.allExperiences.get(0).size());
                Log.e("all bad size", ">> " + ExperienceTab.allExperiences.get(1).size());
                Log.e("all fun size", ">> " + ExperienceTab.allExperiences.get(2).size());
                customLoading.hideProgress();
                showExperiences();
            }
        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.ws_err));
        } finally {
//
            customLoading.hideProgress();
        }

    }

}
