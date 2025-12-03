package com.example.mobile_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile_project.model.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * 지원 내역 데이터 접근 객체
 */
public class ApplicationDAO {

    private DatabaseHelper dbHelper;

    public ApplicationDAO(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * 새 지원 생성
     */
    public long createApplication(int jobPostingId, int studentId, String resume, String coverLetter) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("job_posting_id", jobPostingId);
        values.put("student_id", studentId);
        values.put("resume", resume);
        values.put("cover_letter", coverLetter);
        values.put("status", "pending");

        long result = db.insert("applications", null, values);
        return result;
    }

    /**
     * 지원 상태 업데이트 (고용주용)
     */
    public int updateApplicationStatus(int applicationId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", status);

        return db.update("applications", values, "id = ?", new String[]{String.valueOf(applicationId)});
    }

    /**
     * 지원 내용 수정 (재학생용)
     */
    public int updateApplication(int applicationId, String resume, String coverLetter) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("resume", resume);
        values.put("cover_letter", coverLetter);

        return db.update("applications", values, "id = ?", new String[]{String.valueOf(applicationId)});
    }

    /**
     * 지원 취소/삭제
     */
    public int deleteApplication(int applicationId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("applications", "id = ?", new String[]{String.valueOf(applicationId)});
    }

    /**
     * 특정 학생의 모든 지원 내역 조회
     */
    public List<Application> getApplicationsByStudentId(int studentId) {
        List<Application> applications = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM applications WHERE student_id = ? ORDER BY applied_at DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        if (cursor.moveToFirst()) {
            do {
                Application application = cursorToApplication(cursor);
                applications.add(application);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return applications;
    }

    /**
     * 특정 공고에 대한 모든 지원 조회 (고용주용)
     */
    public List<Application> getApplicationsByJobPostingId(int jobPostingId) {
        List<Application> applications = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM applications WHERE job_posting_id = ? ORDER BY applied_at DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(jobPostingId)});

        if (cursor.moveToFirst()) {
            do {
                Application application = cursorToApplication(cursor);
                applications.add(application);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return applications;
    }

    /**
     * 특정 상태의 지원 조회
     */
    public List<Application> getApplicationsByStatus(int studentId, String status) {
        List<Application> applications = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM applications WHERE student_id = ? AND status = ? ORDER BY applied_at DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), status});

        if (cursor.moveToFirst()) {
            do {
                Application application = cursorToApplication(cursor);
                applications.add(application);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return applications;
    }

    /**
     * 지원 ID로 조회
     */
    public Application getApplicationById(int applicationId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Application application = null;

        Cursor cursor = db.query("applications", null, "id = ?",
                new String[]{String.valueOf(applicationId)}, null, null, null);

        if (cursor.moveToFirst()) {
            application = cursorToApplication(cursor);
        }
        cursor.close();

        return application;
    }

    /**
     * 중복 지원 체크
     * @return 이미 지원했으면 true
     */
    public boolean hasAlreadyApplied(int jobPostingId, int studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM applications WHERE job_posting_id = ? AND student_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(jobPostingId), String.valueOf(studentId)});

        boolean hasApplied = false;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            hasApplied = (count > 0);
        }
        cursor.close();

        return hasApplied;
    }

    /**
     * 지원 통계 조회
     */
    public int getApplicationCountByStatus(int studentId, String status) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM applications WHERE student_id = ? AND status = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), status});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }

    /**
     * Cursor를 Application 객체로 변환
     */
    private Application cursorToApplication(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        int jobPostingId = cursor.getInt(cursor.getColumnIndexOrThrow("job_posting_id"));
        int studentId = cursor.getInt(cursor.getColumnIndexOrThrow("student_id"));
        String resume = cursor.getString(cursor.getColumnIndexOrThrow("resume"));
        String coverLetter = cursor.getString(cursor.getColumnIndexOrThrow("cover_letter"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        String appliedAt = cursor.getString(cursor.getColumnIndexOrThrow("applied_at"));
        String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow("updated_at"));

        return new Application(id, jobPostingId, studentId, resume, coverLetter, status, appliedAt, updatedAt);
    }
}