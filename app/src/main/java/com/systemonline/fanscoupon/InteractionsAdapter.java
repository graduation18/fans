package com.systemonline.fanscoupon;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Interaction;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;

public class InteractionsAdapter extends BaseAdapter {
    public static String FACEBOOK_PAGE_ID = "ElBaNnA.BeaCh";
    public ArrayList<Interaction> values;
    Context con;
    Interaction interaction;
    Utility utility;
    int poss;
    ShareDialog shareDialog;

    InteractionsAdapter(Context c, ArrayList<Interaction> values, int poss) {
        super(c, R.layout.social_activities_row, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
        this.poss = poss;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
//        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (row == null) {
            LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            row = inflater.inflate(R.layout.social_activities_row, parent, false);

        }
        interaction = values.get(position);
        shareDialog = new ShareDialog(utility.getCurrentActivity());


        ImageView missionStatus = (ImageView) row.findViewById(R.id.missionStatus);
        TextView missionLink = (TextView) row.findViewById(R.id.tv_missionLink);


        final String link = interaction.getLink();
        final String name = interaction.getMissionName();

        missionLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("link", link);

                if (poss == 0 && name.contains("share")) {
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(link))
                                .build();
                        shareDialog.show(linkContent);
                    }
                } else if (poss == 1 && name.contains("share")) {
                    TweetComposer.Builder builder = new TweetComposer.Builder(con)
                            .text(link);
                    builder.show();
                } else {
                    utility.goToLink(link);
                }

/*
                switch (poss) {
                    case 0:
                        Log.e("p0s", "0");

                        if (name.contains("share")) {
                        } else {
                            try {
//                                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
//                                String facebookUrl = getFacebookPageURL(con,link);
//                                facebookIntent.setData(Uri.parse(facebookUrl));
//                                con.startActivity(facebookIntent);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        break;

                    case 1:
                        Log.e("p0s", "1");

                        if (name.contains("share")) {

                        } else  {
//                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
//                            StatusesService statusesService = twitterApiClient.getStatusesService();
//                            Call<Tweet> call = statusesService.show(524971209851543553L, null, null, null);
//                            call.enqueue(new Callback<Tweet>() {
//                                @Override
//                                public void success(Result<Tweet> result) {
//                                    //Do something with result
//                                }
//
//                                public void failure(TwitterException exception) {
//                                    //Do something on failure
//                                }
//                            });

                            Intent tw = new Intent(Intent.ACTION_VIEW);
                            tw.setData(Uri.parse(link));
                            con.startActivity(tw);
                        }

                        break;

                    case 2:
                        Log.e("p0s", "2");

                        Intent ut = new Intent(Intent.ACTION_VIEW);
                        ut.setData(Uri.parse(link));
                        con.startActivity(ut);

                        break;

                    case 3:
                        Log.e("p0s", "3");

                        Intent ins = new Intent(Intent.ACTION_VIEW);
                        ins.setData(Uri.parse(link));
                        con.startActivity(ins);

                        break;
                }
*/


/*
                try {
                    Log.e("link", link);

//                    Intent i = new Intent();
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
//                    i.setAction("android.intent.action.VIEW");
//                    i.addCategory("android.intent.category.BROWSABLE");
                    i.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
                    i.setData(Uri.parse(link));
                    con.startActivity(i);
                } catch (Exception e) {
                    Log.e("link", "catch");
                    e.printStackTrace();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(interaction.getLink()));
                    utility.getCurrentActivity().startActivity(i);
                }

*/

            }
        });


        TextView missionName = (TextView) row.findViewById(R.id.tv_missionName);
        missionName.setText(interaction.getMissionName());
        utility.setTextViewLines(missionName, interaction.getMissionName());
        if (interaction.isDone())
            missionStatus.setImageResource(R.drawable.right);
        else
            missionStatus.setImageResource(R.drawable.wrong);

        return row;
    }

//    public String getFacebookPageURL(Context context,String link) {
////        PackageManager packageManager = context.getPackageManager();
//        try {
////            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
////            if (versionCode >= 3002850) { //newer versions of fb app
//                return "fb://facewebmodal/f?href=" + link;
////            } else { //older versions of fb app
////                return "fb://page/" + FACEBOOK_PAGE_ID;
//////                return "fb://profile/" + FACEBOOK_PROFILE_ID;
//////                return "fb://note/" + FACEBOOK_NOTE_ID;
////            }
//        } catch (Exception e) {
////        } catch (PackageManager.NameNotFoundException e) {
//            return link; //normal web url
//        }
//    }
}