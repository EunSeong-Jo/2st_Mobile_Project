package com.example.mobile_project.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.database.JobPostingDAO;
import com.example.mobile_project.model.JobPosting;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 채용공고 작성/수정 화면
 */
public class JobFormActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etCompanyName, etTitle, etSalary, etLocation;
    private TextInputEditText etWorkTime, etWorkDays, etDescription, etRequirements;
    private MaterialButton btnSubmit;

    private JobPostingDAO jobPostingDAO;
    private int jobPostingId = -1; // -1이면 새 공고, 아니면 수정
    private boolean isEditMode = false;
    private int currentUserId = 1; // TODO: 실제 로그인 사용자 ID 가져오기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_form);

        jobPostingDAO = new JobPostingDAO(this);

        // Intent에서 공고 ID 가져오기 (수정 모드인 경우)
        jobPostingId = getIntent().getIntExtra("job_posting_id", -1);
        isEditMode = (jobPostingId != -1);

        initViews();
        setupToolbar();
        setupClickListeners();

        if (isEditMode) {
            loadJobPosting();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etCompanyName = findViewById(R.id.et_company_name);
        etTitle = findViewById(R.id.et_title);
        etSalary = findViewById(R.id.et_salary);
        etLocation = findViewById(R.id.et_location);
        etWorkTime = findViewById(R.id.et_work_time);
        etWorkDays = findViewById(R.id.et_work_days);
        etDescription = findViewById(R.id.et_description);
        etRequirements = findViewById(R.id.et_requirements);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void setupToolbar() {
        if (isEditMode) {
            toolbar.setTitle("채용공고 수정");
            btnSubmit.setText("수정하기");
        } else {
            toolbar.setTitle("채용공고 작성");
            btnSubmit.setText("등록하기");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> {
            if (isEditMode) {
                updateJobPosting();
            } else {
                createJobPosting();
            }
        });
    }

    private void loadJobPosting() {
        JobPosting jobPosting = jobPostingDAO.getJobPostingById(jobPostingId);

        if (jobPosting == null) {
            Toast.makeText(this, "공고를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 폼에 데이터 채우기
        etCompanyName.setText(jobPosting.getCompanyName());
        etTitle.setText(jobPosting.getTitle());
        etSalary.setText(String.valueOf(jobPosting.getSalary()));
        etLocation.setText(jobPosting.getLocation());
        etWorkTime.setText(jobPosting.getWorkTime());
        etWorkDays.setText(jobPosting.getWorkDays());
        etDescription.setText(jobPosting.getDescription());
        etRequirements.setText(jobPosting.getRequirements());
    }

    private void createJobPosting() {
        // 입력값 가져오기
        String companyName = etCompanyName.getText().toString().trim();
        String title = etTitle.getText().toString().trim();
        String salaryStr = etSalary.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String workTime = etWorkTime.getText().toString().trim();
        String workDays = etWorkDays.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String requirements = etRequirements.getText().toString().trim();

        // 입력 검증
        if (!validateInput(companyName, title, salaryStr, location, workTime)) {
            return;
        }

        int salary = Integer.parseInt(salaryStr);

        // 공고 등록
        long result = jobPostingDAO.createJobPosting(currentUserId, companyName, title, description,
                salary, location, workTime, workDays, requirements);

        if (result > 0) {
            Toast.makeText(this, "공고가 등록되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "공고 등록에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateJobPosting() {
        // 입력값 가져오기
        String title = etTitle.getText().toString().trim();
        String salaryStr = etSalary.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String workTime = etWorkTime.getText().toString().trim();
        String workDays = etWorkDays.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String requirements = etRequirements.getText().toString().trim();

        // 입력 검증 (회사명 제외)
        if (!validateInput("dummy", title, salaryStr, location, workTime)) {
            return;
        }

        int salary = Integer.parseInt(salaryStr);

        // 공고 수정
        int result = jobPostingDAO.updateJobPosting(jobPostingId, title, description,
                salary, location, workTime, workDays, requirements);

        if (result > 0) {
            Toast.makeText(this, "공고가 수정되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "공고 수정에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String companyName, String title, String salaryStr,
                                   String location, String workTime) {
        // 회사명 검증 (작성 모드만)
        if (!isEditMode && TextUtils.isEmpty(companyName)) {
            etCompanyName.setError("회사명을 입력해주세요");
            etCompanyName.requestFocus();
            return false;
        }

        // 제목 검증
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("공고 제목을 입력해주세요");
            etTitle.requestFocus();
            return false;
        }

        // 시급 검증
        if (TextUtils.isEmpty(salaryStr)) {
            etSalary.setError("시급을 입력해주세요");
            etSalary.requestFocus();
            return false;
        }
        try {
            int salary = Integer.parseInt(salaryStr);
            if (salary < 0) {
                etSalary.setError("올바른 금액을 입력해주세요");
                etSalary.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etSalary.setError("숫자만 입력해주세요");
            etSalary.requestFocus();
            return false;
        }

        // 지역 검증
        if (TextUtils.isEmpty(location)) {
            etLocation.setError("근무 지역을 입력해주세요");
            etLocation.requestFocus();
            return false;
        }

        // 근무시간 검증
        if (TextUtils.isEmpty(workTime)) {
            etWorkTime.setError("근무 시간을 입력해주세요");
            etWorkTime.requestFocus();
            return false;
        }

        return true;
    }
}