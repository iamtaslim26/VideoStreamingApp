package com.kgec.videostreamingapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.Label;
import com.kgec.videostreamingapp.R;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private Context mContext;
    private List<Comments>mComments;

    public CommentsAdapter(Context mContext,List<Comments>mComments){

        this.mContext=mContext;
        this.mComments=mComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout,parent,false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Comments comments=mComments.get(position);
        holder.comment_display.setText(comments.getComment());
        holder.comment_date.setText(comments.getDate());
        holder.comment_time.setText(comments.getTime());
        holder.comment_username.setText(comments.getFullname());

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView comment_username,comment_date,comment_time,comment_display;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            comment_username=itemView.findViewById(R.id.username_comment);
            comment_date=itemView.findViewById(R.id.comment_Date);
            comment_time=itemView.findViewById(R.id.comment_Time);
            comment_display=itemView.findViewById(R.id.comment_text_display);
        }
    }

}
