package com.example.mobile_project.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.database.ApplicationDAO;
import com.example.mobile_project.database.JobPostingDAO;
import com.example.mobile_project.model.JobPosting;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 지원서 작성 화면
 */
public class ApplicationFormActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvJobTitle, tvCompanyName;
    private TextInputEditText etResume, etCoverLetter;
    private MaterialButton btnSubmit;

    private ApplicationDAO applicationDAO;
    private JobPostingDAO jobPostingDAO;
    private int jobPostingId;
    private int currentUserId = 1; // TODO: 실제 로그인 사용자 ID 가져오기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_form);

        applicationDAO = new ApplicationDAO(this);
        jobPostingDAO = new JobPostingDAO(this);

        // Intent에서 공고 ID 가져오기
        jobPostingId = getIntent().getIntExtra("job_posting_id", -1);
        if (jobPostingId == -1) {
            Toast.makeText(this, "잘못된 접근입니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 이미 지원했는지 체크
        if (applicationDAO.hasAlreadyApplied(jobPostingId, currentUserId)) {
            Toast.makeText(this, "이미 지원한 공고입니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadJobPosting();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvCompanyName = findViewById(R.id.tv_company_name);
        etResume = findViewById(R.id.et_resume);
        etCoverLetter = findViewById(R.id.et_cover_letter);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void setupToolbar() {
        toolbar.setTitle("지원하기");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadJobPosting() {
        JobPosting jobPosting = jobPostingDAO.getJobPostingById(jobPostingId);

        if (jobPosting == null) {
            Toast.makeText(this, "공고를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvJobTitle.setText(jobPosting.getTitle());
        tvCompanyName.setText(jobPosting.getCompanyName());
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> submitApplication());
    }

    private void submitApplication() {
        // 입력값 가져오기
        String resume = etResume.getText().toString().trim();
        String coverLetter = etCoverLetter.getText().toString().trim();

        // 입력 검증
        if (!validateInput(resume, coverLetter)) {
            return;
        }

        // 지원서 제출
        long result = applicationDAO.createApplication(jobPostingId, currentUserId, resume, coverLetter);

        if (result > 0) {
            Toast.makeText(this, "지원이 완료되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "지원에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String resume, String coverLetter) {
        // 이력서 검증
        if (TextUtils.isEmpty(resume)) {
            etResume.setError("이력서를 입력해주세요");
            etResume.requestFocus();
            return false;
        }

        if (resume.length() < 50) {
            etResume.setError("이력서는 최소 50자 이상 작성해주세요");
            etResume.requestFocus();
            return false;
        }

        // 자기소개서 검증
        if (TextUtils.isEmpty(coverLetter)) {
            etCoverLetter.setError("자기소개서를 입력해주세요");
            etCoverLetter.requestFocus();
            return false;
        }

        if (coverLetter.length() < 100) {
            etCoverLetter.setError("자기소개서는 최소 100자 이상 작성해주세요");
            etCoverLetter.requestFocus();
            return false;
        }

        return true;
    }
}