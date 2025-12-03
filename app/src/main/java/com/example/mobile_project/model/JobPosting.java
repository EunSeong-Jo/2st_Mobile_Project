package com.example.mobile_project.model;

/**
 * 채용 공고 데이터 모델
 */
public class JobPosting {
    private int id;
    private int employerId;
    private String companyName;
    private String title;
    private String description;
    private int salary;
    private String location;
    private String workTime;
    private String workDays;
    private String requirements;
    private String status; // "active" or "closed"
    private int viewCount;
    private String createdAt;
    private String updatedAt;

    // UI 전용 필드 (DB에 없음)
    private float rating;
    private boolean isBookmarked;

    // Default constructor
    public JobPosting() {
        this.rating = 0.0f;
        this.isBookmarked = false;
    }

    // Full constructor (for database)
    public JobPosting(int id, int employerId, String companyName, String title, String description,
                      int salary, String location, String workTime, String workDays, String requirements,
                      String status, int viewCount, String createdAt, String updatedAt) {
        this.id = id;
        this.employerId = employerId;
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.salary = salary;
        this.location = location;
        this.workTime = workTime;
        this.workDays = workDays;
        this.requirements = requirements;
        this.status = status;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.rating = 0.0f;
        this.isBookmarked = false;
    }

    // Simple constructor (for UI)
    public JobPosting(int id, String companyName, String title, int salary,
                      String location, String workTime, float rating, boolean isBookmarked) {
        this.id = id;
        this.companyName = companyName;
        this.title = title;
        this.salary = salary;
        this.location = location;
        this.workTime = workTime;
        this.rating = rating;
        this.isBookmarked = isBookmarked;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployerId() {
        return employerId;
    }

    public void setEmployerId(int employerId) {
        this.employerId = employerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getWorkDays() {
        return workDays;
    }

    public void setWorkDays(String workDays) {
        this.workDays = workDays;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    // Utility methods
    public boolean isActive() {
        return "active".equals(status);
    }

    public boolean isClosed() {
        return "closed".equals(status);
    }

    public String getSalaryDisplay() {
        return "시급 " + String.format("%,d", salary) + "원";
    }
}