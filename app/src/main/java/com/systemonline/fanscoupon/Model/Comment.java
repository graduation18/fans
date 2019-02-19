package com.systemonline.fanscoupon.Model;


public class Comment {
    private Fan commentWriter;
    private String commentBody;
    private int commentID, rate;

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public Fan getCommentWriter() {
        return commentWriter;
    }

    public void setCommentWriter(Fan commentWriter) {
        this.commentWriter = commentWriter;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
