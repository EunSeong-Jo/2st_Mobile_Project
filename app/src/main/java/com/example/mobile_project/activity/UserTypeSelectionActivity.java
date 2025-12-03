package com.example.mobile_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mobile_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * 사용자 유형 선택 화면
 * 재학생 또는 고용주 중 선택
 */
public class UserTypeSelectionActivity extends AppCompatActivity {

    private MaterialCardView cardStudent, cardEmployer;
    private MaterialButton btnNext;
    private String selectedUserType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_selection);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        cardStudent = findViewById(R.id.card_student);
        cardEmployer = findViewById(R.id.card_employer);
        btnNext = findViewById(R.id.btn_next);
    }

    private void setupClickListeners() {
        // 재학생 카드 선택
        cardStudent.setOnClickListener(v -> {
            selectedUserType = "student";
            updateCardSelection();
            btnNext.setEnabled(true);
        });

        // 고용주 카드 선택
        cardEmployer.setOnClickListener(v -> {
            selectedUserType = "employer";
            updateCardSelection();
            btnNext.setEnabled(true);
        });

        // 다음 단계 버튼
        btnNext.setOnClickListener(v -> {
            if (selectedUserType != null) {
                Intent intent;
                if ("student".equals(selectedUserType)) {
                    // 재학생 회원가입 화면으로 이동
                    intent = new Intent(UserTypeSelectionActivity.this, SignupStudentActivity.class);
                } else {
                    // 고용주 회원가입 화면으로 이동
                    intent = new Intent(UserTypeSelectionActivity.this, SignupEmployerActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateCardSelection() {
        // 재학생 카드 스타일
        if ("student".equals(selectedUserType)) {
            cardStudent.setCardBackgroundColor(getColor(R.color.blue_50));
            cardStudent.setStrokeWidth(4);
            cardEmployer.setCardBackgroundColor(getColor(R.color.white));
            cardEmployer.setStrokeWidth(2);
        }
        // 고용주 카드 스타일
        else if ("employer".equals(selectedUserType)) {
            cardEmployer.setCardBackgroundColor(getColor(R.color.blue_50));
            cardEmployer.setStrokeWidth(4);
            cardStudent.setCardBackgroundColor(getColor(R.color.white));
            cardStudent.setStrokeWidth(2);
        }
    }
}
