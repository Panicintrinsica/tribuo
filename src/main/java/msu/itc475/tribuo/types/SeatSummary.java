/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.tribuo.types;

public class SeatSummary {
    private String name;
    private int available;
    private int preferred;
    private double price;
    private double estimatedTotalEarnings;

    public SeatSummary(String name, int available, int preferred, double price, double estimatedTotalEarnings) {
        this.name = name;
        this.available = available;
        this.preferred = preferred;
        this.price = price;
        this.estimatedTotalEarnings = estimatedTotalEarnings;
    }

    public String getName() { return titleCase(name); }
    public int getAvailableSeats() { return available; }
    public int getAvailableCount() { return available; }
    public int getPreferred() { return preferred; }
    public double getPrice() { return price; }
    public double getEstimatedTotalEarnings() { return estimatedTotalEarnings; }

    private String titleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1).toLowerCase();
    }
}