package com.systemonline.fanscoupon.Model;

import java.util.ArrayList;

/**
 * Created by SystemOnline1 on 7/3/2017.
 */

public class LBC {
    private int campaignsUpdateInterval;
    private ArrayList<CampaignLocation> campaignLocations;

    public int getCampaignsUpdateInterval() {
        return campaignsUpdateInterval;
    }

    public void setCampaignsUpdateInterval(int campaignsUpdateInterval) {
        this.campaignsUpdateInterval = campaignsUpdateInterval;
    }

    public ArrayList<CampaignLocation> getCampaignLocations() {
        return campaignLocations;
    }

    public void setCampaignLocations(ArrayList<CampaignLocation> campaignLocations) {
        this.campaignLocations = campaignLocations;
    }
}
