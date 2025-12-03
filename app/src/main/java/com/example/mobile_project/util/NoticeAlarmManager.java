package com.example.mobile_project.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.mobile_project.worker.NoticeCheckWorker;

import java.util.Calendar;

/**
 * 공지사항 체크 알람 관리
 * 매일 9시, 12시, 3시(15시), 6시(18시)에 실행
 */
public class NoticeAlarmManager {

    private static final String TAG = "NoticeAlarmManager";
    private static final int[] CHECK_HOURS = {9, 12, 15, 18}; // 9시, 12시, 3시, 6시
    private static final int[] REQUEST_CODES = {1001, 1002, 1003, 1004};

    /**
     * 알람 설정
     */
    public static void scheduleNoticeCheck(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < CHECK_HOURS.length; i++) {
            Intent intent = new Intent(context, NoticeCheckReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    REQUEST_CODES[i],
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // 오늘 해당 시각 계산
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, CHECK_HOURS[i]);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // 이미 지난 시간이면 내일로 설정
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            // 정확한 알람 설정
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12 이상: 정확한 알람 권한 확인
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                    Log.d(TAG, "Alarm scheduled for " + CHECK_HOURS[i] + ":00");
                } else {
                    Log.w(TAG, "Cannot schedule exact alarm - permission denied");
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
                Log.d(TAG, "Alarm scheduled for " + CHECK_HOURS[i] + ":00");
            }
        }
    }

    /**
     * 알람 취소
     */
    public static void cancelNoticeCheck(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < REQUEST_CODES.length; i++) {
            Intent intent = new Intent(context, NoticeCheckReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    REQUEST_CODES[i],
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pendingIntent);
        }

        Log.d(TAG, "All alarms cancelled");
    }

    /**
     * BroadcastReceiver - 알람이 울릴 때 실행
     */
    public static class NoticeCheckReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Alarm received - starting NoticeCheckWorker");

            // Worker 실행
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NoticeCheckWorker.class)
                    .build();

            WorkManager.getInstance(context).enqueue(workRequest);

            // 다음 날 같은 시간에 다시 알람 설정
            scheduleNoticeCheck(context);
        }
    }
}
