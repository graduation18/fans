package com.systemonline.fanscoupon.Model;


public class FaceBookPage {
    private String pageName, pagePath, pageFbId;
    private int pageId;

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public String getPageFbId() {
        return pageFbId;
    }

    public void setPageFbId(String pageFbId) {
        this.pageFbId = pageFbId;
    }
}
