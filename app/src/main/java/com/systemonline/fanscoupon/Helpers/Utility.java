package com.systemonline.fanscoupon.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.FbMainFragment;
import com.systemonline.fanscoupon.MainActivity;
import com.systemonline.fanscoupon.Model.Brand;
import com.systemonline.fanscoupon.Model.CustomerType;
import com.systemonline.fanscoupon.Model.Fan;
import com.systemonline.fanscoupon.Model.MyCircleImageView;
import com.systemonline.fanscoupon.MyAccountFragment;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;
import com.systemonline.fanscoupon.experience_tabs.ExperienceTab;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import static android.os.Looper.getMainLooper;

public class Utility {

    // approximate large numbers
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    public static Activity _CurrentActivity = null;
    private static ProgressDialog ProgressDialog;

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    SpannableStringBuilder builder;
    String strBuild;
    SpannableString strColor;
    private CharSequence text;
    private int duration;
    private Toast toast;
    private Context context;
    private AVLoadingIndicatorView avi;

    public Utility(Context context) {
        this.context = context;
//        ProgressDialog = new ProgressDialog(context);
        avi = new AVLoadingIndicatorView(context);
    }

    public Context GetAppContext() {
        return getCurrentActivity().getBaseContext();
    }

    public void SetCurrentActivity(Activity myActivity) {
        _CurrentActivity = myActivity;
    }

    public Activity getCurrentActivity() {
        return _CurrentActivity;
    }

    /**
     * Show alertdialog
     *
     * @param msg
     */
    public void ShowDialog(String msg, boolean cancelable) {
        ProgressDialog = new ProgressDialog(getCurrentActivity());
        ProgressDialog.setMessage(msg);
        ProgressDialog.show();
        ProgressDialog.setCancelable(cancelable);
        ProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showMessage(getCurrentActivity().getString(R.string.request_cancelled));
                MainActivity.jsonAsync.cancel(true);
                getCurrentActivity().onBackPressed();
            }
        });
//        startAnim();
    }

    /**
     * Hide alertdialog
     */
    public void HideDialog() {
        ProgressDialog.dismiss();
//        stopAnim();
    }
//
//    /**
//     * Show alertdialog
//     *
//     * @param view
//     */
//    public void ShowDialog(View view) {
//        startAnim(view);
////        WindowManager.LayoutParams lp = getCurrentActivity().getWindow().getAttributes();
////        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//////        lp.dimAmount=1.0f;
////        lp.screenBrightness=0.05f;
////        getCurrentActivity().getWindow().setAttributes(lp);
//    }
//
//    /**
//     * Hide alertdialog
//     */
//    public void HideDialog(View view) {
//        stopAnim(view);
////        WindowManager.LayoutParams lp = getCurrentActivity().getWindow().getAttributes();
////        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//////        lp.dimAmount=0.0f;
////        try {
////            lp.screenBrightness= (float) Settings.System.getInt(
////                    getCurrentActivity().getContentResolver(),
////                    Settings.System.SCREEN_BRIGHTNESS);
////        } catch (Settings.SettingNotFoundException e) {
////            e.printStackTrace();
////        }
////
////        getCurrentActivity().getWindow().setAttributes(lp);
//    }
//
//    private void startAnim(View view) {
//        Log.e("loader", "show " + view.toString());
//
//
////        AVLoadingIndicatorView avl = (AVLoadingIndicatorView) MainActivity.currentView.findViewById(R.id.avi);
//        AVLoadingIndicatorView avl = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
//        avl.setVisibility(View.VISIBLE);
//        avl.bringToFront();
//        avl = null;
//
////        RelativeLayout rel_dim = (RelativeLayout) view.findViewById(R.id.rel_dimView);
////        rel_dim.setAlpha(0.3f);
////        rel_dim.getBackground().setAlpha(100);
////        avi.show();
//        // or avi.smoothToShow();
//    }
//
//    private void stopAnim(View view) {
//        Log.e("loader", "hide " + view.toString());
////        MainActivity.currentView.findViewById(R.id.avi).setVisibility(View.GONE);
//        AVLoadingIndicatorView avl = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
//        avl.setVisibility(View.GONE);
//        avl = null;
////        RelativeLayout rel_dim = (RelativeLayout) view.findViewById(R.id.rel_dimView);
////        rel_dim.setAlpha(1.0f);
////        rel_dim.getBackground().setAlpha(255);
//
////        avi.hide();
//        // or avi.smoothToHide();
//    }
//
////    public void startAnim(CustomLoading cShowProgress) {
////        cShowProgress.showProgress(getCurrentActivity());
////    }
////
////    public void stopAnim(CustomLoading cShowProgress) {
////        cShowProgress.hideProgress();
////    }

    /**
     * Checking for all possible gps providers
     **/
    public boolean checkGPS() {
        final LocationManager manager = (LocationManager) GetAppContext().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        }
        return true;
    }

    /**
     * convert arabic date to english
     *
     * @param number
     * @return English date format
     */
    public String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }

    /**
     * set the properties of the spinner
     *
     * @param stAr
     * @param spin
     */
//    public void spinnerProperties(String[] stAr, Spinner spin) {
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getCurrentActivity(), R.layout.simple_list, stAr) {
//            public View getView(int position, View convertView, android.view.ViewGroup parent) {
//                TextView v = (TextView) super.getView(position, convertView, parent);
//                v.setTextColor(Color.BLACK);
//                v.setTextSize(16);
//                return v;
//            }
//
//            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
//                TextView v = (TextView) super.getView(position, convertView, parent);
//                v.setTextColor(Color.BLACK);
//                v.setTextSize(16);
//                return v;
//            }
//        };
//        adapter1.setDropDownViewResource(R.layout.simple_drop);
//
//        spin.setAdapter(adapter1);
//    }
    public void spinnerProperties(String[] stAr, Spinner spin) {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getCurrentActivity(), R.layout.simple_list, stAr) {
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTextColor(Color.BLACK);
                v.setTextSize(16);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTextColor(Color.BLACK);
                v.setTextSize(16);
                return v;
            }
        };
        adapter1.setDropDownViewResource(R.layout.simple_drop);

        spin.setAdapter(adapter1);
    }


    /**
     * Checking for connected internet provider by making ping to server
     **/
    public boolean isConnectingToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) GetAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Checking for connected internet provider
     **/
    public boolean isConnectingToInternet_ping() {

        ConnectivityManager cm =
                (ConnectivityManager) GetAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();


//        if (netInfo != null && netInfo.isConnected()) {
//            return isOnline();
//        } else
//            return false;
    }

    /**
     * checking for active internet connection
     *
     * @return
     */
    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * determine list view suitable height.
     *
     * @param listView
     */

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                // VIP note:
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.setFocusable(false);
        listView.requestLayout();
    }

    /**
     * Show hover above the fragment page
     */
    public void showHover(final String type) {
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Dialog added;
                    added = new Dialog(getCurrentActivity());
                    added.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    switch (type) {
                        case "add":
                            added.setContentView(R.layout.pop_up_added);
                            break;
                        case "pass":
                            added.setContentView(R.layout.pop_up_passed);
                            break;
                        case "fail":
                            added.setContentView(R.layout.pop_up_failed);
                            break;
                    }

                    added.setCancelable(true);
                    added.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void popUpPassed() {
        Dialog usedDialog = new Dialog(getCurrentActivity());
        usedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        usedDialog.setContentView(R.layout.pop_up_passed);
        usedDialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = usedDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        usedDialog.show();
    }

    /**
     * get image uri from bitmap
     *
     * @param inContext
     * @param inImage
     * @return
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "", "");
        return Uri.parse(path);
    }

    public void showMessage(String Message, boolean IsLong) {

        if (getCurrentActivity() != null) {
            context = getCurrentActivity().getApplicationContext();

            if (context != null) {
                text = Message;
                duration = (IsLong) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
                toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }

    public void showMessage(String Message) {
        showMessage(Message, false);
    }

    public void loginDialogBox() {
        AlertDialog.Builder build = new AlertDialog.Builder(getCurrentActivity());
        build.setMessage(getCurrentActivity().getResources().getString(R.string.plz_relogin))
                .setCancelable(false)
                .setPositiveButton(getCurrentActivity().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        ((MainActivity) getCurrentActivity()).fragmentTransaction(new FbMainFragment());
                    }
                })
                .setNegativeButton(getCurrentActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        build.show();
    }

    public void fbLoginDialogBox() {
        AlertDialog.Builder build = new AlertDialog.Builder(getCurrentActivity());
        build.setMessage(getCurrentActivity().getResources().getString(R.string.plz_relogin_fb))
                .setCancelable(false)
                .setPositiveButton(getCurrentActivity().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        ((MainActivity) getCurrentActivity()).fragmentTransaction(new FbMainFragment());
                    }
                })
                .setNegativeButton(getCurrentActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        getCurrentActivity().onBackPressed();
                    }
                });
        build.show();
    }

    public void socialAccountDialogBox(String message) {
        AlertDialog.Builder build = new AlertDialog.Builder(getCurrentActivity());
        build.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getCurrentActivity().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        ((MainActivity) getCurrentActivity()).fragmentTransaction(new MyAccountFragment());
                    }
                })
                .setNegativeButton(getCurrentActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        getCurrentActivity().onBackPressed();
                    }
                });

        build.show();
    }


    public synchronized void createDynamicImageViews(String type, ArrayList objects, LinearLayout Linear) {
        Linear.removeAllViews();
        int size;
        size = objects.size();
//        Log.e("create views size",size+"--");
        int arrowSize = 50;
        if (type.equals("friends") || type.equals("customerTypes") || type.equals("Alliances"))
            arrowSize = 30;
        if (!type.equals("brands"))
            drawLeftRightArrows(Linear, R.drawable.left_arrow, arrowSize);
        MyCircleImageView image;
        LinearLayout.LayoutParams imgvwDimens;
        int dimens, finalDimens, dimensMargin, finalDimensMargin;
        float density, densityMargin;
        for (int i = 0; i < size; i++) {
            image = new MyCircleImageView(getCurrentActivity());
            image.setBorderColor(getCurrentActivity().getResources().getColor(R.color.grey_bg2));
            image.requestLayout();
            Linear.addView(image);
            dimens = 30;
            if (type.equals("Badges"))
                dimens = 50;
            else if (type.equals("brands"))
                dimens = 47;
            density = getCurrentActivity().getResources().getDisplayMetrics().density;
            finalDimens = (int) (dimens * density);

            dimensMargin = 5;
            densityMargin = getCurrentActivity().getResources().getDisplayMetrics().density;
            finalDimensMargin = (int) (dimensMargin * densityMargin);

            imgvwDimens = new LinearLayout.LayoutParams(finalDimens, finalDimens);
            imgvwDimens.setMargins(finalDimensMargin, 0, finalDimensMargin, 0);
            image.setLayoutParams(imgvwDimens);

            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            switch (type) {
                case "friends":
                    Picasso.with(getCurrentActivity()).load(Const.imagesURL + "users/40x40/" +
                            ((Fan) objects.get(i)).getFanImage()).placeholder(R.drawable.ph_user).into(image);
                    break;
                case "Alliances":
                    Picasso.with(getCurrentActivity()).load(Const.imagesURL + "brands/50x50/" +
                            ((Brand) objects.get(i)).getBrandImage()).placeholder(R.drawable.ph_brand).into(image);
                    break;
                case "customerTypes":
                    Picasso.with(getCurrentActivity()).load(Const.imagesURL + "customer-types/50x50/" +
                            ((CustomerType) objects.get(i)).getCustomerTypeImage()).placeholder(R.drawable.ph_badge).into(image);
                    break;
                case "Badges":
                    Picasso.with(getCurrentActivity()).load(Const.imagesURL + "badges/" +
                            ((CustomerType) objects.get(i)).getCustomerTypeImage()).placeholder(R.drawable.ph_badge).into(image);
                    break;
                case "brands":
                    Picasso.with(getCurrentActivity()).load(Const.imagesURL + "brands/50x50/" +
                            objects.get(i)).placeholder(R.drawable.ph_brand).into(image);
                    break;
            }

        }
        if (!type.equals("brands"))
            drawLeftRightArrows(Linear, R.drawable.right_arrow, arrowSize);

    }

    public synchronized void createDynamicImageViews(ArrayList<CustomerType> objects, LinearLayout Linear) {
        Linear.removeAllViews();
        int size;
        size = objects.size();
//        Log.e("create views size",size+"--");
//        int arrowSize = 50;
//        if (type.equals("friends") || type.equals("customerTypes") || type.equals("Alliances"))
//            arrowSize = 30;
//        drawLeftRightArrows(Linear, R.drawable.left_arrow, arrowSize);
        MyCircleImageView image;
        TextView badgeCount;
        LinearLayout.LayoutParams imgvwDimens;
        int dimens, finalDimens, dimensMargin, finalDimensMargin;
        float density, densityMargin;
        for (int i = 0; i < size; i++) {
            image = new MyCircleImageView(getCurrentActivity());
            image.setBorderColor(getCurrentActivity().getResources().getColor(R.color.grey_bg2));
            image.requestLayout();
            Linear.addView(image);
            dimens = 50;
            density = getCurrentActivity().getResources().getDisplayMetrics().density;
            finalDimens = (int) (dimens * density);

            dimensMargin = 5;
            densityMargin = getCurrentActivity().getResources().getDisplayMetrics().density;
            finalDimensMargin = (int) (dimensMargin * densityMargin);

            imgvwDimens = new LinearLayout.LayoutParams(finalDimens, finalDimens);
            imgvwDimens.setMargins(finalDimensMargin, 0, finalDimensMargin, 0);
            image.setLayoutParams(imgvwDimens);

            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(getCurrentActivity()).load(Const.imagesURL + "customer-types/50x50/" +
                    objects.get(i).getCustomerTypeImage()).placeholder(R.drawable.ph_badge).into(image);


            badgeCount = new TextView(getCurrentActivity());
//            badgeCount.setBorderColor(getCurrentActivity().getResources().getColor(R.color.grey_bg2));
            badgeCount.requestLayout();
            Linear.addView(badgeCount);

            dimensMargin = 5;
            densityMargin = getCurrentActivity().getResources().getDisplayMetrics().density;
            finalDimensMargin = (int) (dimensMargin * densityMargin);
            imgvwDimens = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, finalDimens);
            imgvwDimens.setMargins(0, 0, finalDimensMargin * 4, 0);
            badgeCount.setLayoutParams(imgvwDimens);
            badgeCount.setGravity(Gravity.CENTER);
            badgeCount.setTextSize(15.0f);
            badgeCount.setTextColor(getCurrentActivity().getResources().getColor(R.color.black));
            badgeCount.setText(String.valueOf(objects.get(i).getCustomerTypeCount()));
//            badgeCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ph_badge, 0, 0, 0);

//            badgeCount.setScaleType(ImageView.ScaleType.CENTER_CROP);


//            switch (type) {
//                case "friends":
//                    Picasso.with(getCurrentActivity()).load(Const.imagesURL + "users/40x40/" +
//                            ((Fan) objects.get(i)).getFanImage()).placeholder(R.drawable.ph_user).into(image);
//                    break;
//                case "Alliances":
//                    Picasso.with(getCurrentActivity()).load(Const.imagesURL + "brands/50x50/" +
//                            ((Brand) objects.get(i)).getBrandImage()).placeholder(R.drawable.ph_brand).into(image);
//                    break;
//                case "customerTypes":
//                    Picasso.with(getCurrentActivity()).load(Const.imagesURL + "customer-types/50x50/" +
//                            ((CustomerType) objects.get(i)).getCustomerTypeImage()).placeholder(R.drawable.ph_badge).into(image);
//                    break;
//                case "Badges":
//                    Picasso.with(getCurrentActivity()).load(Const.imagesURL + "badges/" +
//                            ((CustomerType) objects.get(i)).getCustomerTypeImage()).placeholder(R.drawable.ph_badge).into(image);
//                    break;
//                case "brands":
//                    Picasso.with(getCurrentActivity()).load(Const.imagesURL + "brands/50x50/" +
//                            objects.get(i)).placeholder(R.drawable.ph_brand).into(image);
//                    break;
//            }

        }
//        drawLeftRightArrows(Linear, R.drawable.right_arrow, arrowSize);

    }

    private void drawLeftRightArrows(LinearLayout linear, int arrow, int size) {
        MyCircleImageView image;
        LinearLayout.LayoutParams imgvwDimens;
        int dimens, finalDimens, dimensMargin, finalDimensMargin;
        float density, densityMargin;
        image = new MyCircleImageView(getCurrentActivity());
        image.setBorderColor(getCurrentActivity().getResources().getColor(R.color.grey_bg2));
        image.requestLayout();
        linear.addView(image);
        dimens = size;
        density = getCurrentActivity().getResources().getDisplayMetrics().density;
        finalDimens = (int) (dimens * density);

        dimensMargin = 5;
        densityMargin = getCurrentActivity().getResources().getDisplayMetrics().density;
        finalDimensMargin = (int) (dimensMargin * densityMargin);

        imgvwDimens = new LinearLayout.LayoutParams(finalDimens, finalDimens);
        imgvwDimens.setMargins(finalDimensMargin, 0, finalDimensMargin, 0);
        image.setLayoutParams(imgvwDimens);

        image.setImageResource(arrow);
    }

    public void createDynamicStars(double size, LinearLayout Linear) {
        Linear.removeAllViews();
        for (int i = 0; i < 5; i++) {
            ImageView image = new ImageView(getCurrentActivity());
            image.requestLayout();
            Linear.addView(image);
            int dimens = 15;
            float density = getCurrentActivity().getResources().getDisplayMetrics().density;
            int finalDimens = (int) (dimens * density);
            LinearLayout.LayoutParams imgvwDimens =
                    new LinearLayout.LayoutParams(finalDimens, finalDimens);
            image.setLayoutParams(imgvwDimens);

            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            int dimensMargin = 5;
            float densityMargin = getCurrentActivity().getResources().getDisplayMetrics().density;
            int finalDimensMargin = (int) (dimensMargin * densityMargin);

            LinearLayout.LayoutParams imgvwMargin =
                    new LinearLayout.LayoutParams(finalDimens, finalDimens);
            imgvwMargin.setMargins
                    (finalDimensMargin, finalDimensMargin, finalDimensMargin, finalDimensMargin);
            if (i < size)
                image.setImageResource(R.drawable.star_full);
            else
                image.setImageResource(R.drawable.star_empty);

        }
    }

    /**
     * Update all coupons listView after making add to fav to specific coupon
     *
     * @param type
     */

    public void updateCoupons(String type) {
        Log.e("update coupons", "qualified fragment");
        int allCouponsSize;
        if (type.equals("wallet")) {

            for (int k = 0; k < CouponTab.allCoupons2.size(); k++) {
                allCouponsSize = CouponTab.allCoupons2.get(k).size();
                for (int i = 0; i < allCouponsSize; i++) {
                    if (CouponTab.allCoupons2.get(k).get(i).getCouponSlug().equals(CouponTab.coupSlugTemp)) {
                        CouponTab.allCoupons2.get(k).get(i).setCouponSavedByCount(CouponTab.allCoupons2.get(k).get(i).getCouponSavedByCount() + 1);
                        CouponTab.allCoupons2.get(k).get(i).setHasSaved(true);
//                        return;
                    }
                }
            }


            for (int k = 0; k < CouponTab.forYou2.size(); k++) {
                allCouponsSize = CouponTab.forYou2.get(k).size();
                for (int i = 0; i < allCouponsSize; i++) {
                    if (CouponTab.forYou2.get(k).get(i).getCouponSlug().equals(CouponTab.coupSlugTemp)) {
                        CouponTab.forYou2.get(k).get(i).setCouponSavedByCount(CouponTab.forYou2.get(k).get(i).getCouponSavedByCount() + 1);
                        CouponTab.forYou2.get(k).get(i).setHasSaved(true);
//                        return;
                    }
                }
            }

        } else {
            if (MainActivity.selectedCoupon != null)
                MainActivity.selectedCoupon.getCouponChallenges().setMissionStatus(true);

            for (int k = 0; k < CouponTab.allCoupons2.size(); k++) {
                allCouponsSize = CouponTab.allCoupons2.get(k).size();
                for (int i = 0; i < allCouponsSize; i++) {
                    if (CouponTab.allCoupons2.get(k).get(i).getCouponSlug().equals(CouponTab.coupSlugTemp)) {
                        CouponTab.allCoupons2.get(k).get(i).getCouponChallenges().setMissionStatus(true);
//                        return;
                    }
                }
            }

            for (int k = 0; k < CouponTab.forYou2.size(); k++) {
                allCouponsSize = CouponTab.forYou2.get(k).size();
                for (int i = 0; i < allCouponsSize; i++) {
                    if (CouponTab.forYou2.get(k).get(i).getCouponSlug().equals(CouponTab.coupSlugTemp)) {
                        CouponTab.forYou2.get(k).get(i).getCouponChallenges().setMissionStatus(true);
//                        return;
                    }
                }
            }
        }


    }

    public boolean updateExperiences(int expType, String expSlug) {
        Log.e("update experiences", "all");
        int allExpSize = ExperienceTab.allExperiences.size();
        for (int i = 0; i < allExpSize; i++) {
            if (ExperienceTab.allExperiences.get(expType).get(i).getExperienceSlug().equals(expSlug)) {
                ExperienceTab.allExperiences.get(expType).remove(i);
                return true;
            }
        }
        return false;
    }

    public void setTextViewLines(TextView textView, String fullString) {
        WindowManager wm = (WindowManager) getCurrentActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth(); // Get full screen width
        int eightyPercent = (screenWidth * 80) / 100; // Calculate 80% of it
        float textWidth = textView.getPaint().measureText(fullString);
// this method will give you the total width required to display total String

        int numberOfLines = ((int) textWidth / eightyPercent) + 1;
// calculate number of lines it might take
        textView.setLines(numberOfLines);
    }

    public String formatNumber(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatNumber(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatNumber(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public void goToLink(String link) {
        Intent in = new Intent(Intent.ACTION_VIEW);
        in.setData(Uri.parse(link));
        Log.e("goToLink fn", link);
        getCurrentActivity().startActivity(in);
    }

    public SpannableStringBuilder colorString(int string, int color1, String data, int color2) {
        builder = new SpannableStringBuilder();

        strBuild = getCurrentActivity().getResources().getString(string);
        strColor = new SpannableString(strBuild);
        strColor.setSpan(new ForegroundColorSpan(getCurrentActivity().getResources().getColor(color1)), 0, strBuild.length(), 0);
        builder.append(strColor);

        builder.append(" ");

        strColor = new SpannableString(data);
        strColor.setSpan(new ForegroundColorSpan(getCurrentActivity().getResources().getColor(color2)), 0, data.length(), 0);
        builder.append(strColor);

        return builder;
    }

    public SpannableStringBuilder colorString(String data, int color1, int string, int color2) {
        builder = new SpannableStringBuilder();

        strColor = new SpannableString(data);
        strColor.setSpan(new ForegroundColorSpan(getCurrentActivity().getResources().getColor(color1)), 0, data.length(), 0);
        builder.append(strColor);

        builder.append(" ");

        strBuild = getCurrentActivity().getResources().getString(string);
        strColor = new SpannableString(strBuild);
        strColor.setSpan(new ForegroundColorSpan(getCurrentActivity().getResources().getColor(color2)), 0, strBuild.length(), 0);
        builder.append(strColor);

        return builder;
    }

}