package com.example.mobile_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.database.JobPostingDAO;
import com.example.mobile_project.model.JobPosting;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * 채용공고 상세 화면
 */
public class JobDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvCompanyName, tvJobTitle, tvSalary;
    private TextView tvLocation, tvWorkTime, tvWorkDays;
    private TextView tvDescription, tvRequirements;

    private LinearLayout layoutBottomButtons, layoutEmployerButtons;
    private MaterialButton btnBookmark, btnApply;
    private MaterialButton btnEdit, btnDelete;

    private JobPostingDAO jobPostingDAO;
    private int jobPostingId;
    private JobPosting jobPosting;
    private int currentUserId; // TODO: 실제 로그인 사용자 ID 가져오기
    private boolean isEmployer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        jobPostingDAO = new JobPostingDAO(this);

        // Intent에서 공고 ID 가져오기
        jobPostingId = getIntent().getIntExtra("job_posting_id", -1);
        if (jobPostingId == -1) {
            Toast.makeText(this, "잘못된 접근입니다", Toast.LENGTH_SHORT).show();
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
        tvCompanyName = findViewById(R.id.tv_company_name);
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvSalary = findViewById(R.id.tv_salary);
        tvLocation = findViewById(R.id.tv_location);
        tvWorkTime = findViewById(R.id.tv_work_time);
        tvWorkDays = findViewById(R.id.tv_work_days);
        tvDescription = findViewById(R.id.tv_description);
        tvRequirements = findViewById(R.id.tv_requirements);

        layoutBottomButtons = findViewById(R.id.layout_bottom_buttons);
        layoutEmployerButtons = findViewById(R.id.layout_employer_buttons);
        btnBookmark = findViewById(R.id.btn_bookmark);
        btnApply = findViewById(R.id.btn_apply);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadJobPosting() {
        jobPosting = jobPostingDAO.getJobPostingById(jobPostingId);

        if (jobPosting == null) {
            Toast.makeText(this, "공고를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 조회수 증가
        jobPostingDAO.incrementViewCount(jobPostingId);

        // UI 업데이트
        tvCompanyName.setText(jobPosting.getCompanyName());
        tvJobTitle.setText(jobPosting.getTitle());
        tvSalary.setText(String.format("%,d원", jobPosting.getSalary()));
        tvLocation.setText(jobPosting.getLocation());
        tvWorkTime.setText(jobPosting.getWorkTime());
        tvWorkDays.setText(jobPosting.getWorkDays() != null ? jobPosting.getWorkDays() : "-");
        tvDescription.setText(jobPosting.getDescription() != null ? jobPosting.getDescription() : "상세 설명이 없습니다.");
        tvRequirements.setText(jobPosting.getRequirements() != null ? jobPosting.getRequirements() : "우대사항이 없습니다.");

        // 버튼 표시 (고용주 본인 공고인 경우 수정/삭제 버튼 표시)
        // TODO: 실제 로그인 사용자 ID와 비교
        isEmployer = (jobPosting.getEmployerId() == currentUserId);

        if (isEmployer) {
            layoutBottomButtons.setVisibility(View.GONE);
            layoutEmployerButtons.setVisibility(View.VISIBLE);
        } else {
            layoutBottomButtons.setVisibility(View.VISIBLE);
            layoutEmployerButtons.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // 북마크 버튼
        btnBookmark.setOnClickListener(v -> {
            // TODO: 북마크 기능 구현
            Toast.makeText(this, "북마크 기능 준비 중", Toast.LENGTH_SHORT).show();
        });

        // 지원하기 버튼
        btnApply.setOnClickListener(v -> {
            Intent intent = new Intent(JobDetailActivity.this, ApplicationFormActivity.class);
            intent.putExtra("job_posting_id", jobPostingId);
            startActivity(intent);
        });

        // 수정 버튼
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(JobDetailActivity.this, JobFormActivity.class);
            intent.putExtra("job_posting_id", jobPostingId);
            startActivity(intent);
        });

        // 삭제 버튼
        btnDelete.setOnClickListener(v -> showDeleteDialog());
    }

    private void showDeleteDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("공고 삭제")
                .setMessage("정말 이 공고를 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    int result = jobPostingDAO.deleteJobPosting(jobPostingId);
                    if (result > 0) {
                        Toast.makeText(this, "공고가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 수정 후 돌아왔을 때 데이터 다시 로드
        if (jobPostingId != -1) {
            loadJobPosting();
        }
    }
}