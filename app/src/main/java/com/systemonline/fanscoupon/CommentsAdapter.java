package com.systemonline.fanscoupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Comment;

import java.util.ArrayList;

public class CommentsAdapter extends BaseAdapter {
    public ArrayList<Comment> values;
    Context con;
    Utility utility;
    private Comment comment;

    public CommentsAdapter(Context c, ArrayList<Comment> values) {
        super(c, R.layout.loyalty_single_coupon_fragment, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        comment = values.get(position);

        row = inflater.inflate(R.layout.comments_row, parent, false);

        TextView tv_commenter = (TextView) row.findViewById(R.id.tv_commenter);
        TextView tv_comment = (TextView) row.findViewById(R.id.tv_comment);
        ImageView img_commenter = (ImageView) row.findViewById(R.id.img_commenter);

        tv_comment.setText(comment.getCommentBody());
        utility.setTextViewLines(tv_comment, comment.getCommentBody());
        tv_commenter.setText(comment.getCommentWriter().getFanFirstName() + " " + comment.getCommentWriter().getFanLastName());
        utility.setTextViewLines(tv_commenter, comment.getCommentWriter().getFanFirstName() + " " + comment.getCommentWriter().getFanLastName());
        Picasso.with(getContext()).load(Const.imagesURL + "users/40x40/" + comment.getCommentWriter().getFanImage()).placeholder(R.drawable.ph_user).into(img_commenter);

        LinearLayout rate = (LinearLayout) row.findViewById(R.id.commentRate);
        utility.createDynamicStars(comment.getRate(), rate);

        return row;
    }


}
