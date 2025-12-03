package com.example.mobile_project.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.database.DatabaseHelper;
import com.example.mobile_project.util.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 로그인 화면
 * 이메일/비밀번호 로그인, 자동 로그인, 회원가입 연결
 */
public class LoginActivity extends AppCompatActivity {

    // 테스트용 계정 (임시)
    private static final String TEST_EMAIL = "dmu@dongyang.ac.kr";
    private static final String TEST_PASSWORD = "12345678";

    private TextInputEditText etEmail, etPassword;
    private CheckBox cbAutoLogin;
    private MaterialButton btnLogin;
    private TextView tvSignup, tvFindId, tvFindPassword;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);

        // 자동 로그인 체크
        if (preferenceManager.isAutoLoginEnabled() && preferenceManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
        loadSavedEmail();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cbAutoLogin = findViewById(R.id.cb_auto_login);
        btnLogin = findViewById(R.id.btn_login);
        tvSignup = findViewById(R.id.tv_signup);
        tvFindId = findViewById(R.id.tv_find_id);
        tvFindPassword = findViewById(R.id.tv_find_password);
    }

    private void setupClickListeners() {
        // 로그인 버튼
        btnLogin.setOnClickListener(v -> handleLogin());

        // 회원가입 링크
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, UserTypeSelectionActivity.class);
            startActivity(intent);
        });

        // 아이디 찾기
        tvFindId.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, FindIdActivity.class);
            startActivity(intent);
        });

        // 비밀번호 찾기
        tvFindPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        boolean autoLogin = cbAutoLogin.isChecked();

        // 입력 검증
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("이메일을 입력해주세요");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("비밀번호를 입력해주세요");
            etPassword.requestFocus();
            return;
        }

        // 데이터베이스에서 계정 확인
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                new String[]{
                        DatabaseHelper.COLUMN_ID,
                        DatabaseHelper.COLUMN_EMAIL,
                        DatabaseHelper.COLUMN_NAME,
                        DatabaseHelper.COLUMN_USER_TYPE
                },
                DatabaseHelper.COLUMN_EMAIL + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?",
                new String[]{email, password},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            // 로그인 성공
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            String userType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_TYPE));

            cursor.close();

            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();

            // 로그인 정보 저장
            preferenceManager.saveLoginInfo(userId, email, userName, userType, autoLogin);

            navigateToMain();
        } else {
            // 로그인 실패
            if (cursor != null) {
                cursor.close();
            }

            Toast.makeText(this, "이메일 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            etPassword.requestFocus();
        }
    }

    /**
     * 저장된 이메일 불러오기
     */
    private void loadSavedEmail() {
        if (preferenceManager.isLoggedIn()) {
            String savedEmail = preferenceManager.getUserEmail();
            if (!TextUtils.isEmpty(savedEmail)) {
                etEmail.setText(savedEmail);
            }
        }
    }

    /**
     * MainActivity로 이동
     */
    private void navigateToMain() {
        String userType = preferenceManager.getUserType();
        Intent intent = new Intent(LoginActivity.this, com.example.mobile_project.MainActivity.class);
        intent.putExtra("user_type", userType);
        intent.putExtra("user_id", preferenceManager.getUserId());
        intent.putExtra("user_name", preferenceManager.getUserName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
