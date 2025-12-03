package com.example.mobile_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile_project.model.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * 시간표 데이터 접근 객체
 * 수업 + 알바 일정 관리, 겹침 체크
 */
public class ScheduleDAO {

    private DatabaseHelper dbHelper;

    public ScheduleDAO(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * 새 일정 추가
     */
    public long createSchedule(int studentId, String title, String type, int dayOfWeek,
                               String startTime, String endTime, String location, String memo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("title", title);
        values.put("type", type);
        values.put("day_of_week", dayOfWeek);
        values.put("start_time", startTime);
        values.put("end_time", endTime);
        values.put("location", location);
        values.put("memo", memo);

        long result = db.insert("schedules", null, values);
        return result;
    }

    /**
     * 일정 수정
     */
    public int updateSchedule(int scheduleId, String title, String type, int dayOfWeek,
                              String startTime, String endTime, String location, String memo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("type", type);
        values.put("day_of_week", dayOfWeek);
        values.put("start_time", startTime);
        values.put("end_time", endTime);
        values.put("location", location);
        values.put("memo", memo);

        return db.update("schedules", values, "id = ?", new String[]{String.valueOf(scheduleId)});
    }

    /**
     * 일정 삭제
     */
    public int deleteSchedule(int scheduleId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("schedules", "id = ?", new String[]{String.valueOf(scheduleId)});
    }

    /**
     * 특정 학생의 모든 일정 조회
     */
    public List<Schedule> getSchedulesByStudentId(int studentId) {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM schedules WHERE student_id = ? ORDER BY day_of_week, start_time";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = cursorToSchedule(cursor);
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return schedules;
    }

    /**
     * 특정 요일의 일정 조회
     */
    public List<Schedule> getSchedulesByDay(int studentId, int dayOfWeek) {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM schedules WHERE student_id = ? AND day_of_week = ? ORDER BY start_time";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), String.valueOf(dayOfWeek)});

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = cursorToSchedule(cursor);
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return schedules;
    }

    /**
     * 특정 유형의 일정 조회 (수업 or 알바)
     */
    public List<Schedule> getSchedulesByType(int studentId, String type) {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM schedules WHERE student_id = ? AND type = ? ORDER BY day_of_week, start_time";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), type});

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = cursorToSchedule(cursor);
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return schedules;
    }

    /**
     * 일정 ID로 조회
     */
    public Schedule getScheduleById(int scheduleId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Schedule schedule = null;

        Cursor cursor = db.query("schedules", null, "id = ?",
                new String[]{String.valueOf(scheduleId)}, null, null, null);

        if (cursor.moveToFirst()) {
            schedule = cursorToSchedule(cursor);
        }
        cursor.close();

        return schedule;
    }

    /**
     * 일정 겹침 체크
     * @return 겹치는 일정이 있으면 true
     */
    public boolean hasConflict(int studentId, int dayOfWeek, String startTime, String endTime, Integer excludeScheduleId) {
        List<Schedule> daySchedules = getSchedulesByDay(studentId, dayOfWeek);

        Schedule newSchedule = new Schedule(0, studentId, "", "", dayOfWeek, startTime, endTime, "", "", "");

        for (Schedule existing : daySchedules) {
            // 수정 시 자기 자신은 제외
            if (excludeScheduleId != null && existing.getId() == excludeScheduleId) {
                continue;
            }

            if (newSchedule.isConflictWith(existing)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 겹치는 일정 목록 반환
     */
    public List<Schedule> getConflictingSchedules(int studentId) {
        List<Schedule> allSchedules = getSchedulesByStudentId(studentId);
        List<Schedule> conflicts = new ArrayList<>();

        for (int i = 0; i < allSchedules.size(); i++) {
            for (int j = i + 1; j < allSchedules.size(); j++) {
                Schedule s1 = allSchedules.get(i);
                Schedule s2 = allSchedules.get(j);

                if (s1.isConflictWith(s2)) {
                    if (!conflicts.contains(s1)) {
                        conflicts.add(s1);
                    }
                    if (!conflicts.contains(s2)) {
                        conflicts.add(s2);
                    }
                }
            }
        }

        return conflicts;
    }

    /**
     * Cursor를 Schedule 객체로 변환
     */
    private Schedule cursorToSchedule(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        int studentId = cursor.getInt(cursor.getColumnIndexOrThrow("student_id"));
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
        int dayOfWeek = cursor.getInt(cursor.getColumnIndexOrThrow("day_of_week"));
        String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
        String endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));
        String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
        String memo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

        return new Schedule(id, studentId, title, type, dayOfWeek, startTime, endTime, location, memo, createdAt);
    }
}