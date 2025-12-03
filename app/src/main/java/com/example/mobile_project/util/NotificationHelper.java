package com.example.mobile_project.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.mobile_project.MainActivity;
import com.example.mobile_project.R;

/**
 * 알림 관리 헬퍼 클래스
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "school_notice_channel";
    private static final String CHANNEL_NAME = "학교 공지사항";
    private static final String CHANNEL_DESCRIPTION = "새로운 학교 공지사항 알림";
    private static final int NOTIFICATION_ID = 1001;

    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    /**
     * 알림 채널 생성 (Android 8.0 이상)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 새로운 공지사항 알림 표시
     *
     * @param title 공지사항 제목
     * @param count 새 공지사항 개수
     */
    public void showNewNoticeNotification(String title, int count) {
        // MainActivity로 이동하는 Intent
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("open_notifications", true); // 알림 탭으로 이동

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 알림 내용
        String contentText = count == 1
                ? title
                : String.format("새로운 공지사항 %d개", count);

        // 알림 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications) // 알림 아이콘
                .setContentTitle("새로운 학교 공지사항")
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // 클릭 시 자동으로 알림 제거
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 200, 500}); // 진동 패턴

        // 알림 표시
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * 알림 취소
     */
    public void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
