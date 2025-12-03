package com.example.mobile_project.model;

/**
 * 추천 장소 모델
 * 주변 음식점/카페 정보
 */
public class Place {
    private int id;
    private String name;
    private String category; // "restaurant", "cafe"
    private String address;
    private double latitude;
    private double longitude;
    private float rating; // 평점 (0.0 ~ 5.0)
    private int distance; // 거리 (미터)

    public Place() {
    }

    public Place(int id, String name, String category, String address,
                 double latitude, double longitude, float rating, int distance) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.distance = distance;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * 거리를 읽기 쉬운 형식으로 변환
     * @return "100m" 또는 "1.2km"
     */
    public String getFormattedDistance() {
        if (distance < 1000) {
            return distance + "m";
        } else {
            return String.format("%.1fkm", distance / 1000.0);
        }
    }

    /**
     * 카테고리를 한글로 변환
     * @return "음식점" 또는 "카페"
     */
    public String getCategoryInKorean() {
        switch (category) {
            case "restaurant":
                return "음식점";
            case "cafe":
                return "카페";
            default:
                return category;
        }
    }
}