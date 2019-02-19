package com.systemonline.fanscoupon.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.systemonline.fanscoupon.Model.CampaignLocation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.systemonline.fanscoupon.Helpers.Const.DATABASE_NAME;
import static com.systemonline.fanscoupon.Helpers.Const.DATABASE_VERSION;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
//    private static final int DATABASE_VERSION = 1;

    // Database Name
//    private static final String DATABASE_NAME = "fansCoupon";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * creating the database
     */
    public void createDB() {
        Log.e("databaseHandler", "create database");
        SQLiteDatabase db = this.getReadableDatabase();
        try {
//            for (int i = 0; i < SQLScript.sql.length; i++) {
//                Log.e("query", "index " + i + " ---" + SQLScript.sql[i].substring(0, 30));
            db.execSQL(SQLScript.campaignLocationTable);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dropDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
//            for (int i = 0; i < SQLScript.tables.length; i++) {
            db.execSQL("DROP TABLE IF EXISTS `campaign_location`");
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean insertCampLocations(ArrayList<CampaignLocation> campaignLocations) {
        try {
            for (int i = 0; i < campaignLocations.size(); i++) {
                insertBranch(campaignLocations.get(i));
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void insertBranch(CampaignLocation campaignLocation) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put("branch_id", campaignLocation.getBranchId());
        values.put("camp_id", campaignLocation.getCampaignID());
        values.put("longitude", String.valueOf(campaignLocation.getCampaignLong()));
        values.put("latitude", String.valueOf(campaignLocation.getCampaignLat()));
        values.put("branch_notify_range", campaignLocation.getBranchNotifyRange());
        values.put("coup_id", campaignLocation.getCampaignCouponId());
        values.put("coup_slug", campaignLocation.getCampaignCouponSlug().trim());
        values.put("coup_type", campaignLocation.getCampaignCouponType().trim());
        values.put("coup_img", campaignLocation.getCampaignCouponImg().trim());
        values.put("camp_end_date", campaignLocation.getCampaignEndDate().trim());
        values.put("camp_name", campaignLocation.getCampaignName().trim());
        values.put("camp_title", campaignLocation.getCampaignTitle().trim());
        values.put("camp_body", campaignLocation.getCampaignBody().trim());
        values.put("frequency", campaignLocation.getFrequency());
        values.put("frequency_limit", campaignLocation.getFrequencyLimit());
        values.put("time_difference", campaignLocation.getTimeDifference());

//        long res = db.insert("campaign_location", null, values);
        long res = db.insertWithOnConflict("campaign_location", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.e("insert row", res + "--");
    }

    public ArrayList<String> getLBCs() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String Date = sdf.format(new Date());
//        String query = "Select * from campaign_location WHERE frequency < frequency_limit AND camp_end_date >= \"" + Date + "\"";
        String query = "Select * from campaign_location WHERE camp_end_date >= \"" + Date + "\"";
        ArrayList<String> offersTable = SelectSQL(query);
        for (int i = 0; i < offersTable.size(); i++) {
            Log.e("offers table", offersTable.get(i));
        }

        return offersTable;
    }

    /**
     * make every select from database
     *
     * @param query
     * @return
     */
    public ArrayList<String> SelectSQL(String query) {
        ArrayList<String> tableData = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String row;
        if (cursor.moveToFirst()) {

            do {

                row = "";
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    row = row + cursor.getString(i) + ",";
                }
                tableData.add(row.substring(0, row.length() - 1));
//                Log.e("index", row);
            }
            while (cursor.moveToNext());
        }

        db.close();
        return tableData;
    }

    public boolean notificationDeliveryUpdate(int campID, String currentTime) {
        try {
            String query = "Update campaign_location set frequency = frequency+1 , delivered_time=\'" + currentTime + "\' WHERE camp_id=" + campID;
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
//        ContentValues values = new ContentValues();
//
//        values.put("frequency", br_CompId);
//
//        long res = db.update("campaign_location", values, "`branch_id`=" + br_Id, null);

    }

}