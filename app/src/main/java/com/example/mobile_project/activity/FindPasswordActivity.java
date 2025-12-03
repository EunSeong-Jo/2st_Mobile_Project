package com.example.mobile_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.database.UserDAO;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 비밀번호 찾기 화면
 * 이메일, 이름, 전화번호 확인 후 비밀번호 재설정
 */
public class FindPasswordActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etEmail, etName, etPhone;
    private TextInputEditText etNewPassword, etConfirmPassword;
    private MaterialButton btnFindPassword, btnResetPassword;
    private MaterialCardView cardResult;

    private UserDAO userDAO;
    private int verifiedUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        userDAO = new UserDAO(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etEmail = findViewById(R.id.et_email);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnFindPassword = findViewById(R.id.btn_find_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        cardResult = findViewById(R.id.card_result);
    }

    private void setupClickListeners() {
        // 뒤로가기
        toolbar.setNavigationOnClickListener(v -> finish());

        // 비밀번호 찾기 (사용자 확인)
        btnFindPassword.setOnClickListener(v -> handleVerifyUser());

        // 비밀번호 재설정
        btnResetPassword.setOnClickListener(v -> handleResetPassword());
    }

    private void handleVerifyUser() {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // 입력 검증
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("이메일을 입력해주세요");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            etName.setError("이름을 입력해주세요");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("전화번호를 입력해주세요");
            etPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            etPhone.setError("올바른 전화번호를 입력해주세요");
            etPhone.requestFocus();
            return;
        }

        // TODO: 실제 데이터베이스에서 이메일, 이름, 전화번호로 사용자 확인
        // 현재는 테스트 계정 확인
        if ("dmu@dongyang.ac.kr".equals(email) && "테스트 사용자".equals(name) && "01012345678".equals(phone)) {
            verifiedUserId = 1; // 테스트 계정 ID
            cardResult.setVisibility(View.VISIBLE);
            Toast.makeText(this, "본인 확인이 완료되었습니다. 새 비밀번호를 설정해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "일치하는 회원 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            cardResult.setVisibility(View.GONE);
        }
    }

    private void handleResetPassword() {
        if (verifiedUserId == -1) {
            Toast.makeText(this, "먼저 본인 확인을 진행해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // 입력 검증
        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("새 비밀번호를 입력해주세요");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 8) {
            etNewPassword.setError("비밀번호는 8자 이상이어야 합니다");
            etNewPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("비밀번호 확인을 입력해주세요");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("비밀번호가 일치하지 않습니다");
            etConfirmPassword.requestFocus();
            return;
        }

        // TODO: 실제 데이터베이스에서 비밀번호 업데이트
        // int result = userDAO.updatePassword(verifiedUserId, newPassword);

        Toast.makeText(this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();

        // 로그인 화면으로 이동
        Intent intent = new Intent(FindPasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
