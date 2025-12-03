package com.example.mobile_project.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 관리 클래스
 * 자동 로그인, 사용자 정보 저장
 */
public class PreferenceManager {

    private static final String PREF_NAME = "AlbaDaePreferences";

    // Keys
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_TYPE = "user_type"; // "student" or "employer"
    private static final String KEY_AUTO_LOGIN = "auto_login";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * 로그인 정보 저장
     */
    public void saveLoginInfo(int userId, String email, String name, String userType, boolean autoLogin) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_TYPE, userType);
        editor.putBoolean(KEY_AUTO_LOGIN, autoLogin);
        editor.apply();
    }

    /**
     * 자동 로그인 설정
     */
    public void setAutoLogin(boolean autoLogin) {
        editor.putBoolean(KEY_AUTO_LOGIN, autoLogin);
        editor.apply();
    }

    /**
     * 자동 로그인 여부 확인
     */
    public boolean isAutoLoginEnabled() {
        return preferences.getBoolean(KEY_AUTO_LOGIN, false);
    }

    /**
     * 로그인 상태 확인
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * 사용자 ID 가져오기
     */
    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * 사용자 이메일 가져오기
     */
    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }

    /**
     * 사용자 이름 가져오기
     */
    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }

    /**
     * 사용자 타입 가져오기
     */
    public String getUserType() {
        return preferences.getString(KEY_USER_TYPE, "");
    }

    /**
     * 로그아웃 (모든 정보 삭제)
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }

    /**
     * 로그인 상태만 해제 (자동 로그인 비활성화 시)
     */
    public void clearLoginSession() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }
}