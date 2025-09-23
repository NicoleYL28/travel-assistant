package oocl.travelassistant.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "travel_plans")
public class TravelPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(nullable = false)
    private Integer duration;

    @Column(name = "total_budget", precision = 10, scale = 2)
    private BigDecimal totalBudget;

    @Column(name = "accommodation_budget", precision = 10, scale = 2)
    private BigDecimal accommodationBudget;

    @Column(name = "food_budget", precision = 10, scale = 2)
    private BigDecimal foodBudget;

    @Column(name = "transportation_budget", precision = 10, scale = 2)
    private BigDecimal transportationBudget;

    @Column(name = "activities_budget", precision = 10, scale = 2)
    private BigDecimal activitiesBudget;

    @Column(name = "shopping_budget", precision = 10, scale = 2)
    private BigDecimal shoppingBudget;

    @Column(name = "other_budget", precision = 10, scale = 2)
    private BigDecimal otherBudget;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyPlan> dailyPlans;

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TravelTip> tips;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public BigDecimal getAccommodationBudget() {
        return accommodationBudget;
    }

    public void setAccommodationBudget(BigDecimal accommodationBudget) {
        this.accommodationBudget = accommodationBudget;
    }

    public BigDecimal getFoodBudget() {
        return foodBudget;
    }

    public void setFoodBudget(BigDecimal foodBudget) {
        this.foodBudget = foodBudget;
    }

    public BigDecimal getTransportationBudget() {
        return transportationBudget;
    }

    public void setTransportationBudget(BigDecimal transportationBudget) {
        this.transportationBudget = transportationBudget;
    }

    public BigDecimal getActivitiesBudget() {
        return activitiesBudget;
    }

    public void setActivitiesBudget(BigDecimal activitiesBudget) {
        this.activitiesBudget = activitiesBudget;
    }

    public BigDecimal getShoppingBudget() {
        return shoppingBudget;
    }

    public void setShoppingBudget(BigDecimal shoppingBudget) {
        this.shoppingBudget = shoppingBudget;
    }

    public BigDecimal getOtherBudget() {
        return otherBudget;
    }

    public void setOtherBudget(BigDecimal otherBudget) {
        this.otherBudget = otherBudget;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<DailyPlan> getDailyPlans() {
        return dailyPlans;
    }

    public void setDailyPlans(List<DailyPlan> dailyPlans) {
        this.dailyPlans = dailyPlans;
    }

    public List<TravelTip> getTips() {
        return tips;
    }

    public void setTips(List<TravelTip> tips) {
        this.tips = tips;
    }
}