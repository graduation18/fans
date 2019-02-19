package com.systemonline.fanscoupon;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.json.JSONTokener;

public class MyAccountFragment extends BaseFragment {

    Utility _utility;
    TextView full_name, user_name, first_name, last_name, mail, birthdate, phone, mob, address, city_name, country, couponsCount;
    ImageView imgv_pp;
    ListView lv_pages;
    LinearLayout lin_edit_acc, lin_gplus_acc, lin_tw_acc, lin_fb_acc;
    FanPagesAdapter fanPagesAdapter;
    JSONAsync call;
    RelativeLayout rel_pages;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_account, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        lin_edit_acc = (LinearLayout) rootView.findViewById(R.id.lin_edit_acc);
        lin_fb_acc = (LinearLayout) rootView.findViewById(R.id.lin_fb_acc);
        lin_tw_acc = (LinearLayout) rootView.findViewById(R.id.lin_tw_acc);
        lin_gplus_acc = (LinearLayout) rootView.findViewById(R.id.lin_gplus_acc);
        lin_fb_acc.setVisibility(View.GONE);
        lin_tw_acc.setVisibility(View.GONE);
        lin_gplus_acc.setVisibility(View.GONE);
        couponsCount = (TextView) rootView.findViewById(R.id.couponsNumber);
        user_name = (TextView) rootView.findViewById(R.id.user_name);
        first_name = (TextView) rootView.findViewById(R.id.first_name);
        full_name = (TextView) rootView.findViewById(R.id.full_name);
        last_name = (TextView) rootView.findViewById(R.id.last_name);
        mail = (TextView) rootView.findViewById(R.id.mail);
        imgv_pp = (ImageView) rootView.findViewById(R.id.imgv_pp);
        birthdate = (TextView) rootView.findViewById(R.id.birthdate);
        phone = (TextView) rootView.findViewById(R.id.phone);
        mob = (TextView) rootView.findViewById(R.id.mob);
        address = (TextView) rootView.findViewById(R.id.address);
        city_name = (TextView) rootView.findViewById(R.id.city_name);
        country = (TextView) rootView.findViewById(R.id.country);
        lv_pages = (ListView) rootView.findViewById(R.id.lv_pages);
        rel_pages = (RelativeLayout) rootView.findViewById(R.id.rel_pages);

        lin_edit_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Fragment fragment = new EditAccountFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();

                getActivity().getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.fragment_container, new EditAccountFragment()).
                        addToBackStack(null).
                        commit();
            }
        });

        getActivity().setTitle(getString(R.string.my_account));
        requestMyProfile();

        return rootView;
    }

    private void requestMyProfile() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        Log.e("My -- request ", " profile");
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(MyAccountFragment.this);
        call = service.getMyProfile(null);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;
            MainActivity.myProfile = ParseData.parseMyProfile(Result);
            if (MainActivity.myProfile == null) {
                _utility.showMessage(getResources().getString(R.string.ws_err));
                customLoading.hideProgress();
                return;
            }

            if (MainActivity.myProfile.getFanFbPages().size() > 0) {
                fanPagesAdapter = new FanPagesAdapter(getContext(), MainActivity.myProfile.getFanFbPages());
                lv_pages.setAdapter(fanPagesAdapter);
                _utility.setListViewHeightBasedOnChildren(lv_pages);

                lv_pages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        _utility.goToLink("https://www.facebook.com/" + MainActivity.myProfile.getFanFbPages().get(i).getPageFbId());
                    }
                });
            } else {
                rel_pages.setVisibility(View.GONE);
            }


            couponsCount.setText(String.valueOf(MainActivity.myProfile.getCouponCount()));
            full_name.setText(MainActivity.myProfile.getFanFirstName());
            if (!MainActivity.myProfile.getFanUserName().equals("null"))
                user_name.setText(MainActivity.myProfile.getFanUserName());
            first_name.setText(MainActivity.myProfile.getFanFirstName());
            last_name.setText(MainActivity.myProfile.getFanLastName());
            mail.setText(MainActivity.myProfile.getFanEMAil());
//            save image to shared preference and current fan
            MainActivity.editor.putString("admin_picture", MainActivity.myProfile.getFanImage());
            MainActivity.editor.commit();

            Picasso.with(getContext()).load(Const.imagesURL + "users/40x40/" + MainActivity.myProfile.getFanImage()).placeholder(R.drawable.ph_user).into(imgv_pp);
//            imgv_pp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e("notify", "here");
//
//                    NotificationManager notificationManager = (NotificationManager) _utility.getCurrentActivity().getSystemService(NOTIFICATION_SERVICE);
//
//                    Drawable d = _utility.getCurrentActivity().getResources().getDrawable(R.drawable.avatar);
//                    Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
//                    d = _utility.getCurrentActivity().getResources().getDrawable(R.drawable.fans_coupons);
//                    Bitmap bitmap2 = ((BitmapDrawable) d).getBitmap();
//
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                        Notification notif = new Notification.Builder(_utility.getCurrentActivity())
//                                .setContentTitle("Title")
//                                .setContentText("content")
//                                .setSmallIcon(R.drawable.logo)
//                                .setLargeIcon(bitmap)
//                                .setStyle(new Notification.BigPictureStyle()
//                                        .bigPicture(bitmap2)
//                                        .setBigContentTitle("big title"))
//                                .build();
//                        notificationManager.notify(0, notif);
//
//                    }
//                }
//            });
            if (!MainActivity.myProfile.getFanBirthDate().equals("null"))
                birthdate.setText(MainActivity.myProfile.getFanBirthDate());
            if (!MainActivity.myProfile.getFanPhone().equals("null"))
                phone.setText(MainActivity.myProfile.getFanPhone());
            if (!MainActivity.myProfile.getFanMob().equals("null"))
                mob.setText(MainActivity.myProfile.getFanMob());
            if (!MainActivity.myProfile.getFanAddress().equals("null"))
                address.setText(MainActivity.myProfile.getFanAddress());
            if (!MainActivity.myProfile.getFanCity().equals("null"))
                city_name.setText(MainActivity.myProfile.getFanCity());
            country.setText(MainActivity.myProfile.getFanCountry());

            for (int i = 0; i < MainActivity.myProfile.getSocialAccounts().size(); i++) {
                if (MainActivity.myProfile.getSocialAccounts().get(i).getSocialNetworkType() == 13) {
                    final String link = "https://www.facebook.com/" + MainActivity.myProfile.getSocialAccounts().get(i).getSocialNetworkAccountID();
                    lin_fb_acc.setVisibility(View.VISIBLE);
                    lin_fb_acc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            _utility.goToLink(link);
                        }
                    });
                } else if (MainActivity.myProfile.getSocialAccounts().get(i).getSocialNetworkType() == 14) {
                    final String link = "https://www.twitter.com/" + MainActivity.myProfile.getSocialAccounts().get(i).getAccountName();
                    lin_tw_acc.setVisibility(View.VISIBLE);
                    lin_tw_acc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            _utility.goToLink(link);
                        }
                    });
                } else if (MainActivity.myProfile.getSocialAccounts().get(i).getSocialNetworkType() == 15) {
                    final String link = "https://www.youtube.com/channel/" + MainActivity.myProfile.getSocialAccounts().get(i).getSocialNetworkAccountID();
                    lin_gplus_acc.setVisibility(View.VISIBLE);
                    lin_gplus_acc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            _utility.goToLink(link);
                        }
                    });
                } else if (MainActivity.myProfile.getSocialAccounts().get(i).getSocialNetworkType() == 16) {
//                    final String link = "https://www.instagram.com/" + MainActivity.myProfile.getSocialAccounts().get(i).getAccountName();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.ws_err));

        } finally {
            customLoading.hideProgress();
        }
    }
}