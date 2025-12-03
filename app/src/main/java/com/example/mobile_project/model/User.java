package com.example.mobile_project.model;

/**
 * 사용자 데이터 모델
 * 재학생과 고용주 모두 포함
 */
public class User {
    private int id;
    private String email;
    private String password;
    private String userType; // "student" or "employer"
    private String name;
    private String phone;

    // 재학생 전용 필드
    private String studentId;
    private String department;
    private int grade;

    // 고용주 전용 필드
    private String businessName;
    private String businessNumber;

    private String createdAt;
    private String updatedAt;

    // Constructor for student
    public User(int id, String email, String password, String name, String phone,
                String studentId, String department, int grade) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userType = "student";
        this.name = name;
        this.phone = phone;
        this.studentId = studentId;
        this.department = department;
        this.grade = grade;
    }

    // Constructor for employer
    public User(int id, String email, String password, String name, String phone,
                String businessName, String businessNumber) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userType = "employer";
        this.name = name;
        this.phone = phone;
        this.businessName = businessName;
        this.businessNumber = businessNumber;
    }

    // Full constructor
    public User(int id, String email, String password, String userType, String name, String phone,
                String studentId, String department, int grade,
                String businessName, String businessNumber,
                String createdAt, String updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.name = name;
        this.phone = phone;
        this.studentId = studentId;
        this.department = department;
        this.grade = grade;
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
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

    // Utility methods
    public boolean isStudent() {
        return "student".equals(userType);
    }

    public boolean isEmployer() {
        return "employer".equals(userType);
    }

    public String getDisplayInfo() {
        if (isStudent()) {
            return department + " · " + grade + "학년";
        } else {
            return businessName;
        }
    }
}