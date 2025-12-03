package com.example.mobile_project.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.adapter.JobListAdapter;
import com.example.mobile_project.adapter.NearbyPlaceAdapter;
import com.example.mobile_project.database.DatabaseHelper;
import com.example.mobile_project.model.JobPosting;
import com.example.mobile_project.model.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * 홈 Fragment
 * 검색, 내 주변 알바, 추천 알바, 다가오는 일정 표시
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private TextView tvGreeting;
    private TextView tvSeeMoreNearby, tvSeeMoreRecommended;
    private LinearLayout layoutUpcomingSchedule;
    private RecyclerView rvNearbyPlaces, rvRecommendedJobs;

    private NearbyPlaceAdapter nearbyPlaceAdapter;
    private JobListAdapter recommendedJobAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupRecyclerViews();
        setupClickListeners();
        loadUserData();
        loadNearbyPlaces();
        loadRecommendedJobs();

        return view;
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvSeeMoreNearby = view.findViewById(R.id.tv_see_more_nearby);
        tvSeeMoreRecommended = view.findViewById(R.id.tv_see_more_recommended);
        layoutUpcomingSchedule = view.findViewById(R.id.layout_upcoming_schedule);
        rvNearbyPlaces = view.findViewById(R.id.rv_nearby_places);
        rvRecommendedJobs = view.findViewById(R.id.rv_recommended_jobs);
    }

    private void setupRecyclerViews() {
        // 주변 장소 RecyclerView (가로 스크롤)
        nearbyPlaceAdapter = new NearbyPlaceAdapter();
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvNearbyPlaces.setLayoutManager(horizontalLayoutManager);
        rvNearbyPlaces.setAdapter(nearbyPlaceAdapter);

        // 추천 알바 RecyclerView (세로)
        recommendedJobAdapter = new JobListAdapter();
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getContext());
        rvRecommendedJobs.setLayoutManager(verticalLayoutManager);
        rvRecommendedJobs.setAdapter(recommendedJobAdapter);
        rvRecommendedJobs.setNestedScrollingEnabled(false); // ScrollView 내부에 있으므로
    }

    private void setupClickListeners() {
        // 내 주변 알바 더보기
        tvSeeMoreNearby.setOnClickListener(v -> {
            // TODO: 채용공고 탭으로 이동
        });

        // 추천 알바 더보기
        tvSeeMoreRecommended.setOnClickListener(v -> {
            // TODO: 추천 공고 리스트로 이동
        });
    }

    private void loadUserData() {
        // TODO: 실제 사용자 데이터 로드
        // 임시: 하드코딩된 인사말
        String userName = "홍길동";
        tvGreeting.setText("안녕하세요, " + userName + "님! 👋");

        // TODO: 사용자 유형 확인 후 일정 섹션 표시 여부 결정
        // 재학생인 경우만 표시
        boolean isStudent = true;
        layoutUpcomingSchedule.setVisibility(isStudent ? View.VISIBLE : View.GONE);
    }

    /**
     * 주변 장소 데이터 로드 (거리순 상위 5개)
     */
    private void loadNearbyPlaces() {
        if (getContext() == null) return;

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Place> places = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_NEARBY_PLACES,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "distance ASC", // 거리순 정렬
                    "5" // 상위 5개만
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Place place = new Place();
                    place.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    place.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    place.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                    place.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                    place.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
                    place.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
                    place.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow("rating")));
                    place.setDistance(cursor.getInt(cursor.getColumnIndexOrThrow("distance")));

                    places.add(place);
                    Log.d(TAG, "Loaded place: " + place.getName() + " (" + place.getFormattedDistance() + ")");
                } while (cursor.moveToNext());
            } else {
                Log.w(TAG, "No nearby places found in database");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading nearby places", e);
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d(TAG, "Total nearby places loaded: " + places.size());
        nearbyPlaceAdapter.setPlaces(places);
    }

    /**
     * 추천 알바 데이터 로드 (최신순 상위 5개)
     */
    private void loadRecommendedJobs() {
        if (getContext() == null) return;

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<JobPosting> jobs = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_JOB_POSTINGS,
                    null,
                    "status = ?",
                    new String[]{"active"}, // 활성 공고만
                    null,
                    null,
                    "created_at DESC", // 최신순 정렬
                    "5" // 상위 5개만
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    JobPosting job = new JobPosting();
                    job.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    job.setEmployerId(cursor.getInt(cursor.getColumnIndexOrThrow("employer_id")));
                    job.setCompanyName(cursor.getString(cursor.getColumnIndexOrThrow("company_name")));
                    job.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                    job.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    job.setSalary(cursor.getInt(cursor.getColumnIndexOrThrow("salary")));
                    job.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
                    job.setWorkTime(cursor.getString(cursor.getColumnIndexOrThrow("work_time")));
                    job.setWorkDays(cursor.getString(cursor.getColumnIndexOrThrow("work_days")));
                    job.setRequirements(cursor.getString(cursor.getColumnIndexOrThrow("requirements")));
                    job.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                    job.setViewCount(cursor.getInt(cursor.getColumnIndexOrThrow("view_count")));

                    jobs.add(job);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        recommendedJobAdapter.setJobList(jobs);
    }
}
