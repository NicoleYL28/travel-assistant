package oocl.travelassistant.dto;

import java.math.BigDecimal;

public class BudgetBreakdownDTO {
    private BigDecimal accommodation;
    private BigDecimal food;
    private BigDecimal transportation;
    private BigDecimal activities;
    private BigDecimal shopping;
    private BigDecimal other;

    public BigDecimal getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(BigDecimal accommodation) {
        this.accommodation = accommodation;
    }

    public BigDecimal getFood() {
        return food;
    }

    public void setFood(BigDecimal food) {
        this.food = food;
    }

    public BigDecimal getTransportation() {
        return transportation;
    }

    public void setTransportation(BigDecimal transportation) {
        this.transportation = transportation;
    }

    public BigDecimal getActivities() {
        return activities;
    }

    public void setActivities(BigDecimal activities) {
        this.activities = activities;
    }

    public BigDecimal getShopping() {
        return shopping;
    }

    public void setShopping(BigDecimal shopping) {
        this.shopping = shopping;
    }

    public BigDecimal getOther() {
        return other;
    }

    public void setOther(BigDecimal other) {
        this.other = other;
    }
}