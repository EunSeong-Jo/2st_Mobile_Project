package com.example.mobile_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile_project.model.JobPosting;

import java.util.ArrayList;
import java.util.List;

/**
 * JobPosting 테이블 Data Access Object
 * 채용공고 CRUD 작업
 */
public class JobPostingDAO {

    private DatabaseHelper dbHelper;

    public JobPostingDAO(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * 채용공고 작성 (고용주)
     */
    public long createJobPosting(int employerId, String companyName, String title, String description,
                                 int salary, String location, String workTime, String workDays, String requirements) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EMPLOYER_ID, employerId);
        values.put(DatabaseHelper.COLUMN_COMPANY_NAME, companyName);
        values.put(DatabaseHelper.COLUMN_TITLE, title);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_SALARY, salary);
        values.put(DatabaseHelper.COLUMN_LOCATION, location);
        values.put(DatabaseHelper.COLUMN_WORK_TIME, workTime);
        values.put(DatabaseHelper.COLUMN_WORK_DAYS, workDays);
        values.put(DatabaseHelper.COLUMN_REQUIREMENTS, requirements);
        values.put(DatabaseHelper.COLUMN_STATUS, "active");

        return db.insert(DatabaseHelper.TABLE_JOB_POSTINGS, null, values);
    }

    /**
     * 모든 활성 채용공고 조회
     */
    public List<JobPosting> getAllActiveJobPostings() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<JobPosting> jobPostings = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_STATUS + " = ?";
        String[] selectionArgs = {"active"};
        String orderBy = DatabaseHelper.COLUMN_CREATED_AT + " DESC";

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_JOB_POSTINGS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                jobPostings.add(getJobPostingFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return jobPostings;
    }

    /**
     * ID로 채용공고 조회
     */
    public JobPosting getJobPostingById(int jobPostingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(jobPostingId)};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_JOB_POSTINGS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        JobPosting jobPosting = null;
        if (cursor != null && cursor.moveToFirst()) {
            jobPosting = getJobPostingFromCursor(cursor);
            cursor.close();
        }

        return jobPosting;
    }

    /**
     * 고용주가 작성한 공고 목록 조회
     */
    public List<JobPosting> getJobPostingsByEmployer(int employerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<JobPosting> jobPostings = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_EMPLOYER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(employerId)};
        String orderBy = DatabaseHelper.COLUMN_CREATED_AT + " DESC";

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_JOB_POSTINGS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                jobPostings.add(getJobPostingFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return jobPostings;
    }

    /**
     * 채용공고 수정
     */
    public int updateJobPosting(int jobPostingId, String title, String description,
                                int salary, String location, String workTime, String workDays, String requirements) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, title);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_SALARY, salary);
        values.put(DatabaseHelper.COLUMN_LOCATION, location);
        values.put(DatabaseHelper.COLUMN_WORK_TIME, workTime);
        values.put(DatabaseHelper.COLUMN_WORK_DAYS, workDays);
        values.put(DatabaseHelper.COLUMN_REQUIREMENTS, requirements);

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(jobPostingId)};

        return db.update(DatabaseHelper.TABLE_JOB_POSTINGS, values, whereClause, whereArgs);
    }

    /**
     * 채용공고 상태 변경 (마감)
     */
    public int closeJobPosting(int jobPostingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_STATUS, "closed");

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(jobPostingId)};

        return db.update(DatabaseHelper.TABLE_JOB_POSTINGS, values, whereClause, whereArgs);
    }

    /**
     * 채용공고 삭제
     */
    public int deleteJobPosting(int jobPostingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(jobPostingId)};

        return db.delete(DatabaseHelper.TABLE_JOB_POSTINGS, whereClause, whereArgs);
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount(int jobPostingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "UPDATE " + DatabaseHelper.TABLE_JOB_POSTINGS +
                " SET " + DatabaseHelper.COLUMN_VIEW_COUNT + " = " + DatabaseHelper.COLUMN_VIEW_COUNT + " + 1" +
                " WHERE " + DatabaseHelper.COLUMN_ID + " = ?";

        db.execSQL(sql, new Object[]{jobPostingId});
    }

    /**
     * 검색 기능 - 제목, 회사명, 위치로 검색
     */
    public List<JobPosting> searchJobPostings(String keyword) {
        List<JobPosting> jobPostings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = "(" + DatabaseHelper.COLUMN_TITLE + " LIKE ? OR " +
                DatabaseHelper.COLUMN_COMPANY_NAME + " LIKE ? OR " +
                DatabaseHelper.COLUMN_LOCATION + " LIKE ?) AND " +
                DatabaseHelper.COLUMN_STATUS + " = ?";

        String searchPattern = "%" + keyword + "%";
        String[] selectionArgs = {searchPattern, searchPattern, searchPattern, "active"};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_JOB_POSTINGS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                jobPostings.add(getJobPostingFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return jobPostings;
    }

    /**
     * 필터링 기능 - 급여, 위치, 근무시간으로 필터
     */
    public List<JobPosting> filterJobPostings(Integer minSalary, String location, String workTime) {
        List<JobPosting> jobPostings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder selection = new StringBuilder(DatabaseHelper.COLUMN_STATUS + " = ?");
        List<String> selectionArgsList = new ArrayList<>();
        selectionArgsList.add("active");

        // 최소 급여 필터
        if (minSalary != null && minSalary > 0) {
            selection.append(" AND ").append(DatabaseHelper.COLUMN_SALARY).append(" >= ?");
            selectionArgsList.add(String.valueOf(minSalary));
        }

        // 위치 필터
        if (location != null && !location.isEmpty()) {
            selection.append(" AND ").append(DatabaseHelper.COLUMN_LOCATION).append(" LIKE ?");
            selectionArgsList.add("%" + location + "%");
        }

        // 근무시간 필터
        if (workTime != null && !workTime.isEmpty()) {
            selection.append(" AND ").append(DatabaseHelper.COLUMN_WORK_TIME).append(" LIKE ?");
            selectionArgsList.add("%" + workTime + "%");
        }

        String[] selectionArgs = selectionArgsList.toArray(new String[0]);

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_JOB_POSTINGS,
                null,
                selection.toString(),
                selectionArgs,
                null,
                null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                jobPostings.add(getJobPostingFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return jobPostings;
    }

    /**
     * 급여 범위로 검색
     */
    public List<JobPosting> searchBySalaryRange(int minSalary, int maxSalary) {
        List<JobPosting> jobPostings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_SALARY + " >= ? AND " +
                DatabaseHelper.COLUMN_SALARY + " <= ? AND " +
                DatabaseHelper.COLUMN_STATUS + " = ?";

        String[] selectionArgs = {String.valueOf(minSalary), String.valueOf(maxSalary), "active"};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_JOB_POSTINGS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                DatabaseHelper.COLUMN_SALARY + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                jobPostings.add(getJobPostingFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return jobPostings;
    }

    /**
     * 정렬 기능 - 최신순, 급여높은순, 조회수순
     */
    public List<JobPosting> getJobPostingsSorted(String sortBy) {
        List<JobPosting> jobPostings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String orderBy;
        switch (sortBy) {
            case "salary_desc":
                orderBy = DatabaseHelper.COLUMN_SALARY + " DESC";
                break;
            case "view_count_desc":
                orderBy = DatabaseHelper.COLUMN_VIEW_COUNT + " DESC";
                break;
            case "created_at_desc":
            default:
                orderBy = DatabaseHelper.COLUMN_CREATED_AT + " DESC";
                break;
        }

        String selection = DatabaseHelper.COLUMN_STATUS + " = ?";
        String[] selectionArgs = {"active"};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_JOB_POSTINGS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );

        if (cursor.moveToFirst()) {
            do {
                jobPostings.add(getJobPostingFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return jobPostings;
    }

    /**
     * Cursor에서 JobPosting 객체 생성
     */
    private JobPosting getJobPostingFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        int employerId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMPLOYER_ID));
        String companyName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMPANY_NAME));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
        int salary = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALARY));
        String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
        String workTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORK_TIME));
        String workDays = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORK_DAYS));
        String requirements = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REQUIREMENTS));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));
        int viewCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VIEW_COUNT));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT));
        String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT));

        return new JobPosting(id, employerId, companyName, title, description, salary, location,
                workTime, workDays, requirements, status, viewCount, createdAt, updatedAt);
    }
}