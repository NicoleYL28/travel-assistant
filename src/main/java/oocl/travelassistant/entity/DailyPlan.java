package oocl.travelassistant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "daily_plans")
public class DailyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "travel_plan_id")
    private TravelPlan travelPlan;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    private String theme;

    @Column(columnDefinition = "TEXT")
    private String morning;

    @Column(columnDefinition = "TEXT")
    private String afternoon;

    @Column(columnDefinition = "TEXT")
    private String evening;

    @Column(columnDefinition = "TEXT")
    private String breakfast;

    @Column(columnDefinition = "TEXT")
    private String lunch;

    @Column(columnDefinition = "TEXT")
    private String dinner;

    @Column(columnDefinition = "TEXT")
    private String accommodation;

    @Column(name = "transportation_details", columnDefinition = "TEXT")
    private String transportationDetails;

    @Column(name = "transportation_cost", precision = 10, scale = 2)
    private BigDecimal transportationCost;

    @Column(name = "daily_cost", precision = 10, scale = 2)
    private BigDecimal dailyCost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TravelPlan getTravelPlan() {
        return travelPlan;
    }

    public void setTravelPlan(TravelPlan travelPlan) {
        this.travelPlan = travelPlan;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
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

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public String getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(String accommodation) {
        this.accommodation = accommodation;
    }

    public String getTransportationDetails() {
        return transportationDetails;
    }

    public void setTransportationDetails(String transportationDetails) {
        this.transportationDetails = transportationDetails;
    }

    public BigDecimal getTransportationCost() {
        return transportationCost;
    }

    public void setTransportationCost(BigDecimal transportationCost) {
        this.transportationCost = transportationCost;
    }

    public BigDecimal getDailyCost() {
        return dailyCost;
    }

    public void setDailyCost(BigDecimal dailyCost) {
        this.dailyCost = dailyCost;
    }
}