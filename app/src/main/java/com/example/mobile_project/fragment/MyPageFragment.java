package com.example.mobile_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile_project.R;
import com.example.mobile_project.activity.LoginActivity;
import com.example.mobile_project.activity.MyApplicationsActivity;
import com.example.mobile_project.util.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * 마이페이지 Fragment
 * 사용자 정보, 내 활동, 설정
 */
public class MyPageFragment extends Fragment {

    private TextView tvProfileInitial, tvUserName, tvUserType, tvUserDetail;
    private TextView tvSectionActivity;
    private ImageView ivEditProfile;
    private LinearLayout menuBookmark, menuApplicationHistory, menuMyPostings, menuMyReviews;
    private LinearLayout menuAccountSettings, menuNotificationSettings, menuCustomerService;
    private MaterialButton btnLogout;

    private PreferenceManager preferenceManager;
    private String userType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        preferenceManager = new PreferenceManager(requireContext());
        userType = preferenceManager.getUserType();

        initViews(view);
        setupUserTypeSpecificUI();
        setupClickListeners();
        loadUserData();

        return view;
    }

    private void initViews(View view) {
        tvProfileInitial = view.findViewById(R.id.tv_profile_initial);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserType = view.findViewById(R.id.tv_user_type);
        tvUserDetail = view.findViewById(R.id.tv_user_detail);
        tvSectionActivity = view.findViewById(R.id.tv_section_activity);
        ivEditProfile = view.findViewById(R.id.iv_edit_profile);

        menuBookmark = view.findViewById(R.id.menu_bookmark);
        menuApplicationHistory = view.findViewById(R.id.menu_application_history);
        menuMyPostings = view.findViewById(R.id.menu_my_postings);
        menuMyReviews = view.findViewById(R.id.menu_my_reviews);

        menuAccountSettings = view.findViewById(R.id.menu_account_settings);
        menuNotificationSettings = view.findViewById(R.id.menu_notification_settings);
        menuCustomerService = view.findViewById(R.id.menu_customer_service);

        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void setupUserTypeSpecificUI() {
        // 재학생 vs 고용주에 따라 메뉴 구성 변경
        if ("student".equals(userType)) {
            // 재학생: 지원 내역 표시, 내 공고 관리 숨김
            menuApplicationHistory.setVisibility(View.VISIBLE);
            menuMyPostings.setVisibility(View.GONE);
            tvSectionActivity.setText("내 활동");
        } else {
            // 고용주: 지원 내역 숨김, 내 공고 관리 표시
            menuApplicationHistory.setVisibility(View.GONE);
            menuMyPostings.setVisibility(View.VISIBLE);
            tvSectionActivity.setText("공고 관리");
        }
    }

    private void setupClickListeners() {
        // 프로필 편집
        ivEditProfile.setOnClickListener(v -> {
            // TODO: 프로필 편집 화면으로 이동
            Toast.makeText(getContext(), "프로필 편집 기능 준비 중", Toast.LENGTH_SHORT).show();
        });

        // 북마크
        menuBookmark.setOnClickListener(v -> {
            // TODO: 북마크 목록 화면으로 이동
            Toast.makeText(getContext(), "북마크 기능 준비 중", Toast.LENGTH_SHORT).show();
        });

        // 지원 내역 (재학생)
        menuApplicationHistory.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyApplicationsActivity.class);
            startActivity(intent);
        });

        // 내 공고 관리 (고용주)
        menuMyPostings.setOnClickListener(v -> {
            // TODO: 내 공고 관리 화면으로 이동
            Toast.makeText(getContext(), "내 공고 관리 기능 준비 중", Toast.LENGTH_SHORT).show();
        });

        // 내가 쓴 리뷰
        menuMyReviews.setOnClickListener(v -> {
            // TODO: 내 리뷰 목록 화면으로 이동
            Toast.makeText(getContext(), "리뷰 기능 준비 중", Toast.LENGTH_SHORT).show();
        });

        // 계정 설정
        menuAccountSettings.setOnClickListener(v -> {
            // TODO: 계정 설정 화면으로 이동
            Toast.makeText(getContext(), "계정 설정 기능 준비 중", Toast.LENGTH_SHORT).show();
        });

        // 알림 설정
        menuNotificationSettings.setOnClickListener(v -> {
            // TODO: 알림 설정 화면으로 이동
            Toast.makeText(getContext(), "알림 설정 기능 준비 중", Toast.LENGTH_SHORT).show();
        });

        // 고객센터
        menuCustomerService.setOnClickListener(v -> {
            // TODO: 고객센터 화면으로 이동
            Toast.makeText(getContext(), "고객센터 기능 준비 중", Toast.LENGTH_SHORT).show();
        });

        // 로그아웃
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadUserData() {
        // SharedPreferences에서 사용자 정보 로드
        String userName = preferenceManager.getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvProfileInitial.setText(userName.substring(0, 1));
            tvUserName.setText(userName);
        } else {
            // 기본값
            tvProfileInitial.setText("?");
            tvUserName.setText("사용자");
        }

        if ("student".equals(userType)) {
            tvUserType.setText("재학생");
            // TODO: 데이터베이스에서 학과, 학년 정보 로드
            tvUserDetail.setText("컴퓨터정보공학과 · 2학년");
        } else {
            tvUserType.setText("고용주");
            // TODO: 데이터베이스에서 회사명 정보 로드
            tvUserDetail.setText("CU 구로점");
        }
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void performLogout() {
        // SharedPreferences 자동 로그인 정보 삭제
        preferenceManager.logout();

        // 로그인 화면으로 이동
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}