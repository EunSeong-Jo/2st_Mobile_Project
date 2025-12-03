package com.example.mobile_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mobile_project.R;
import com.example.mobile_project.adapter.SchoolNoticeAdapter;
import com.example.mobile_project.model.SchoolNotice;
import com.example.mobile_project.util.SchoolNoticeService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

/**
 * 알림 Fragment
 * 지원 결과, 학교 공지 (재학생 전용) 알림 표시
 */
public class NotificationsFragment extends Fragment {

    private TabLayout tabLayout;
    private MaterialButton btnMarkAllRead;
    private RecyclerView rvNotificationList;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SchoolNoticeAdapter schoolNoticeAdapter;
    private SchoolNoticeService schoolNoticeService;
    private String userType = "student"; // TODO: 실제 사용자 타입 가져오기

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        initViews(view);
        setupUserTypeSpecificUI();
        setupClickListeners();
        setupRecyclerView();
        loadNotifications();

        return view;
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        btnMarkAllRead = view.findViewById(R.id.btn_mark_all_read);
        rvNotificationList = view.findViewById(R.id.rv_notification_list);
        layoutEmpty = view.findViewById(R.id.layout_empty);
    }

    private void setupUserTypeSpecificUI() {
        // 고용주는 "학교 공지" 탭 숨김
        if ("employer".equals(userType)) {
            TabLayout.Tab schoolTab = tabLayout.getTabAt(2);
            if (schoolTab != null) {
                tabLayout.removeTab(schoolTab);
            }
        }
    }

    private void setupClickListeners() {
        // 모두 읽음 버튼
        btnMarkAllRead.setOnClickListener(v -> {
            // TODO: 모든 알림 읽음 처리
            markAllAsRead();
        });

        // 탭 선택 리스너
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterNotifications(tab.getPosition());
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
        rvNotificationList.setLayoutManager(new LinearLayoutManager(getContext()));
        schoolNoticeAdapter = new SchoolNoticeAdapter(getContext());
        rvNotificationList.setAdapter(schoolNoticeAdapter);

        schoolNoticeService = new SchoolNoticeService();
    }

    private void loadNotifications() {
        // 학교 공지사항 불러오기 (재학생 전용)
        if ("student".equals(userType)) {
            loadSchoolNotices();
        } else {
            // 고용주는 공지사항 없음
            showEmptyState(true);
        }
    }

    private void loadSchoolNotices() {
        showLoading(true);

        schoolNoticeService.fetchNotices(new SchoolNoticeService.NoticeCallback() {
            @Override
            public void onSuccess(List<SchoolNotice> notices) {
                showLoading(false);
                if (notices.isEmpty()) {
                    showEmptyState(true);
                } else {
                    showEmptyState(false);
                    schoolNoticeAdapter.setNoticeList(notices);
                }
            }

            @Override
            public void onError(String errorMessage) {
                showLoading(false);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                showEmptyState(true);
            }
        });
    }

    private void filterNotifications(int tabPosition) {
        switch (tabPosition) {
            case 0:
                // 전체 알림
                loadNotifications();
                break;
            case 1:
                // 지원 결과만
                // TODO: 지원 결과 알림 표시
                showEmptyState(true);
                break;
            case 2:
                // 학교 공지만 (재학생만)
                loadSchoolNotices();
                break;
        }
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void markAllAsRead() {
        // TODO: 모든 알림을 읽음 상태로 업데이트
    }

    private void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            rvNotificationList.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvNotificationList.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}