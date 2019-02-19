package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 10/18/2016.
 */

public class CollectAndWin extends ChallengeBaseClass {
    private int punchCardID, totalQRCodes, scannedQRCodes;
    private String codeName;

    public int getPunchCardID() {
        return punchCardID;
    }

    public void setPunchCardID(int punchCardID) {
        this.punchCardID = punchCardID;
    }

    public int getTotalQRCodes() {
        return totalQRCodes;
    }

    public void setTotalQRCodes(int totalQRCodes) {
        this.totalQRCodes = totalQRCodes;
    }

    public int getScannedQRCodes() {
        return scannedQRCodes;
    }

    public void setScannedQRCodes(int scannedQRCodes) {
        this.scannedQRCodes = scannedQRCodes;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
}
