package com.example.mobile_project.model;

/**
 * 학교 공지사항 모델
 */
public class SchoolNotice {
    private String title;      // 제목
    private String url;        // 링크
    private String date;       // 작성일
    private String author;     // 작성자
    private boolean isNew;     // 새 글 여부

    public SchoolNotice(String title, String url, String date, String author, boolean isNew) {
        this.title = title;
        this.url = url;
        this.date = date;
        this.author = author;
        this.isNew = isNew;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
