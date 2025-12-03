package com.example.mobile_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.model.JobPosting;

import java.util.ArrayList;
import java.util.List;

/**
 * 채용공고 목록 RecyclerView Adapter
 */
public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.JobViewHolder> {

    private List<JobPosting> jobList;
    private OnItemClickListener onItemClickListener;
    private OnBookmarkClickListener onBookmarkClickListener;

    public JobListAdapter() {
        this.jobList = new ArrayList<>();
    }

    public JobListAdapter(List<JobPosting> jobList) {
        this.jobList = jobList != null ? jobList : new ArrayList<>();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_card, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobPosting job = jobList.get(position);
        holder.bind(job);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    // 데이터 업데이트
    public void setJobList(List<JobPosting> jobList) {
        this.jobList = jobList != null ? jobList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addJob(JobPosting job) {
        jobList.add(job);
        notifyItemInserted(jobList.size() - 1);
    }

    public void removeJob(int position) {
        if (position >= 0 && position < jobList.size()) {
            jobList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateJob(int position, JobPosting job) {
        if (position >= 0 && position < jobList.size()) {
            jobList.set(position, job);
            notifyItemChanged(position);
        }
    }

    /**
     * 전체 목록 업데이트
     */
    public void setJobPostingList(List<JobPosting> newList) {
        this.jobList = newList;
        notifyDataSetChanged();
    }

    // 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(JobPosting job, int position);
    }

    public interface OnBookmarkClickListener {
        void onBookmarkClick(JobPosting job, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnBookmarkClickListener(OnBookmarkClickListener listener) {
        this.onBookmarkClickListener = listener;
    }

    // ViewHolder
    class JobViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCompanyName;
        private TextView tvRating;
        private ImageView ivBookmark;
        private TextView tvJobTitle;
        private TextView tvSalary;
        private TextView tvLocation;
        private TextView tvWorkTime;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvRating = itemView.findViewById(R.id.tv_rating);
            ivBookmark = itemView.findViewById(R.id.iv_bookmark);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvSalary = itemView.findViewById(R.id.tv_salary);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvWorkTime = itemView.findViewById(R.id.tv_work_time);
        }

        public void bind(JobPosting job) {
            // 회사명
            tvCompanyName.setText(job.getCompanyName());

            // 평점
            if (job.getRating() > 0) {
                tvRating.setText("⭐" + String.format("%.1f", job.getRating()));
                tvRating.setVisibility(View.VISIBLE);
            } else {
                tvRating.setVisibility(View.GONE);
            }

            // 북마크 아이콘
            if (job.isBookmarked()) {
                ivBookmark.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                ivBookmark.setImageResource(android.R.drawable.btn_star_big_off);
            }

            // 공고 제목
            tvJobTitle.setText(job.getTitle());

            // 급여
            tvSalary.setText("💰 " + job.getSalaryDisplay());

            // 위치
            tvLocation.setText("📍 " + job.getLocation());

            // 근무 시간
            tvWorkTime.setText("⏰ " + job.getWorkTime());

            // 카드 클릭 이벤트
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(job, getAdapterPosition());
                }
            });

            // 북마크 클릭 이벤트
            ivBookmark.setOnClickListener(v -> {
                if (onBookmarkClickListener != null) {
                    onBookmarkClickListener.onBookmarkClick(job, getAdapterPosition());
                }
            });
        }
    }
}