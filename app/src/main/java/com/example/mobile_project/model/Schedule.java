package com.example.mobile_project.model;

/**
 * 시간표 데이터 모델 (재학생 전용)
 * 수업 + 알바 일정 통합
 */
public class Schedule {
    private int id;
    private int studentId;
    private String title;
    private String type; // "class" or "work"
    private int dayOfWeek; // 0=일, 1=월, 2=화, ..., 6=토
    private String startTime; // HH:mm 형식
    private String endTime; // HH:mm 형식
    private String location;
    private String memo;
    private String createdAt;

    // Constructor
    public Schedule(int id, int studentId, String title, String type, int dayOfWeek,
                    String startTime, String endTime, String location, String memo, String createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.title = title;
        this.type = type;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.memo = memo;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Utility methods
    public boolean isClass() {
        return "class".equals(type);
    }

    public boolean isWork() {
        return "work".equals(type);
    }

    public String getDayOfWeekDisplay() {
        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        if (dayOfWeek >= 0 && dayOfWeek < days.length) {
            return days[dayOfWeek];
        }
        return "";
    }

    public String getTimeDisplay() {
        return startTime + " ~ " + endTime;
    }

    public int getTypeColor() {
        // 수업: DMU Blue, 알바: Alba Orange
        return isClass() ? 0xFF1E88E5 : 0xFFFF9800;
    }

    /**
     * 두 일정이 겹치는지 확인
     */
    public boolean isConflictWith(Schedule other) {
        // 다른 요일이면 겹치지 않음
        if (this.dayOfWeek != other.dayOfWeek) {
            return false;
        }

        // 시간 비교 (HH:mm 형식)
        int thisStart = timeToMinutes(this.startTime);
        int thisEnd = timeToMinutes(this.endTime);
        int otherStart = timeToMinutes(other.startTime);
        int otherEnd = timeToMinutes(other.endTime);

        // 겹침 체크: (start1 < end2) AND (end1 > start2)
        return (thisStart < otherEnd) && (thisEnd > otherStart);
    }

    /**
     * HH:mm 형식의 시간을 분(minute) 단위로 변환
     */
    private int timeToMinutes(String time) {
        if (time == null || !time.contains(":")) {
            return 0;
        }
        String[] parts = time.split(":");
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}