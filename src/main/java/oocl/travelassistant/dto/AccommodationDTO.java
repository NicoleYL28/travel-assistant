package oocl.travelassistant.dto;

import java.math.BigDecimal;

public class AccommodationDTO {
    private String name;
    private String address;
    private String roomType;
    private BigDecimal price;
    private String bookingLink;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getRoomType() {
        return roomType;
    }
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getBookingLink() {
        return bookingLink;
    }
    public void setBookingLink(String bookingLink) {
        this.bookingLink = bookingLink;
    }
}

