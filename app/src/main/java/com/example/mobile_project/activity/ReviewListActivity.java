package com.example.mobile_project.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.adapter.ReviewAdapter;
import com.example.mobile_project.database.JobPostingDAO;
import com.example.mobile_project.database.ReviewDAO;
import com.example.mobile_project.model.JobPosting;
import com.example.mobile_project.model.Review;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

/**
 * 리뷰 목록 화면
 */
public class ReviewListActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvJobTitle, tvCompanyName, tvAverageRating, tvReviewCount;
    private RecyclerView rvReviews;
    private LinearLayout layoutEmpty;

    private ReviewDAO reviewDAO;
    private JobPostingDAO jobPostingDAO;
    private ReviewAdapter reviewAdapter;
    private int jobPostingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        reviewDAO = new ReviewDAO(this);
        jobPostingDAO = new JobPostingDAO(this);

        // Intent에서 공고 ID 가져오기
        jobPostingId = getIntent().getIntExtra("job_posting_id", -1);
        if (jobPostingId == -1) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadJobPosting();
        setupRecyclerView();
        loadReviews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvCompanyName = findViewById(R.id.tv_company_name);
        tvAverageRating = findViewById(R.id.tv_average_rating);
        tvReviewCount = findViewById(R.id.tv_review_count);
        rvReviews = findViewById(R.id.rv_reviews);
        layoutEmpty = findViewById(R.id.layout_empty);
    }

    private void setupToolbar() {
        toolbar.setTitle("리뷰 목록");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadJobPosting() {
        JobPosting jobPosting = jobPostingDAO.getJobPostingById(jobPostingId);

        if (jobPosting != null) {
            tvJobTitle.setText(jobPosting.getTitle());
            tvCompanyName.setText(jobPosting.getCompanyName());
        }

        // 평균 평점 및 리뷰 개수
        float avgRating = reviewDAO.getAverageRating(jobPostingId);
        int reviewCount = reviewDAO.getReviewCount(jobPostingId);

        tvAverageRating.setText(String.format("%.1f", avgRating));
        tvReviewCount.setText(String.format("(%d개의 리뷰)", reviewCount));
    }

    private void setupRecyclerView() {
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter();
        rvReviews.setAdapter(reviewAdapter);
    }

    private void loadReviews() {
        List<Review> reviews = reviewDAO.getReviewsByJobPostingId(jobPostingId);

        if (reviews.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            reviewAdapter.setReviewList(reviews);
        }
    }

    private void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            rvReviews.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvReviews.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}