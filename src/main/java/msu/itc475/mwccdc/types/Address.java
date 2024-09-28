package msu.itc475.mwccdc.types;

public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;

    public Address(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public Address(String address) {
        String[] parts = address.split(", ");
        this.street = parts.length > 0 ? parts[0] : null;
        this.city = parts.length > 1 ? parts[1] : null;
        this.state = parts.length > 2 ? parts[2] : null;
        this.zipCode = parts.length > 3 ? parts[3] : null;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return street + ", " + city + ", " + state + ", " + zipCode;
    }
}