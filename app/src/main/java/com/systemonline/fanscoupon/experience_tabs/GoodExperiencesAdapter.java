package com.systemonline.fanscoupon.experience_tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.ContextThemeWrapper;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.Experience;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SystemOnline1 on 4/16/2017.
 */

public class GoodExperiencesAdapter extends BaseAdapter {

    public ArrayList<Experience> values;
    private Context con;
    //    private Experience experience;
    private Utility utility;
    private int datePointIndex;
    private CharSequence dateValue;
    private JSONAsync call;
    private String expSlug;
    private CustomLoading customLoading;

    GoodExperiencesAdapter(Context c, ArrayList<Experience> values) {
        super(c, R.layout.experience_tab_good, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
        customLoading = new CustomLoading(utility.getCurrentActivity());
    }

    @NonNull
    @Override
    public View getView(final int position, View row, @NonNull ViewGroup parent) {

//
        if (row == null) {
            LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            row = inflater.inflate(R.layout.experiences_row, parent, false);
        }
//        final View finalRow = row;
//        rootView = finalRow;
        final Experience experience = values.get(position);

        TextView tv_exp_author_name = (TextView) row.findViewById(R.id.tv_ex_writer);
        TextView tv_exp_time = (TextView) row.findViewById(R.id.experience_time);
        TextView tv_exp_title = (TextView) row.findViewById(R.id.tv_ex_title);
        TextView tv_exp_body = (TextView) row.findViewById(R.id.tv_ex_body);
        ImageView img_exp_author_name = (ImageView) row.findViewById(R.id.img_ex_writer);
        ImageView edit = (ImageView) row.findViewById(R.id.img_ex_edit);
        ImageView delete = (ImageView) row.findViewById(R.id.img_ex_delete);

        final String expSlug = experience.getExperienceSlug();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(new ContextThemeWrapper(con, R.style.myDialog));
                confirmationDialog.setMessage(con.getResources().getString(R.string.delete_experience))
                        .setCancelable(false)
                        .setPositiveButton(con.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                deleteExp(expSlug);
                            }
                        })
                        .setNegativeButton(con.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                confirmationDialog.create().show();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExperienceTab.experienceToEdit = ExperienceTab.allExperiences.get(0).get(position);
                Fragment fragment = new WriteExperienceFragment();
                FragmentManager fragmentManager = ((FragmentActivity) utility.getCurrentActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        tv_exp_author_name.setText(experience.getAuthorName());
        utility.setTextViewLines(tv_exp_author_name, experience.getAuthorName());
        if (experience.getExperienceTime().contains("."))
            datePointIndex = experience.getExperienceTime().indexOf('.');
        else
            datePointIndex = experience.getExperienceTime().length() - 1;
        dateValue = DateUtils.getRelativeTimeSpanString(getDateInMillis(experience.getExperienceTime().substring(0, datePointIndex)), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        tv_exp_time.setText(dateValue);
        utility.setTextViewLines(tv_exp_time, dateValue.toString());

        tv_exp_title.setText(experience.getExperienceTitle());
        utility.setTextViewLines(tv_exp_title, experience.getExperienceTitle());

        tv_exp_body.setText(experience.getExperienceBody());
        utility.setTextViewLines(tv_exp_body, experience.getExperienceBody());

//        tv_commenter.setText(comment.getCommentWriter().getFanFirstName() + " " + comment.getCommentWriter().getFanLastName());
//        utility.setTextViewLines(tv_commenter, comment.getCommentWriter().getFanFirstName() + " " + comment.getCommentWriter().getFanLastName());
        Picasso.with(getContext()).load(Const.imagesURL + "users/40x40/" + experience.getAuthorImg()).placeholder(R.drawable.ph_user).into(img_exp_author_name);


        return row;
    }

    private long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    /**
     * make a request to delete an exp
     *
     * @param expSlug
     */
    private void deleteExp(String expSlug) {
        try {
            if (utility.isConnectingToInternet_ping()) {
                customLoading.showProgress(utility.getCurrentActivity());
                JSONWebServices service = new JSONWebServices(GoodExperiencesAdapter.this);
                call = service.deleteExperienceRequest(null, expSlug);
                Log.e("send delete exp slug", expSlug);
                this.expSlug = expSlug;
            } else {
                customLoading.hideProgress();
                utility.showMessage(con.getResources().getString(R.string.no_net));
            }
        } catch (Exception e) {
            e.printStackTrace();
            utility.showMessage(con.getResources().getString(R.string.error));
        }
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("delete exp response", "------------- " + Result.toString());


            if (!ParseData.parseActionsResult(Result)) {
                utility.showMessage(utility.getCurrentActivity().getResources().getString(R.string.ws_err));
                customLoading.hideProgress();
//                utility.HideDialog(rootView);
            } else {
                Log.e("resp delete exp slug", expSlug);
                Log.e("update all", utility.updateExperiences(0, expSlug) + " -- ");
                this.notifyDataSetChanged();

                ;
                customLoading.hideProgress();
//                Log.e("good adapter hide",rootView.toString());
//                utility.HideDialog(rootView);

            }
        } catch (Exception e) {
            e.printStackTrace();
            utility.showMessage(utility.getCurrentActivity().getResources().getString(R.string.ws_err));
        } finally {
            customLoading.hideProgress();
//            utility.HideDialog(rootView);
            call = null;
        }
    }


}
