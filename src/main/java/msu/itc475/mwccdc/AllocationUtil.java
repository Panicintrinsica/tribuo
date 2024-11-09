/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc;

import msu.itc475.mwccdc.types.Fan;

import java.util.*;
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
        List<Fan> militaryFans = fans.stream().filter(Fan::isMilitary).collect(Collectors.toList());
        List<Fan> nonMilitaryFans = fans.stream().filter(fan -> !fan.isMilitary()).collect(Collectors.toList());

        PriorityQueue<Fan> priorityQueue = new PriorityQueue<>((f1, f2) -> {
            double weight1 = f1.isMilitary() ? 0.8 : 0.2;
            double weight2 = f2.isMilitary() ? 0.8 : 0.2;
            return Double.compare(weight2, weight1);
        });

        priorityQueue.addAll(militaryFans);
        priorityQueue.addAll(nonMilitaryFans);

        List<Fan> selectedFans = new ArrayList<>();
        while (selectedFans.size() < numberOfSeats && !priorityQueue.isEmpty()) {
            selectedFans.add(priorityQueue.poll());
        }

        return selectedFans;
    }
}