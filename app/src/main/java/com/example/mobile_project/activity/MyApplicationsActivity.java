package com.example.mobile_project.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.adapter.ApplicationAdapter;
import com.example.mobile_project.database.ApplicationDAO;
import com.example.mobile_project.model.Application;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

/**
 * 내 지원 내역 화면 (재학생용)
 */
public class MyApplicationsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView rvApplications;
    private LinearLayout layoutEmpty;

    private ApplicationDAO applicationDAO;
    private ApplicationAdapter applicationAdapter;
    private int currentUserId = 1; // TODO: 실제 로그인 사용자 ID 가져오기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        applicationDAO = new ApplicationDAO(this);

        initViews();
        setupToolbar();
        setupTabs();
        setupRecyclerView();
        loadApplications("all");
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        rvApplications = findViewById(R.id.rv_applications);
        layoutEmpty = findViewById(R.id.layout_empty);
    }

    private void setupToolbar() {
        toolbar.setTitle("지원 내역");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("전체"));
        tabLayout.addTab(tabLayout.newTab().setText("대기 중"));
        tabLayout.addTab(tabLayout.newTab().setText("합격"));
        tabLayout.addTab(tabLayout.newTab().setText("불합격"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        loadApplications("all");
                        break;
                    case 1:
                        loadApplications("pending");
                        break;
                    case 2:
                        loadApplications("accepted");
                        break;
                    case 3:
                        loadApplications("rejected");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupRecyclerView() {
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        applicationAdapter = new ApplicationAdapter();
        rvApplications.setAdapter(applicationAdapter);

        // TODO: 지원 내역 클릭 이벤트 추가
    }

    private void loadApplications(String filter) {
        List<Application> applications;

        if ("all".equals(filter)) {
            applications = applicationDAO.getApplicationsByStudentId(currentUserId);
        } else {
            applications = applicationDAO.getApplicationsByStatus(currentUserId, filter);
        }

        if (applications.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            applicationAdapter.setApplicationList(applications);
        }
    }

    private void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            rvApplications.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvApplications.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}