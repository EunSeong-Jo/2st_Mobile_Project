package com.example.mobile_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.activity.JobDetailActivity;
import com.example.mobile_project.adapter.JobListAdapter;
import com.example.mobile_project.database.JobPostingDAO;
import com.example.mobile_project.model.JobPosting;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.List;

/**
 * 채용공고 Fragment
 * 채용공고 목록, 검색, 필터링 기능
 */
public class JobsFragment extends Fragment {

    private MaterialCardView cvSearch;
    private RecyclerView rvJobList;
    private LinearLayout layoutEmpty;
    private Chip chipAll, chipBookmarked, chipNearby;

    private JobPostingDAO jobPostingDAO;
    private JobListAdapter jobListAdapter;
    private String currentFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);

        jobPostingDAO = new JobPostingDAO(requireContext());

        initViews(view);
        setupClickListeners();
        setupRecyclerView();
        loadJobPostings();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 화면으로 돌아왔을 때 목록 새로고침
        loadJobPostings();
    }

    private void initViews(View view) {
        cvSearch = view.findViewById(R.id.cv_search);
        rvJobList = view.findViewById(R.id.rv_job_list);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        chipAll = view.findViewById(R.id.chip_all);
        chipBookmarked = view.findViewById(R.id.chip_bookmarked);
        chipNearby = view.findViewById(R.id.chip_nearby);
    }

    private void setupClickListeners() {
        // 검색 바 클릭
        cvSearch.setOnClickListener(v -> {
            // TODO: SearchActivity 구현 필요
            Toast.makeText(requireContext(), "검색 기능 준비 중", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(requireContext(), SearchActivity.class);
            // startActivity(intent);
        });

        // 필터 칩 클릭
        chipAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateChipStates();
            loadJobPostings();
        });
        chipBookmarked.setOnClickListener(v -> {
            currentFilter = "bookmarked";
            updateChipStates();
            filterJobPostings("bookmarked");
        });
        chipNearby.setOnClickListener(v -> {
            currentFilter = "nearby";
            updateChipStates();
            filterJobPostings("nearby");
        });
    }

    private void updateChipStates() {
        chipAll.setChecked("all".equals(currentFilter));
        chipBookmarked.setChecked("bookmarked".equals(currentFilter));
        chipNearby.setChecked("nearby".equals(currentFilter));
    }

    private void setupRecyclerView() {
        rvJobList.setLayoutManager(new LinearLayoutManager(getContext()));
        jobListAdapter = new JobListAdapter();
        rvJobList.setAdapter(jobListAdapter);

        // 공고 클릭 이벤트
        jobListAdapter.setOnItemClickListener((jobPosting, position) -> {
            Intent intent = new Intent(requireContext(), JobDetailActivity.class);
            intent.putExtra("job_posting_id", jobPosting.getId());
            startActivity(intent);
        });
    }

    private void loadJobPostings() {
        List<JobPosting> jobPostings = jobPostingDAO.getAllActiveJobPostings();

        if (jobPostings.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            jobListAdapter.setJobPostingList(jobPostings);
        }
    }

    private void filterJobPostings(String filter) {
        List<JobPosting> jobPostings;

        switch (filter) {
            case "bookmarked":
                // TODO: 북마크한 공고만 (현재는 전체 목록 표시)
                jobPostings = jobPostingDAO.getAllActiveJobPostings();
                break;
            case "nearby":
                // TODO: 내 주변 공고만 (현재는 구로 지역 공고 필터)
                jobPostings = jobPostingDAO.filterJobPostings(null, "구로", null);
                break;
            case "all":
            default:
                jobPostings = jobPostingDAO.getAllActiveJobPostings();
                break;
        }

        if (jobPostings.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            jobListAdapter.setJobPostingList(jobPostings);
        }
    }

    private void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            rvJobList.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvJobList.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}