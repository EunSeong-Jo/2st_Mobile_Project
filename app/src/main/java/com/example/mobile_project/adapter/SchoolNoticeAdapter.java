package com.example.mobile_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.model.SchoolNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * 학교 공지사항 어댑터
 */
public class SchoolNoticeAdapter extends RecyclerView.Adapter<SchoolNoticeAdapter.ViewHolder> {

    private Context context;
    private List<SchoolNotice> noticeList = new ArrayList<>();

    public SchoolNoticeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_school_notice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SchoolNotice notice = noticeList.get(position);

        holder.tvTitle.setText(notice.getTitle());
        holder.tvDate.setText(notice.getDate());

        // NEW 표시
        holder.tvNew.setVisibility(notice.isNew() ? View.VISIBLE : View.GONE);

        // 작성자 표시 (있을 경우)
        if (notice.getAuthor() != null && !notice.getAuthor().isEmpty()) {
            holder.tvAuthor.setVisibility(View.VISIBLE);
            holder.tvAuthor.setText(notice.getAuthor());
        } else {
            holder.tvAuthor.setVisibility(View.GONE);
        }

        // 클릭 시 웹 브라우저로 열기
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(notice.getUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public void setNoticeList(List<SchoolNotice> notices) {
        this.noticeList = notices;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvAuthor, tvNew;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notice_title);
            tvDate = itemView.findViewById(R.id.tv_notice_date);
            tvAuthor = itemView.findViewById(R.id.tv_notice_author);
            tvNew = itemView.findViewById(R.id.tv_notice_new);
        }
    }
}