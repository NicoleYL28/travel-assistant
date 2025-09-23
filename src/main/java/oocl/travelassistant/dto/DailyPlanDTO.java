package oocl.travelassistant.dto;

import java.math.BigDecimal;

public class DailyPlanDTO {
    private Integer day;
    private String theme;
    private String morning;
    private String afternoon;
    private String evening;
    private MealsDTO meals;
    private String accommodation;
    private TransportationDTO transportation;
    private BigDecimal dailyCost;

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getMorning() {
        return morning;
    }

    public void setMorning(String morning) {
        this.morning = morning;
    }

    public String getAfternoon() {
        return afternoon;
    }

    public void setAfternoon(String afternoon) {
        this.afternoon = afternoon;
    }

    public String getEvening() {
        return evening;
    }

    public void setEvening(String evening) {
        this.evening = evening;
    }

    public MealsDTO getMeals() {
        return meals;
    }

    public void setMeals(MealsDTO meals) {
        this.meals = meals;
    }

    public String getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(String accommodation) {
        this.accommodation = accommodation;
    }

    public TransportationDTO getTransportation() {
        return transportation;
    }

    public void setTransportation(TransportationDTO transportation) {
        this.transportation = transportation;
    }

    public BigDecimal getDailyCost() {
        return dailyCost;
    }

    public void setDailyCost(BigDecimal dailyCost) {
        this.dailyCost = dailyCost;
    }
}