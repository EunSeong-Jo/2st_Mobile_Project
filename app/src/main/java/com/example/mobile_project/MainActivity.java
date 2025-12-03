package com.example.mobile_project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.mobile_project.fragment.HomeFragment;
import com.example.mobile_project.fragment.JobsFragment;
import com.example.mobile_project.fragment.MyPageFragment;
import com.example.mobile_project.fragment.NotificationsFragment;
import com.example.mobile_project.fragment.ScheduleFragment;
import com.example.mobile_project.util.NoticeAlarmManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 메인 화면
 * Bottom Navigation으로 Fragment 전환
 */
public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    private BottomNavigationView bottomNavigationView;
    private String userType = "student"; // 기본값: 재학생

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intent에서 사용자 유형 가져오기
        if (getIntent().hasExtra("user_type")) {
            userType = getIntent().getStringExtra("user_type");
        }

        initViews();
        setupBottomNavigation();

        // 초기 Fragment 설정 (홈)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // 알림 권한 요청 및 알람 설정
        requestNotificationPermissionAndSetupAlarm();
    }

    /**
     * 알림 권한 요청 및 알람 설정
     */
    private void requestNotificationPermissionAndSetupAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상: 알림 권한 필요
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            } else {
                // 이미 권한이 있으면 바로 알람 설정
                setupNoticeAlarm();
            }
        } else {
            // Android 12 이하: 알림 권한 자동 허용
            setupNoticeAlarm();
        }
    }

    /**
     * 공지사항 알람 설정
     */
    private void setupNoticeAlarm() {
        NoticeAlarmManager.scheduleNoticeCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용됨 - 알람 설정
                setupNoticeAlarm();
            }
            // 권한 거부되어도 앱은 정상 동작 (알림만 안 옴)
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Edge-to-edge 설정: 시스템 제스처 바와 겹치지 않도록 패딩 추가
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    insets.bottom
            );
            return WindowInsetsCompat.CONSUMED;
        });

        // 고용주인 경우 시간표 메뉴 숨기기
        if ("employer".equals(userType)) {
            bottomNavigationView.getMenu().removeItem(R.id.nav_schedule);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_jobs) {
                selectedFragment = new JobsFragment();
            } else if (itemId == R.id.nav_schedule) {
                selectedFragment = new ScheduleFragment();
            } else if (itemId == R.id.nav_notifications) {
                selectedFragment = new NotificationsFragment();
            } else if (itemId == R.id.nav_my_page) {
                selectedFragment = new MyPageFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}