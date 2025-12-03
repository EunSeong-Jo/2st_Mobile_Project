package com.example.mobile_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.MainActivity;
import com.example.mobile_project.R;
import com.example.mobile_project.database.UserDAO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 재학생 회원가입 화면
 */
public class SignupStudentActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etPasswordConfirm;
    private TextInputEditText etName, etStudentId, etDepartment, etGrade, etPhone;
    private MaterialButton btnSignup;

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_student);

        userDAO = new UserDAO(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        etName = findViewById(R.id.et_name);
        etStudentId = findViewById(R.id.et_student_id);
        etDepartment = findViewById(R.id.et_department);
        etGrade = findViewById(R.id.et_grade);
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
        String studentId = etStudentId.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String gradeStr = etGrade.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // 입력 검증
        if (!validateInput(email, password, passwordConfirm, name, studentId, department, gradeStr, phone)) {
            return;
        }

        int grade = Integer.parseInt(gradeStr);

        // 이메일 중복 체크
        if (userDAO.isEmailExists(email)) {
            etEmail.setError("이미 사용 중인 이메일입니다");
            etEmail.requestFocus();
            return;
        }

        // 회원가입 처리
        long result = userDAO.registerStudent(email, password, name, phone, studentId, department, grade);

        if (result > 0) {
            Toast.makeText(this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();

            // 로그인 화면으로 이동
            Intent intent = new Intent(SignupStudentActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String email, String password, String passwordConfirm,
                                   String name, String studentId, String department,
                                   String gradeStr, String phone) {
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
            etName.setError("이름을 입력해주세요");
            etName.requestFocus();
            return false;
        }

        // 학번 검증
        if (TextUtils.isEmpty(studentId)) {
            etStudentId.setError("학번을 입력해주세요");
            etStudentId.requestFocus();
            return false;
        }

        // 학과 검증
        if (TextUtils.isEmpty(department)) {
            etDepartment.setError("학과를 입력해주세요");
            etDepartment.requestFocus();
            return false;
        }

        // 학년 검증
        if (TextUtils.isEmpty(gradeStr)) {
            etGrade.setError("학년을 입력해주세요");
            etGrade.requestFocus();
            return false;
        }
        try {
            int grade = Integer.parseInt(gradeStr);
            if (grade < 1 || grade > 4) {
                etGrade.setError("학년은 1~4 사이여야 합니다");
                etGrade.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etGrade.setError("올바른 학년을 입력해주세요");
            etGrade.requestFocus();
            return false;
        }

        // 휴대폰 번호 검증
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("휴대폰 번호를 입력해주세요");
            etPhone.requestFocus();
            return false;
        }

        return true;
    }
}