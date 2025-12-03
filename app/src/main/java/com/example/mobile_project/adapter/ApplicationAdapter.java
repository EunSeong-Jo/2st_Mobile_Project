package com.example.mobile_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.model.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * 지원 내역 RecyclerView Adapter
 */
public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private List<Application> applicationList;
    private OnItemClickListener onItemClickListener;

    public ApplicationAdapter() {
        this.applicationList = new ArrayList<>();
    }

    public ApplicationAdapter(List<Application> applicationList) {
        this.applicationList = applicationList != null ? applicationList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application_card, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = applicationList.get(position);
        holder.bind(application);
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    // 데이터 업데이트
    public void setApplicationList(List<Application> applicationList) {
        this.applicationList = applicationList != null ? applicationList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(Application application, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // ViewHolder
    class ApplicationViewHolder extends RecyclerView.ViewHolder {
        private View viewStatusIndicator;
        private TextView tvJobTitle;
        private TextView tvCompanyName;
        private TextView tvAppliedDate;
        private TextView tvStatus;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);

            viewStatusIndicator = itemView.findViewById(R.id.view_status_indicator);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvAppliedDate = itemView.findViewById(R.id.tv_applied_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void bind(Application application) {
            // TODO: JobPosting 정보 가져오기 (현재는 application에 없음)
            // 실제로는 ApplicationDAO에서 JOIN하여 JobPosting 정보도 함께 가져와야 함
            tvJobTitle.setText("채용공고 #" + application.getJobPostingId());
            tvCompanyName.setText("회사명");

            // 지원 날짜
            tvAppliedDate.setText(application.getAppliedAt());

            // 상태 표시
            tvStatus.setText(application.getStatusDisplay());

            // 상태별 색상
            int statusColor;
            if (application.isPending()) {
                statusColor = 0xFF1E88E5; // DMU Blue
            } else if (application.isAccepted()) {
                statusColor = 0xFF4CAF50; // Green
            } else {
                statusColor = 0xFFE53935; // Red
            }
            viewStatusIndicator.setBackgroundColor(statusColor);
            tvStatus.setTextColor(statusColor);

            // 카드 클릭 이벤트
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(application, getAdapterPosition());
                }
            });
        }
    }
}