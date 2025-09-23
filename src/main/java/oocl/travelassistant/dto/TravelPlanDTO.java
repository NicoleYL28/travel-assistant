package oocl.travelassistant.dto;

import java.math.BigDecimal;
import java.util.List;

public class TravelPlanDTO {
    private Long id;
    private String title;
    private String overview;
    private Integer duration;
    private BigDecimal totalBudget;
    private List<DailyPlanDTO> dailyPlan;
    private BudgetBreakdownDTO budgetBreakdown;
    private List<String> tips;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<DailyPlanDTO> getDailyPlan() {
        return dailyPlan;
    }

    public void setDailyPlan(List<DailyPlanDTO> dailyPlan) {
        this.dailyPlan = dailyPlan;
    }

    public BudgetBreakdownDTO getBudgetBreakdown() {
        return budgetBreakdown;
    }

    public void setBudgetBreakdown(BudgetBreakdownDTO budgetBreakdown) {
        this.budgetBreakdown = budgetBreakdown;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }
}