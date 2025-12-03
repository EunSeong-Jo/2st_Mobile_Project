package com.example.mobile_project.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.database.ScheduleDAO;
import com.example.mobile_project.model.Schedule;
import com.example.mobile_project.util.PreferenceManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

/**
 * 일정 추가/수정 화면
 */
public class ScheduleFormActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etTitle, etLocation, etMemo;
    private AutoCompleteTextView actvDayOfWeek;
    private MaterialButton btnStartTime, btnEndTime, btnSubmit;
    private RadioGroup rgScheduleType;
    private RadioButton rbClass, rbWork;

    private ScheduleDAO scheduleDAO;
    private PreferenceManager preferenceManager;
    private int scheduleId = -1; // -1이면 새 일정, 아니면 수정
    private boolean isEditMode = false;
    private int currentUserId;

    private int selectedDayOfWeek = 2; // 월요일 기본값 (Calendar.MONDAY)
    private String selectedStartTime = "09:00";
    private String selectedEndTime = "10:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_form);

        scheduleDAO = new ScheduleDAO(this);
        preferenceManager = new PreferenceManager(this);

        // 로그인된 사용자 ID 가져오기
        currentUserId = preferenceManager.getUserId();

        // Intent에서 일정 ID 가져오기 (수정 모드인 경우)
        scheduleId = getIntent().getIntExtra("schedule_id", -1);
        isEditMode = (scheduleId != -1);

        initViews();
        setupToolbar();
        setupDayOfWeekSpinner();
        setupClickListeners();

        if (isEditMode) {
            loadSchedule();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etTitle = findViewById(R.id.et_title);
        actvDayOfWeek = findViewById(R.id.actv_day_of_week);
        btnStartTime = findViewById(R.id.btn_start_time);
        btnEndTime = findViewById(R.id.btn_end_time);
        etLocation = findViewById(R.id.et_location);
        etMemo = findViewById(R.id.et_memo);
        rgScheduleType = findViewById(R.id.rg_schedule_type);
        rbClass = findViewById(R.id.rb_class);
        rbWork = findViewById(R.id.rb_work);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void setupToolbar() {
        if (isEditMode) {
            toolbar.setTitle("일정 수정");
            btnSubmit.setText("수정하기");
        } else {
            toolbar.setTitle("일정 추가");
            btnSubmit.setText("추가하기");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDayOfWeekSpinner() {
        String[] days = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, days);
        actvDayOfWeek.setAdapter(adapter);
        actvDayOfWeek.setText("월요일", false);
        selectedDayOfWeek = 2; // 월요일 기본값 (Calendar.MONDAY)

        actvDayOfWeek.setOnItemClickListener((parent, view, position, id) -> {
            // position: 0=일요일, 1=월요일, ..., 6=토요일
            // Calendar: 1=일요일, 2=월요일, ..., 7=토요일
            selectedDayOfWeek = position + 1;
        });
    }

    private void setupClickListeners() {
        // 시작 시간 선택
        btnStartTime.setOnClickListener(v -> showTimePicker(true));

        // 종료 시간 선택
        btnEndTime.setOnClickListener(v -> showTimePicker(false));

        // 제출 버튼
        btnSubmit.setOnClickListener(v -> {
            if (isEditMode) {
                updateSchedule();
            } else {
                createSchedule();
            }
        });
    }

    private void showTimePicker(boolean isStartTime) {
        String currentTime = isStartTime ? selectedStartTime : selectedEndTime;
        String[] parts = currentTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    if (isStartTime) {
                        selectedStartTime = time;
                        btnStartTime.setText(time);
                    } else {
                        selectedEndTime = time;
                        btnEndTime.setText(time);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void loadSchedule() {
        Schedule schedule = scheduleDAO.getScheduleById(scheduleId);

        if (schedule == null) {
            Toast.makeText(this, "일정을 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 폼에 데이터 채우기
        etTitle.setText(schedule.getTitle());

        // 요일 설정 (Calendar 값: 1=일요일, 2=월요일, ..., 7=토요일)
        selectedDayOfWeek = schedule.getDayOfWeek();
        String[] days = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        // Calendar 값을 배열 인덱스로 변환 (1-7 → 0-6)
        int dayIndex = selectedDayOfWeek - 1;
        actvDayOfWeek.setText(days[dayIndex], false);

        // 시간 설정
        selectedStartTime = schedule.getStartTime();
        selectedEndTime = schedule.getEndTime();
        btnStartTime.setText(selectedStartTime);
        btnEndTime.setText(selectedEndTime);

        etLocation.setText(schedule.getLocation());
        etMemo.setText(schedule.getMemo());

        // 일정 유형 설정
        if (schedule.isClass()) {
            rbClass.setChecked(true);
        } else {
            rbWork.setChecked(true);
        }
    }

    private void createSchedule() {
        // 입력값 가져오기
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String memo = etMemo.getText().toString().trim();
        String type = rbClass.isChecked() ? "class" : "work";

        // 입력 검증
        if (!validateInput(title)) {
            return;
        }

        // 시간 검증
        if (!validateTime()) {
            Toast.makeText(this, "종료 시간은 시작 시간보다 늦어야 합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // 겹침 체크
        if (scheduleDAO.hasConflict(currentUserId, selectedDayOfWeek, selectedStartTime, selectedEndTime, null)) {
            Toast.makeText(this, "⚠️ 같은 시간대에 다른 일정이 있습니다", Toast.LENGTH_LONG).show();
            // 경고만 하고 계속 진행 (사용자가 원하면 추가 가능)
        }

        // 일정 추가
        long result = scheduleDAO.createSchedule(currentUserId, title, type, selectedDayOfWeek,
                selectedStartTime, selectedEndTime, location, memo);

        if (result > 0) {
            Toast.makeText(this, "일정이 추가되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "일정 추가에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSchedule() {
        // 입력값 가져오기
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String memo = etMemo.getText().toString().trim();
        String type = rbClass.isChecked() ? "class" : "work";

        // 입력 검증
        if (!validateInput(title)) {
            return;
        }

        // 시간 검증
        if (!validateTime()) {
            Toast.makeText(this, "종료 시간은 시작 시간보다 늦어야 합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // 겹침 체크 (자기 자신 제외)
        if (scheduleDAO.hasConflict(currentUserId, selectedDayOfWeek, selectedStartTime, selectedEndTime, scheduleId)) {
            Toast.makeText(this, "⚠️ 같은 시간대에 다른 일정이 있습니다", Toast.LENGTH_LONG).show();
        }

        // 일정 수정
        int result = scheduleDAO.updateSchedule(scheduleId, title, type, selectedDayOfWeek,
                selectedStartTime, selectedEndTime, location, memo);

        if (result > 0) {
            Toast.makeText(this, "일정이 수정되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "일정 수정에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String title) {
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("제목을 입력해주세요");
            etTitle.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateTime() {
        // HH:mm 형식을 분으로 변환하여 비교
        String[] startParts = selectedStartTime.split(":");
        String[] endParts = selectedEndTime.split(":");

        int startMinutes = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
        int endMinutes = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);

        return endMinutes > startMinutes;
    }
}