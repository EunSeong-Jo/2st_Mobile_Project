package com.example.mobile_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.activity.ScheduleFormActivity;
import com.example.mobile_project.adapter.ScheduleAdapter;
import com.example.mobile_project.database.ScheduleDAO;
import com.example.mobile_project.model.Schedule;
import com.example.mobile_project.util.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 시간표 Fragment (월별 달력 기반)
 * 달력에서 날짜를 선택하면 해당 요일의 시간표 표시
 */
public class ScheduleFragment extends Fragment {

    private CalendarView calendarView;
    private TextView tvSelectedDate, tvScheduleCount, tvConflictWarning;
    private MaterialButton btnAddSchedule;
    private RecyclerView rvScheduleList;
    private LinearLayout layoutEmpty;

    private ScheduleDAO scheduleDAO;
    private ScheduleAdapter scheduleAdapter;
    private PreferenceManager preferenceManager;

    private Calendar selectedCalendar;
    private int selectedDayOfWeek; // 1=일요일, 2=월요일, ... 7=토요일

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        scheduleDAO = new ScheduleDAO(requireContext());
        preferenceManager = new PreferenceManager(requireContext());
        selectedCalendar = Calendar.getInstance();

        initViews(view);
        setupCalendar();
        setupRecyclerView();
        setupClickListeners();

        // 오늘 날짜로 초기화
        updateSelectedDate(System.currentTimeMillis());
        loadSchedulesForSelectedDay();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 화면으로 돌아왔을 때 선택된 날짜의 일정 새로고침
        loadSchedulesForSelectedDay();
    }

    private void initViews(View view) {
        calendarView = view.findViewById(R.id.calendar_view);
        tvSelectedDate = view.findViewById(R.id.tv_selected_date);
        tvScheduleCount = view.findViewById(R.id.tv_schedule_count);
        tvConflictWarning = view.findViewById(R.id.tv_conflict_warning);
        btnAddSchedule = view.findViewById(R.id.btn_add_schedule);
        rvScheduleList = view.findViewById(R.id.rv_schedule_list);
        layoutEmpty = view.findViewById(R.id.layout_empty);
    }

    private void setupCalendar() {
        // 달력 날짜 선택 이벤트
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedCalendar.set(year, month, dayOfMonth);
            updateSelectedDate(selectedCalendar.getTimeInMillis());
            loadSchedulesForSelectedDay();
        });
    }

    private void setupRecyclerView() {
        rvScheduleList.setLayoutManager(new LinearLayoutManager(getContext()));
        scheduleAdapter = new ScheduleAdapter();
        rvScheduleList.setAdapter(scheduleAdapter);

        // 일정 클릭 이벤트 (수정)
        scheduleAdapter.setOnItemClickListener((schedule, position) -> {
            Intent intent = new Intent(requireContext(), ScheduleFormActivity.class);
            intent.putExtra("schedule_id", schedule.getId());
            startActivity(intent);
        });

        // 메뉴 클릭 이벤트
        scheduleAdapter.setOnMenuClickListener((schedule, position, anchorView) -> {
            showScheduleMenu(schedule, position, anchorView);
        });
    }

    private void setupClickListeners() {
        // 일정 추가
        btnAddSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ScheduleFormActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 선택된 날짜 정보 업데이트
     */
    private void updateSelectedDate(long timeInMillis) {
        selectedCalendar.setTimeInMillis(timeInMillis);

        // 요일 계산 (1=일요일, 2=월요일, ... 7=토요일)
        selectedDayOfWeek = selectedCalendar.get(Calendar.DAY_OF_WEEK);

        // 날짜 포맷: "2025년 1월 11일 (토)"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 M월 d일", Locale.KOREAN);
        String dateString = dateFormat.format(selectedCalendar.getTime());

        // 요일 이름 가져오기
        String dayOfWeekString = getDayOfWeekString(selectedDayOfWeek);

        tvSelectedDate.setText(dateString + " (" + dayOfWeekString + ")");
    }

    /**
     * 요일 숫자를 한글 문자열로 변환
     */
    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "일";
            case Calendar.MONDAY:
                return "월";
            case Calendar.TUESDAY:
                return "화";
            case Calendar.WEDNESDAY:
                return "수";
            case Calendar.THURSDAY:
                return "목";
            case Calendar.FRIDAY:
                return "금";
            case Calendar.SATURDAY:
                return "토";
            default:
                return "";
        }
    }

    /**
     * 선택된 날짜의 요일에 해당하는 일정 로드
     */
    private void loadSchedulesForSelectedDay() {
        int userId = preferenceManager.getUserId();

        // 해당 요일의 모든 일정 가져오기
        List<Schedule> allSchedules = scheduleDAO.getSchedulesByDay(userId, selectedDayOfWeek);

        // 시간순 정렬
        allSchedules.sort((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));

        // 일정 개수 표시
        tvScheduleCount.setText("총 " + allSchedules.size() + "개");

        if (allSchedules.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            scheduleAdapter.setScheduleList(allSchedules);

            // 일정 겹침 체크
            checkConflicts(allSchedules);
        }
    }

    /**
     * 일정 겹침 체크
     */
    private void checkConflicts(List<Schedule> schedules) {
        boolean hasConflict = false;

        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                if (schedules.get(i).isConflictWith(schedules.get(j))) {
                    hasConflict = true;
                    break;
                }
            }
            if (hasConflict) break;
        }

        tvConflictWarning.setVisibility(hasConflict ? View.VISIBLE : View.GONE);
    }

    /**
     * 일정 메뉴 표시 (수정/삭제)
     */
    private void showScheduleMenu(Schedule schedule, int position, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
        popupMenu.inflate(R.menu.menu_schedule_item);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                // 수정
                Intent intent = new Intent(requireContext(), ScheduleFormActivity.class);
                intent.putExtra("schedule_id", schedule.getId());
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_delete) {
                // 삭제 확인 다이얼로그
                showDeleteDialog(schedule);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    /**
     * 삭제 확인 다이얼로그
     */
    private void showDeleteDialog(Schedule schedule) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("일정 삭제")
                .setMessage("'" + schedule.getTitle() + "' 일정을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    int result = scheduleDAO.deleteSchedule(schedule.getId());
                    if (result > 0) {
                        Toast.makeText(requireContext(), "일정이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        loadSchedulesForSelectedDay(); // 목록 새로고침
                    } else {
                        Toast.makeText(requireContext(), "삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    /**
     * 빈 상태 표시
     */
    private void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            rvScheduleList.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvScheduleList.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}