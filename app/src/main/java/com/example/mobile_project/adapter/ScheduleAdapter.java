package com.example.mobile_project.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.model.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * 시간표 RecyclerView Adapter
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> scheduleList;
    private OnItemClickListener onItemClickListener;
    private OnMenuClickListener onMenuClickListener;

    public ScheduleAdapter() {
        this.scheduleList = new ArrayList<>();
    }

    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList != null ? scheduleList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_card, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    // 데이터 업데이트
    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList != null ? scheduleList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addSchedule(Schedule schedule) {
        scheduleList.add(schedule);
        notifyItemInserted(scheduleList.size() - 1);
    }

    public void removeSchedule(int position) {
        if (position >= 0 && position < scheduleList.size()) {
            scheduleList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateSchedule(int position, Schedule schedule) {
        if (position >= 0 && position < scheduleList.size()) {
            scheduleList.set(position, schedule);
            notifyItemChanged(position);
        }
    }

    /**
     * 일정 겹침 체크 및 표시
     */
    public void checkConflicts() {
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule schedule = scheduleList.get(i);
            boolean hasConflict = false;

            for (int j = 0; j < scheduleList.size(); j++) {
                if (i != j) {
                    Schedule other = scheduleList.get(j);
                    if (schedule.isConflictWith(other)) {
                        hasConflict = true;
                        break;
                    }
                }
            }

            // 겹침 상태를 메모 필드에 임시 저장 (또는 별도 필드 추가 가능)
            // 실제로는 Schedule 모델에 hasConflict 필드를 추가하는 것이 좋습니다
        }
        notifyDataSetChanged();
    }

    // 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(Schedule schedule, int position);
    }

    public interface OnMenuClickListener {
        void onMenuClick(Schedule schedule, int position, View anchorView);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        this.onMenuClickListener = listener;
    }

    // ViewHolder
    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private View viewColorIndicator;
        private TextView tvDayOfWeek;
        private TextView tvTimeRange;
        private TextView tvScheduleTitle;
        private TextView tvLocation;
        private ImageView ivMenu;
        private TextView tvConflictWarning;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            viewColorIndicator = itemView.findViewById(R.id.view_color_indicator);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvTimeRange = itemView.findViewById(R.id.tv_time_range);
            tvScheduleTitle = itemView.findViewById(R.id.tv_schedule_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
            ivMenu = itemView.findViewById(R.id.iv_menu);
            tvConflictWarning = itemView.findViewById(R.id.tv_conflict_warning);
        }

        public void bind(Schedule schedule) {
            // 색상 표시 (수업: 파란색, 알바: 오렌지색)
            int color = schedule.getTypeColor();
            viewColorIndicator.setBackgroundColor(color);

            // 요일
            tvDayOfWeek.setText(schedule.getDayOfWeekDisplay());

            // 시간대
            tvTimeRange.setText(schedule.getTimeDisplay());

            // 일정 제목
            tvScheduleTitle.setText(schedule.getTitle());

            // 장소
            if (schedule.getLocation() != null && !schedule.getLocation().isEmpty()) {
                tvLocation.setText("📍 " + schedule.getLocation());
                tvLocation.setVisibility(View.VISIBLE);
            } else {
                tvLocation.setVisibility(View.GONE);
            }

            // 겹침 체크 (현재 간단히 구현, 실제로는 checkConflicts() 결과 사용)
            boolean hasConflict = checkConflictForSchedule(schedule);
            if (hasConflict) {
                tvConflictWarning.setVisibility(View.VISIBLE);
            } else {
                tvConflictWarning.setVisibility(View.GONE);
            }

            // 카드 클릭 이벤트
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(schedule, getAdapterPosition());
                }
            });

            // 메뉴 클릭 이벤트
            ivMenu.setOnClickListener(v -> {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onMenuClick(schedule, getAdapterPosition(), v);
                }
            });
        }

        /**
         * 해당 일정의 겹침 여부 확인
         */
        private boolean checkConflictForSchedule(Schedule schedule) {
            for (Schedule other : scheduleList) {
                if (schedule.getId() != other.getId() && schedule.isConflictWith(other)) {
                    return true;
                }
            }
            return false;
        }
    }
}