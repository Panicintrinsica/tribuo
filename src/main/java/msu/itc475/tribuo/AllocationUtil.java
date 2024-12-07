/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.tribuo;
import msu.itc475.tribuo.types.Fan;
import java.util.*;
import java.util.stream.Collectors;

public class AllocationUtil {

    public static List<Fan> firstComeFirstServed(List<Fan> fans, int numberOfSeats) {
        return fans.stream()
                .sorted(Comparator.comparing(Fan::getReservationTime))
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

        // Split fans into military and non-military lists
        List<Fan> militaryFans = new ArrayList<>();
        List<Fan> nonMilitaryFans = new ArrayList<>();
        for (Fan fan : fans) {
            if (fan.isMilitary()) {
                militaryFans.add(fan);
            } else {
                nonMilitaryFans.add(fan);
            }
        }

        // Shuffle both lists
        Collections.shuffle(militaryFans, random);
        Collections.shuffle(nonMilitaryFans, random);

        List<Fan> selectedFans = new ArrayList<>();

        // Select fans for each seat
        for (int i = 0; i < numberOfSeats; i++) {
            if (!militaryFans.isEmpty() && !nonMilitaryFans.isEmpty()) {
                // Both lists have fans
                // Generates a number between 0.0 (inclusive) and 1.0 (exclusive)
                double randomNumber = random.nextDouble();
                if (randomNumber < 0.8) {
                    // 80% chance to select a military fan
                    selectedFans.add(militaryFans.remove(0));
                } else {
                    selectedFans.add(nonMilitaryFans.remove(0));
                }
            } else if (!militaryFans.isEmpty()) {
                // Only military fans left
                selectedFans.add(militaryFans.remove(0));
            } else if (!nonMilitaryFans.isEmpty()) {
                // Only non-military fans left
                selectedFans.add(nonMilitaryFans.remove(0));
            } else {
                // No more fans left
                break;
            }
        }

        return selectedFans;
    }
}