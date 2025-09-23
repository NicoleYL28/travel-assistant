package oocl.travelassistant.dto;

import java.math.BigDecimal;

public class TransportationDTO {
    private String details;
    private BigDecimal cost;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}