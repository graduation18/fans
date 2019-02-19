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


public class BadExperienceFragment extends BaseFragment {

    TextView tv_no_bad_exp;
    ListView lvBadExp;
    Button loadMoreBadExp;
    //    boolean loadMoreFlag;
    private Utility _utility;
    private CustomLoading customLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.experience_tab_bad, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        tv_no_bad_exp = (TextView) root.findViewById(R.id.tv_no_badExp);
        tv_no_bad_exp.setVisibility(View.GONE);
//        Log.e("bad exp", "on create");
        lvBadExp = (ListView) root.findViewById(R.id.lv_bad_exp);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.badExp_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

//
                customLoading.showProgress(_utility.getCurrentActivity());
                ExperienceTab.requestAllExp(ExperienceTab.expLimit, ExperienceTab.expIndex, 1, BadExperienceFragment.this);//refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadMoreBadExp = (Button) root.findViewById(R.id.loadMoreBadExp);
//        loadMoreBadExp = (Button) ((LayoutInflater) getActivity().
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_footer, null, false);
//        lvBadExp.addFooterView(loadMoreBadExp);

        loadMoreBadExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                ExperienceTab.requestAllExp(ExperienceTab.allExperiences.get(1).size() + 4, ExperienceTab.expIndex, 1, BadExperienceFragment.this);//load more
            }
        });
        if (ExperienceTab.allExperiences.get(1).isEmpty())
            noExpStatus();
        else
            showExperiences();
        return root;
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("bad exp response", "-------------");

            ExperienceTab.allExperiences.set(1, ParseData.parseAllExperiences(Result).get(1));

            if (ExperienceTab.allExperiences == null || ExperienceTab.allExperiences.get(1) == null) {
                _utility.showMessage(getResources().getString(R.string.ws_err));
//
                customLoading.hideProgress();
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


    private void showExperiences() {
        if (ExperienceTab.allExperiences.get(1).isEmpty())
            noExpStatus();
        else {
            Log.e("sort and show", "bad exp");
            tv_no_bad_exp.setVisibility(View.GONE);
            lvBadExp.setVisibility(View.VISIBLE);
            BadExperiencesAdapter badExperiencesAdapter = new BadExperiencesAdapter(getActivity(), ExperienceTab.allExperiences.get(1));
            lvBadExp.setAdapter(badExperiencesAdapter);
            _utility.setListViewHeightBasedOnChildren(lvBadExp);
//        lvFunExp.setSelectionFromTop(MainActivity.allBrands.size() - 4, 0);
            lvBadExp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
//        loadMoreBadExp.setVisibility(View.VISIBLE);
    }

    private void noExpStatus() {
        loadMoreBadExp.setVisibility(View.GONE);
        tv_no_bad_exp.setVisibility(View.VISIBLE);
        lvBadExp.setVisibility(View.GONE);
    }


}
