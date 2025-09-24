package oocl.travelassistant.dto;

public class LocationDTO {
    private String name;
    private String address;
    private CoordinatesDTO coordinates;

    // Constructors
    public LocationDTO() {}

    public LocationDTO(String name, String address, CoordinatesDTO coordinates) {
        this.name = name;
        this.address = address;
        this.coordinates = coordinates;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public CoordinatesDTO getCoordinates() { return coordinates; }
    public void setCoordinates(CoordinatesDTO coordinates) { this.coordinates = coordinates; }
}