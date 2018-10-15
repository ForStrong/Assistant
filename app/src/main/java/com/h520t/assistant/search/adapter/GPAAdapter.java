package com.h520t.assistant.search.adapter;

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
import com.h520t.assistant.search.bean.GPABean;

import java.util.ArrayList;


public class GPAAdapter extends RecyclerView.Adapter<GPAAdapter.ViewHolder> {
    private ArrayList<GPABean> mGPABeans;
    private Context mContext;

    public GPAAdapter(ArrayList<GPABean> GPABeans) {
        mGPABeans = GPABeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gpa_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GPABean gpaBean = mGPABeans.get(position);
        holder.className.setText(gpaBean.getClassName());
        holder.classNature.setText(gpaBean.getClassNature());
        holder.credit.setText(gpaBean.getCredit());
        holder.gpa.setText(gpaBean.getGpa());
        if (TextUtils.isDigitsOnly(gpaBean.getGrade())&&Integer.parseInt(gpaBean.getGrade())<60){
            int color = mContext.getResources().getColor(R.color.failScore);
            holder.grade.setTextColor(color);
            holder.makeupGrade.setTextColor(color);
            holder.retakenGrade.setTextColor(color);
        }else if (gpaBean.getGrade().equals("不及格")){
            int color = mContext.getResources().getColor(R.color.failScore);
            holder.grade.setTextColor(color);
            holder.makeupGrade.setTextColor(color);
            holder.retakenGrade.setTextColor(color);
        }else {
            int color = mContext.getResources().getColor(R.color.passScore);
            holder.grade.setTextColor(color);
            holder.makeupGrade.setTextColor(color);
            holder.retakenGrade.setTextColor(color);
        }
        holder.grade.setText(gpaBean.getGrade());

        //================================================判断是否有补考重修成绩==============================//
        if (TextUtils.isEmpty(gpaBean.getMakeupGrade())){
            holder.makeupGradeLayout.setVisibility(View.GONE);
        }else{
            holder.makeupGradeLayout.setVisibility(View.VISIBLE);
            holder.makeupGrade.setText(gpaBean.getMakeupGrade());
        }
        if (TextUtils.isEmpty(gpaBean.getRetakenGrade())){
            holder.retakenGradeLayout.setVisibility(View.GONE);
        }else{
            holder.retakenGradeLayout.setVisibility(View.VISIBLE);
            holder.retakenGrade.setText(gpaBean.getRetakenGrade());
        }
    }

    @Override
    public int getItemCount() {
        return mGPABeans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView className,classNature,credit,grade,gpa,makeupGrade,retakenGrade;
        LinearLayout makeupGradeLayout,retakenGradeLayout;
         ViewHolder(View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.gpa_className);
            classNature = itemView.findViewById(R.id.gpa_classNature);
            credit = itemView.findViewById(R.id.gpa_credit);
            grade = itemView.findViewById(R.id.gpa_Grade);
            gpa = itemView.findViewById(R.id.gpa_gpa);
            makeupGrade = itemView.findViewById(R.id.gpa_makeupGrade);
            retakenGrade = itemView.findViewById(R.id.gpa_retakenGrade);

            makeupGradeLayout = itemView.findViewById(R.id.gpa_makeupGradeLayout);
            retakenGradeLayout = itemView.findViewById(R.id.gpa_retakenGradeLayout);
        }
    }
}
