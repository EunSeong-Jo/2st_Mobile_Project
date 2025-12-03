package com.example.mobile_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.database.UserDAO;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 아이디 찾기 화면
 * 이름과 전화번호로 이메일(아이디) 찾기
 */
public class FindIdActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etName, etPhone;
    private MaterialButton btnFindId, btnGoLogin;
    private MaterialCardView cardResult;
    private TextView tvFoundEmail;

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        userDAO = new UserDAO(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        btnFindId = findViewById(R.id.btn_find_id);
        btnGoLogin = findViewById(R.id.btn_go_login);
        cardResult = findViewById(R.id.card_result);
        tvFoundEmail = findViewById(R.id.tv_found_email);
    }

    private void setupClickListeners() {
        // 뒤로가기
        toolbar.setNavigationOnClickListener(v -> finish());

        // 아이디 찾기
        btnFindId.setOnClickListener(v -> handleFindId());

        // 로그인하러 가기
        btnGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(FindIdActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void handleFindId() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // 입력 검증
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

        // TODO: 실제 데이터베이스에서 이름과 전화번호로 이메일 찾기
        // 현재는 테스트 계정 확인
        if ("테스트 사용자".equals(name) && "01012345678".equals(phone)) {
            showResult("dmu@dongyang.ac.kr");
        } else {
            Toast.makeText(this, "일치하는 회원 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            cardResult.setVisibility(View.GONE);
        }
    }

    private void showResult(String email) {
        tvFoundEmail.setText(email);
        cardResult.setVisibility(View.VISIBLE);
        Toast.makeText(this, "아이디를 찾았습니다.", Toast.LENGTH_SHORT).show();
    }
}
