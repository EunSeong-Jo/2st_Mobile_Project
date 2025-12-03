package com.example.mobile_project.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.database.DatabaseHelper;

/**
 * 스플래시 화면
 * 앱 시작 시 2-3초 동안 로고와 브랜딩을 표시
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500; // 2.5초
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_DUMMY_DATA_LOADED = "dummy_data_loaded";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 더미 데이터 로드 (앱 첫 실행 시에만)
        loadDummyDataIfNeeded();

        // 2.5초 후 로그인 화면으로 이동
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // TODO: 로그인 상태 확인 후 메인 또는 로그인 화면으로 이동
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }

    /**
     * 앱 첫 실행 시 더미 데이터 로드
     */
    private void loadDummyDataIfNeeded() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean dummyDataLoaded = prefs.getBoolean(KEY_DUMMY_DATA_LOADED, false);

        // TODO: 개발 중에는 매번 로드 (주석 처리하여 테스트 후 원래대로)
        // 배포 시에는 if (!dummyDataLoaded) 조건 다시 활성화하기
        if (true) {  // 임시: 항상 더미 데이터 로드
            // 더미 데이터 추가
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
            dbHelper.insertDummyData();

            // 더미 데이터 로드 완료 플래그 저장
            prefs.edit().putBoolean(KEY_DUMMY_DATA_LOADED, true).apply();
        }
    }
}
