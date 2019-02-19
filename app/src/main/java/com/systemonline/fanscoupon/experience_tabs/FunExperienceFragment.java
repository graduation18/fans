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


public class FunExperienceFragment extends BaseFragment {

    TextView tv_no_fun_exp;
    ListView lvFunExp;
    Utility _utility;
    Button loadMoreFunExp;
    private CustomLoading customLoading;
//    boolean loadMoreFlag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.experience_tab_fun_experiences, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        tv_no_fun_exp = (TextView) root.findViewById(R.id.tv_no_funExp);
        tv_no_fun_exp.setVisibility(View.GONE);
//        Log.e("fun exp", "on create");

        lvFunExp = (ListView) root.findViewById(R.id.lv_fun_exp);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.funExp_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

//
                customLoading.showProgress(_utility.getCurrentActivity());
                ExperienceTab.requestAllExp(ExperienceTab.expLimit, ExperienceTab.expIndex, 3, FunExperienceFragment.this);//refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadMoreFunExp = (Button) root.findViewById(R.id.loadMoreFunExp);
//
        loadMoreFunExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customLoading.showProgress(_utility.getCurrentActivity());
                ExperienceTab.requestAllExp(ExperienceTab.allExperiences.get(2).size() + 4, ExperienceTab.expIndex, 3, FunExperienceFragment.this);//load more
            }
        });
        if (ExperienceTab.allExperiences.get(2).isEmpty())
            noExpStatus();
        else
            showExperiences();
        return root;
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("all exp response", "-------------");

            ExperienceTab.allExperiences.set(2, ParseData.parseAllExperiences(Result).get(2));

            if (ExperienceTab.allExperiences == null || ExperienceTab.allExperiences.get(2) == null) {
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


    private void showExperiences() {
        if (ExperienceTab.allExperiences.get(2).isEmpty())
            noExpStatus();
        else {
            Log.e("sort and show", "fun exp");
            tv_no_fun_exp.setVisibility(View.GONE);
            lvFunExp.setVisibility(View.VISIBLE);
            FunExperiencesAdapter funExperiencesAdapter = new FunExperiencesAdapter(getActivity(), ExperienceTab.allExperiences.get(2));
            lvFunExp.setAdapter(funExperiencesAdapter);
            _utility.setListViewHeightBasedOnChildren(lvFunExp);
//        lvFunExp.setSelectionFromTop(MainActivity.allBrands.size() - 4, 0);
            lvFunExp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
//        loadMoreFunExp.setVisibility(View.VISIBLE);
    }

    private void noExpStatus() {
        loadMoreFunExp.setVisibility(View.GONE);
        tv_no_fun_exp.setVisibility(View.VISIBLE);
        lvFunExp.setVisibility(View.GONE);
    }


}
