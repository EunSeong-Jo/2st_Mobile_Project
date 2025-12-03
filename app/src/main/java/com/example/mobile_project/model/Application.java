package com.example.mobile_project.model;

/**
 * 지원 내역 데이터 모델
 */
public class Application {
    private int id;
    private int jobPostingId;
    private int studentId;
    private String resume;
    private String coverLetter;
    private String status; // "pending", "accepted", "rejected"
    private String appliedAt;
    private String updatedAt;

    // Constructor
    public Application(int id, int jobPostingId, int studentId, String resume, String coverLetter,
                       String status, String appliedAt, String updatedAt) {
        this.id = id;
        this.jobPostingId = jobPostingId;
        this.studentId = studentId;
        this.resume = resume;
        this.coverLetter = coverLetter;
        this.status = status;
        this.appliedAt = appliedAt;
        this.updatedAt = updatedAt;
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

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(String appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public boolean isPending() {
        return "pending".equals(status);
    }

    public boolean isAccepted() {
        return "accepted".equals(status);
    }

    public boolean isRejected() {
        return "rejected".equals(status);
    }

    public String getStatusDisplay() {
        switch (status) {
            case "pending":
                return "대기 중";
            case "accepted":
                return "합격";
            case "rejected":
                return "불합격";
            default:
                return "알 수 없음";
        }
    }
}