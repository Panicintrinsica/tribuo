/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc;

import msu.itc475.mwccdc.types.Fan;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AllocationUtil {

    public static List<Fan> firstComeFirstServed(List<Fan> fans, int numberOfSeats) {
        return fans.stream()
                .sorted(Comparator.comparing(Fan::getReservationTime)) // Assuming Fan has a field reservationTime
                .limit(numberOfSeats)
                .collect(Collectors.toList());
    }

    public static List<Fan> randomSelection(List<Fan> fans, int numberOfSeats) {
        Collections.shuffle(fans);
        return fans.stream()
                .limit(numberOfSeats)
                .collect(Collectors.toList());
    }

    public static List<Fan> militaryPrioritySelection(List<Fan> fans, int numberOfSeats) {
        Random random = new Random();

        // Create a new list from the original list to avoid modifying it directly
        List<Fan> shuffledFans = new ArrayList<>(fans);

        // Shuffle the list to ensure initial randomness
        Collections.shuffle(shuffledFans, random);

        // List to store the selected fans based on the priority
        List<Fan> selectedFans = new ArrayList<>();

        // Loop until the required number of seats are filled or there are no more fans left
        for (int i = 0; i < numberOfSeats && !shuffledFans.isEmpty(); i++) {
            double totalWeight = 0.0;

            // Calculate the total weight based on military priority
            for (Fan fan : shuffledFans) {
                totalWeight += fan.isMilitary() ? 0.8 : 0.2;
            }

            // Generate a random number in the range of total weight
            double r = random.nextDouble() * totalWeight;

            double runningWeight = 0.0;
            Fan selectedFan = null;

            // Determine which fan to select based on the weighted random number
            for (Fan fan : shuffledFans) {
                runningWeight += fan.isMilitary() ? 0.8 : 0.2;
                if (r <= runningWeight) {
                    selectedFan = fan;
                    break;
                }
            }

            // If a fan is selected, add them to the selected list and remove from the shuffled list
            if (selectedFan != null) {
                selectedFans.add(selectedFan);
                shuffledFans.remove(selectedFan);
            }
        }

        // Return the list of selected fans prioritizing military fans
        return selectedFans;
    }
}