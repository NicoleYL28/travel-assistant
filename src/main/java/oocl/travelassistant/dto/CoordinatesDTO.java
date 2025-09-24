package oocl.travelassistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoordinatesDTO {
    @JsonProperty("longitude")
    private Double longitude; // 经度，精确到6位小数
    
    @JsonProperty("latitude")
    private Double latitude;  // 纬度，精确到6位小数

    // Constructors
    public CoordinatesDTO() {}

    public CoordinatesDTO(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters and Setters
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    @Override
    public String toString() {
        return String.format("CoordinatesDTO{longitude=%.6f, latitude=%.6f}", longitude, latitude);
    }
}