package com.example.mobile_project.model;

/**
 * 리뷰 데이터 모델
 */
public class Review {
    private int id;
    private int jobPostingId;
    private int studentId;
    private float rating; // 1.0 ~ 5.0
    private String comment;
    private String createdAt;

    // Constructor
    public Review(int id, int jobPostingId, int studentId, float rating, String comment, String createdAt) {
        this.id = id;
        this.jobPostingId = jobPostingId;
        this.studentId = studentId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobPostingId() {
        return jobPostingId;
    }

    public void setJobPostingId(int jobPostingId) {
        this.jobPostingId = jobPostingId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Utility methods
    public String getRatingDisplay() {
        return String.format("%.1f", rating);
    }

    public String getRatingStars() {
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;

        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < fullStars; i++) {
            stars.append("⭐");
        }
        if (hasHalfStar) {
            stars.append("⭐");
        }

        return stars.toString();
    }
}