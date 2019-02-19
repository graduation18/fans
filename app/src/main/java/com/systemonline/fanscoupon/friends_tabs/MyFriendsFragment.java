package com.systemonline.fanscoupon.friends_tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.Fan;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MyFriendsFragment extends BaseFragment {

    ListView lvAllFriends;
    TextView tv_no_friends;
    Button loadMore;
    boolean loadMoreFlag;
    JSONAsync call;
    Utility _utility;
    private int friendsLimit = 4, friendsPage = 0;
    private ArrayList<Fan> myFriends;
    private int lastFriendsSize;
    private CustomLoading customLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.friends_tab_myfriends, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());

        lvAllFriends = (ListView) root.findViewById(R.id.lv_my_friends);
        tv_no_friends = (TextView) root.findViewById(R.id.tv_no_friends);
        loadMore = (Button) root.findViewById(R.id.loadMoreAllFriends);
        getActivity().setTitle(getString(R.string.friends));


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.allFriends_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

//
                customLoading.showProgress(_utility.getCurrentActivity());
                friendsLimit = 4;
                requestAllFriends(friendsLimit, friendsPage);//refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadMoreFlag = true;
//
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllFriends(friendsLimit, friendsPage);//loadMore
            }
        });


        getScreenData();

        return root;
    }


    private void getScreenData() {
        customLoading.showProgress(_utility.getCurrentActivity());
        loadMoreFlag = false;
        if (_utility.isConnectingToInternet_ping()) {
            Log.e("get from cloud", "credit");
            requestAllFriends(friendsLimit, friendsPage);//on create
        } else {
//
            customLoading.hideProgress();
            _utility.showMessage(getResources().getString(R.string.no_net));
            noFriendsStatus();//no internet
        }
    }

    private void noFriendsStatus() {
        loadMore.setVisibility(View.GONE);
        tv_no_friends.setVisibility(View.VISIBLE);
        lvAllFriends.setVisibility(View.GONE);
    }

    /**
     * make a request to get all Brands
     *
     * @param limit
     */
    public void requestAllFriends(int limit, int offset) {
        if (_utility.isConnectingToInternet_ping()) {

            Log.e("all -- request friends", limit + "");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("index", String.valueOf(offset)));
            nameValuePairs.add(new BasicNameValuePair("limit", String.valueOf(limit)));


            JSONWebServices service = new JSONWebServices(MyFriendsFragment.this);
            call = service.getAllFriends(nameValuePairs);

        } else {
//
            customLoading.hideProgress();
            _utility.showMessage(getResources().getString(R.string.no_net));
        }
    }


    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("all credit response", "-------------");
            myFriends = ParseData.parseAllFriends(Result);
            if (myFriends != null) {
                if (loadMoreFlag) {
                    if (myFriends.size() == lastFriendsSize)
                        _utility.showMessage(getResources().getString(R.string.no_more));
                    else
                        showFriends();
                } else
                    showFriends();
            } else {
                _utility.showMessage(getResources().getString(R.string.ws_err));
            }


        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.ws_err));
        } finally {
//
            customLoading.hideProgress();
            call = null;

        }

    }

    private void showFriends() {
        Log.e("sort and show brands", "credit");
        if (myFriends.isEmpty())
            noFriendsStatus();//empty
        else {
            friendsLimit += 4;
            lastFriendsSize = myFriends.size();
            tv_no_friends.setVisibility(View.GONE);
            lvAllFriends.setVisibility(View.VISIBLE);
            MyFriendsAdapter myFriendsAdapter = new MyFriendsAdapter(getActivity(), myFriends);
            lvAllFriends.setAdapter(myFriendsAdapter);
            _utility.setListViewHeightBasedOnChildren(lvAllFriends);
            lvAllFriends.setSelectionFromTop(myFriends.size() - 4, 0);
            loadMore.setVisibility(View.VISIBLE);
        }
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


//}

}
