package com.example.mobile_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.database.UserDAO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 고용주 회원가입 화면
 */
public class SignupEmployerActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etPasswordConfirm;
    private TextInputEditText etName, etBusinessName, etBusinessNumber, etPhone;
    private MaterialButton btnSignup;

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_employer);

        userDAO = new UserDAO(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        etName = findViewById(R.id.et_name);
        etBusinessName = findViewById(R.id.et_business_name);
        etBusinessNumber = findViewById(R.id.et_business_number);
        etPhone = findViewById(R.id.et_phone);
        btnSignup = findViewById(R.id.btn_signup);
    }

    private void setupClickListeners() {
        btnSignup.setOnClickListener(v -> handleSignup());
    }

    private void handleSignup() {
        // 입력값 가져오기
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirm = etPasswordConfirm.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String businessName = etBusinessName.getText().toString().trim();
        String businessNumber = etBusinessNumber.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // 입력 검증
        if (!validateInput(email, password, passwordConfirm, name, businessName, businessNumber, phone)) {
            return;
        }

        // 이메일 중복 체크
        if (userDAO.isEmailExists(email)) {
            etEmail.setError("이미 사용 중인 이메일입니다");
            etEmail.requestFocus();
            return;
        }

        // 회원가입 처리
        long result = userDAO.registerEmployer(email, password, name, phone, businessName, businessNumber);

        if (result > 0) {
            Toast.makeText(this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();

            // 로그인 화면으로 이동
            Intent intent = new Intent(SignupEmployerActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String email, String password, String passwordConfirm,
                                   String name, String businessName, String businessNumber, String phone) {
        // 이메일 검증
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("이메일을 입력해주세요");
            etEmail.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("올바른 이메일 형식이 아닙니다");
            etEmail.requestFocus();
            return false;
        }

        // 비밀번호 검증
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("비밀번호를 입력해주세요");
            etPassword.requestFocus();
            return false;
        }
        if (password.length() < 8) {
            etPassword.setError("비밀번호는 8자 이상이어야 합니다");
            etPassword.requestFocus();
            return false;
        }

        // 비밀번호 확인
        if (TextUtils.isEmpty(passwordConfirm)) {
            etPasswordConfirm.setError("비밀번호 확인을 입력해주세요");
            etPasswordConfirm.requestFocus();
            return false;
        }
        if (!password.equals(passwordConfirm)) {
            etPasswordConfirm.setError("비밀번호가 일치하지 않습니다");
            etPasswordConfirm.requestFocus();
            return false;
        }

        // 이름 검증
        if (TextUtils.isEmpty(name)) {
            etName.setError("담당자 이름을 입력해주세요");
            etName.requestFocus();
            return false;
        }

        // 사업자명 검증
        if (TextUtils.isEmpty(businessName)) {
            etBusinessName.setError("사업자명을 입력해주세요");
            etBusinessName.requestFocus();
            return false;
        }

        // 사업자 번호 검증
        if (TextUtils.isEmpty(businessNumber)) {
            etBusinessNumber.setError("사업자 번호를 입력해주세요");
            etBusinessNumber.requestFocus();
            return false;
        }

        // 휴대폰 번호 검증
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("연락처를 입력해주세요");
            etPhone.requestFocus();
            return false;
        }

        return true;
    }
}