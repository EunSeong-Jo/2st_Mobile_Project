package com.example.mobile_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile_project.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * 리뷰 데이터 접근 객체
 */
public class ReviewDAO {

    private DatabaseHelper dbHelper;

    public ReviewDAO(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * 새 리뷰 작성
     */
    public long createReview(int jobPostingId, int studentId, float rating, String comment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("job_posting_id", jobPostingId);
        values.put("student_id", studentId);
        values.put("rating", rating);
        values.put("comment", comment);

        long result = db.insert("reviews", null, values);
        return result;
    }

    /**
     * 리뷰 수정
     */
    public int updateReview(int reviewId, float rating, String comment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("rating", rating);
        values.put("comment", comment);

        return db.update("reviews", values, "id = ?", new String[]{String.valueOf(reviewId)});
    }

    /**
     * 리뷰 삭제
     */
    public int deleteReview(int reviewId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("reviews", "id = ?", new String[]{String.valueOf(reviewId)});
    }

    /**
     * 특정 공고의 모든 리뷰 조회
     */
    public List<Review> getReviewsByJobPostingId(int jobPostingId) {
        List<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM reviews WHERE job_posting_id = ? ORDER BY created_at DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(jobPostingId)});

        if (cursor.moveToFirst()) {
            do {
                Review review = cursorToReview(cursor);
                reviews.add(review);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return reviews;
    }

    /**
     * 특정 학생의 모든 리뷰 조회
     */
    public List<Review> getReviewsByStudentId(int studentId) {
        List<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM reviews WHERE student_id = ? ORDER BY created_at DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        if (cursor.moveToFirst()) {
            do {
                Review review = cursorToReview(cursor);
                reviews.add(review);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return reviews;
    }

    /**
     * 리뷰 ID로 조회
     */
    public Review getReviewById(int reviewId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Review review = null;

        Cursor cursor = db.query("reviews", null, "id = ?",
                new String[]{String.valueOf(reviewId)}, null, null, null);

        if (cursor.moveToFirst()) {
            review = cursorToReview(cursor);
        }
        cursor.close();

        return review;
    }

    /**
     * 특정 학생이 특정 공고에 리뷰를 작성했는지 확인
     */
    public boolean hasReviewed(int jobPostingId, int studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM reviews WHERE job_posting_id = ? AND student_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(jobPostingId), String.valueOf(studentId)});

        boolean hasReviewed = false;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            hasReviewed = (count > 0);
        }
        cursor.close();

        return hasReviewed;
    }

    /**
     * 특정 공고의 평균 평점 계산
     */
    public float getAverageRating(int jobPostingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT AVG(rating) FROM reviews WHERE job_posting_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(jobPostingId)});

        float avgRating = 0.0f;
        if (cursor.moveToFirst()) {
            avgRating = cursor.getFloat(0);
        }
        cursor.close();

        return avgRating;
    }

    /**
     * 특정 공고의 리뷰 개수
     */
    public int getReviewCount(int jobPostingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM reviews WHERE job_posting_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(jobPostingId)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }

    /**
     * Cursor를 Review 객체로 변환
     */
    private Review cursorToReview(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        int jobPostingId = cursor.getInt(cursor.getColumnIndexOrThrow("job_posting_id"));
        int studentId = cursor.getInt(cursor.getColumnIndexOrThrow("student_id"));
        float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
        String comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

        return new Review(id, jobPostingId, studentId, rating, comment, createdAt);
    }
}