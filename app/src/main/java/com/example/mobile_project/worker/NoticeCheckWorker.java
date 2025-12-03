package com.example.mobile_project.worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.mobile_project.model.SchoolNotice;
import com.example.mobile_project.util.NotificationHelper;
import com.example.mobile_project.util.SchoolNoticeService;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 백그라운드에서 공지사항을 체크하는 Worker
 */
public class NoticeCheckWorker extends Worker {

    private static final String TAG = "NoticeCheckWorker";
    private static final String PREFS_NAME = "NoticePrefs";
    private static final String KEY_LAST_NOTICE_URL = "last_notice_url";

    public NoticeCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "NoticeCheckWorker started");

        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // 마지막 확인한 공지사항 URL
        String lastNoticeUrl = prefs.getString(KEY_LAST_NOTICE_URL, null);

        // 공지사항 가져오기 (동기 처리를 위해 CountDownLatch 사용)
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] hasNewNotice = {false};
        final String[] latestNoticeUrl = {null};
        final String[] latestNoticeTitle = {null};
        final int[] newNoticeCount = {0};

        SchoolNoticeService service = new SchoolNoticeService();
        service.fetchNotices(new SchoolNoticeService.NoticeCallback() {
            @Override
            public void onSuccess(List<SchoolNotice> notices) {
                if (notices != null && !notices.isEmpty()) {
                    // 가장 최신 공지사항
                    SchoolNotice latestNotice = notices.get(0);
                    latestNoticeUrl[0] = latestNotice.getUrl();
                    latestNoticeTitle[0] = latestNotice.getTitle();

                    // 마지막 확인 URL과 비교
                    if (lastNoticeUrl == null || !lastNoticeUrl.equals(latestNoticeUrl[0])) {
                        hasNewNotice[0] = true;

                        // 새로운 공지 개수 계산 (URL이 다른 것들)
                        if (lastNoticeUrl != null) {
                            for (SchoolNotice notice : notices) {
                                if (notice.getUrl().equals(lastNoticeUrl)) {
                                    break;
                                }
                                newNoticeCount[0]++;
                            }
                        } else {
                            newNoticeCount[0] = 1; // 처음 실행
                        }

                        Log.d(TAG, "New notice detected: " + latestNoticeTitle[0]);
                        Log.d(TAG, "New notice count: " + newNoticeCount[0]);
                    } else {
                        Log.d(TAG, "No new notices");
                    }
                }
                latch.countDown();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error fetching notices: " + errorMessage);
                latch.countDown();
            }
        });

        // 최대 60초 대기
        try {
            latch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "Worker interrupted", e);
            return Result.failure();
        }

        // 새 공지사항이 있으면 알림 표시
        if (hasNewNotice[0] && latestNoticeUrl[0] != null) {
            // 마지막 공지사항 URL 저장
            prefs.edit().putString(KEY_LAST_NOTICE_URL, latestNoticeUrl[0]).apply();

            // 알림 표시
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.showNewNoticeNotification(
                    latestNoticeTitle[0],
                    newNoticeCount[0]
            );

            Log.d(TAG, "Notification sent");
        }

        service.shutdown();
        return Result.success();
    }
}