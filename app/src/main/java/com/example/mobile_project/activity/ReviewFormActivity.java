package com.example.mobile_project.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.database.JobPostingDAO;
import com.example.mobile_project.database.ReviewDAO;
import com.example.mobile_project.model.JobPosting;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 리뷰 작성 화면
 */
public class ReviewFormActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvJobTitle, tvCompanyName, tvRatingValue;
    private RatingBar ratingBar;
    private TextInputEditText etComment;
    private MaterialButton btnSubmit;

    private ReviewDAO reviewDAO;
    private JobPostingDAO jobPostingDAO;
    private int jobPostingId;
    private int currentUserId = 1; // TODO: 실제 로그인 사용자 ID 가져오기
    private float currentRating = 3.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_form);

        reviewDAO = new ReviewDAO(this);
        jobPostingDAO = new JobPostingDAO(this);

        // Intent에서 공고 ID 가져오기
        jobPostingId = getIntent().getIntExtra("job_posting_id", -1);
        if (jobPostingId == -1) {
            Toast.makeText(this, "잘못된 접근입니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 이미 리뷰를 작성했는지 체크
        if (reviewDAO.hasReviewed(jobPostingId, currentUserId)) {
            Toast.makeText(this, "이미 리뷰를 작성하셨습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadJobPosting();
        setupRatingBar();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvCompanyName = findViewById(R.id.tv_company_name);
        tvRatingValue = findViewById(R.id.tv_rating_value);
        ratingBar = findViewById(R.id.rating_bar);
        etComment = findViewById(R.id.et_comment);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void setupToolbar() {
        toolbar.setTitle("리뷰 작성");
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

    private void setupRatingBar() {
        ratingBar.setRating(currentRating);
        tvRatingValue.setText(String.format("%.1f", currentRating));

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            currentRating = rating;
            tvRatingValue.setText(String.format("%.1f", rating));
        });
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        // 입력값 가져오기
        String comment = etComment.getText().toString().trim();

        // 입력 검증
        if (!validateInput(comment)) {
            return;
        }

        // 리뷰 제출
        long result = reviewDAO.createReview(jobPostingId, currentUserId, currentRating, comment);

        if (result > 0) {
            Toast.makeText(this, "리뷰가 등록되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "리뷰 등록에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String comment) {
        // 평점 검증
        if (currentRating < 1.0f) {
            Toast.makeText(this, "평점을 선택해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 댓글 검증
        if (TextUtils.isEmpty(comment)) {
            etComment.setError("리뷰 내용을 입력해주세요");
            etComment.requestFocus();
            return false;
        }

        if (comment.length() < 10) {
            etComment.setError("리뷰는 최소 10자 이상 작성해주세요");
            etComment.requestFocus();
            return false;
        }

        return true;
    }
}
