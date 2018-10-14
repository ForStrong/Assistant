package com.h520t.assistant.search.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.h520t.assistant.R;

import java.util.ArrayList;


public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private ArrayList<ScoreBean> mScoreBeans;
    private Context mContext;
    public ScoreAdapter(ArrayList<ScoreBean> scoreBeans) {
        mScoreBeans = scoreBeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_score_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScoreBean scoreBean = mScoreBeans.get(position);
        holder.className.setText(scoreBean.getClassName());
        holder.classNature.setText(scoreBean.getClassNature());
        holder.credit.setText(scoreBean.getCredit());
        if (TextUtils.isDigitsOnly(scoreBean.getGrade())&&Integer.parseInt(scoreBean.getGrade())<60){
            int color = mContext.getResources().getColor(R.color.failScore);
            holder.grade.setTextColor(color);
        }else if (scoreBean.getGrade().equals("不及格")){
            int color = mContext.getResources().getColor(R.color.failScore);
            holder.grade.setTextColor(color);
        }else {
            int color = mContext.getResources().getColor(R.color.passScore);
            holder.grade.setTextColor(color);
        }
        holder.grade.setText(scoreBean.getGrade());
        holder.finalGrade.setText(scoreBean.getFinalGrade());

//-----------------------------------------------判断是否有期中成绩,实验成绩,补考成绩----------------------------------//
        if (TextUtils.isEmpty(scoreBean.getMidTermGrade())){
            holder.midTermGradeLayout.setVisibility(View.GONE);
        }else{
            holder.midTermGradeLayout.setVisibility(View.VISIBLE);
            holder.midTermGrade.setText(scoreBean.getMidTermGrade());
        }

        if (TextUtils.isEmpty(scoreBean.getExperimentalGrade())){
            holder.experimentalGradeLayout.setVisibility(View.GONE);
        }else {
            holder.experimentalGradeLayout.setVisibility(View.VISIBLE);
            holder.experimentalGrade.setText(scoreBean.getExperimentalGrade());
        }

        if (TextUtils.isEmpty(scoreBean.getMakeupGrade())){
            holder.makeupGradeLayout.setVisibility(View.GONE);
        }else {
            holder.makeupGradeLayout.setVisibility(View.VISIBLE);
            if (TextUtils.isDigitsOnly(scoreBean.getMakeupGrade())&&Integer.parseInt(scoreBean.getMakeupGrade())<60){
                int color = mContext.getResources().getColor(R.color.failScore);
                holder.makeupGrade.setTextColor(color);
            }else if (scoreBean.getMakeupGrade().equals("不及格")){
                int color = mContext.getResources().getColor(R.color.failScore);
                holder.makeupGrade.setTextColor(color);
            }else{
                int color = mContext.getResources().getColor(R.color.passScore);
                holder.makeupGrade.setTextColor(color);
            }
            holder.makeupGrade.setText(scoreBean.getMakeupGrade());
        }
    }

    @Override
    public int getItemCount() {
        return mScoreBeans==null?0:mScoreBeans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView className,classNature,credit,midTermGrade,finalGrade,experimentalGrade,grade,makeupGrade;
        LinearLayout midTermGradeLayout,experimentalGradeLayout,makeupGradeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.className);
            classNature = itemView.findViewById(R.id.classNature);
            credit = itemView.findViewById(R.id.credit);
            midTermGrade = itemView.findViewById(R.id.midTermGrade);
            finalGrade = itemView.findViewById(R.id.finalGrade);
            experimentalGrade = itemView.findViewById(R.id.experimentalGrade);
            grade = itemView.findViewById(R.id.grade);
            makeupGrade = itemView.findViewById(R.id.makeupGrade);
            midTermGradeLayout = itemView.findViewById(R.id.midTermGradeLayout);
            experimentalGradeLayout = itemView.findViewById(R.id.experimentalGradeLayout);
            makeupGradeLayout = itemView.findViewById(R.id.makeupGradeLayout);
        }
    }
}
