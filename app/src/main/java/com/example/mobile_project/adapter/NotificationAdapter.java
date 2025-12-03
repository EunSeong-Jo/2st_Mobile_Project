package com.example.mobile_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.model.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * 알림 목록 RecyclerView Adapter
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private OnItemClickListener onItemClickListener;

    public NotificationAdapter() {
        this.notificationList = new ArrayList<>();
    }

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList != null ? notificationList : new ArrayList<>();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    // 데이터 업데이트
    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList != null ? notificationList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addNotification(Notification notification) {
        notificationList.add(0, notification); // 최신 알림이 맨 위로
        notifyItemInserted(0);
    }

    public void removeNotification(int position) {
        if (position >= 0 && position < notificationList.size()) {
            notificationList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void markAsRead(int position) {
        if (position >= 0 && position < notificationList.size()) {
            notificationList.get(position).setRead(true);
            notifyItemChanged(position);
        }
    }

    public void markAllAsRead() {
        for (Notification notification : notificationList) {
            notification.setRead(true);
        }
        notifyDataSetChanged();
    }

    // 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(Notification notification, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // ViewHolder
    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private View viewUnreadIndicator;
        private TextView tvNotificationTitle;
        private TextView tvNotificationMessage;
        private TextView tvNotificationTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            viewUnreadIndicator = itemView.findViewById(R.id.view_unread_indicator);
            tvNotificationTitle = itemView.findViewById(R.id.tv_notification_title);
            tvNotificationMessage = itemView.findViewById(R.id.tv_notification_message);
            tvNotificationTime = itemView.findViewById(R.id.tv_notification_time);
        }

        public void bind(Notification notification) {
            // 읽음 표시
            if (notification.isRead()) {
                viewUnreadIndicator.setVisibility(View.INVISIBLE);
                itemView.setAlpha(0.6f);
            } else {
                viewUnreadIndicator.setVisibility(View.VISIBLE);
                itemView.setAlpha(1.0f);
            }

            // 제목
            tvNotificationTitle.setText(notification.getTitle());

            // 메시지
            tvNotificationMessage.setText(notification.getMessage());

            // 시간
            tvNotificationTime.setText(notification.getTimeAgo());

            // 클릭 이벤트
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(notification, getAdapterPosition());
                }
            });
        }
    }
}