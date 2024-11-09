/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc.services;

import msu.itc475.mwccdc.AllocationUtil;
import msu.itc475.mwccdc.ConfigReader;
import msu.itc475.mwccdc.repos.FanRepository;
import msu.itc475.mwccdc.repos.RewardRepository;
import msu.itc475.mwccdc.types.Fan;
import msu.itc475.mwccdc.types.Reward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private static final Logger logger = Logger.getLogger(RewardService.class.getName());

    private final RewardRepository rewardRepository;
    private final FanRepository fanRepository;

    @Autowired
    public RewardService(RewardRepository rewardRepository, FanRepository fanRepository) {
        this.rewardRepository = rewardRepository;
        this.fanRepository = fanRepository;
    }

    public void processRewards() throws IOException {
        // Clear all existing rewards
        rewardRepository.deleteAll();

        // Get all fans from the database and store them in a temp list to work with.
        List<Fan> tempFanList = new ArrayList<>(fanRepository.findAll());

        // Load seat IDs from the config file.
        Map<String, List<String>> seatData = ConfigReader.getSeatData("static/data/prefData.json");

        // Store the remaining seats after allocation for overflow
        Map<String, List<String>> remainingSeats = new HashMap<>();

        // Initially assign seats based on preferences
        for (Map.Entry<String, List<String>> entry : seatData.entrySet()) {
            String stand = entry.getKey();
            List<String> seats = entry.getValue();

            List<Fan> preferredFans = tempFanList.stream()
                    .filter(fan -> fan.getPreferredStand().equals(stand))
                    .collect(Collectors.toList());

            allocateSeats(preferredFans, seats, stand);

            // Save remaining seats for overflow
            if (!seats.isEmpty()) {
                remainingSeats.put(stand, new ArrayList<>(seats)); // Copy the list to avoid concurrent modification issues
            }

            tempFanList.removeAll(preferredFans);
        }

        // Second pass allocation once preferred are seated.
        for (Map.Entry<String, List<String>> entry : remainingSeats.entrySet()) {
            String stand = entry.getKey();
            List<String> seats = entry.getValue();

            if (!seats.isEmpty() && !tempFanList.isEmpty()) {
                allocateSeats(tempFanList, seats, stand);
            }
        }
    }

    private void allocateSeats(List<Fan> fans, List<String> seats, String stand) {
        List<Fan> selectedFans = switch (stand) {
            case "NORTH" -> AllocationUtil.militaryPrioritySelection(fans, seats.size());
            case "SOUTH" -> AllocationUtil.randomSelection(fans, seats.size());
            case "EAST", "WEST" -> AllocationUtil.firstComeFirstServed(fans, seats.size());
            default -> throw new IllegalArgumentException("Unknown stand: " + stand);
        };

        List<String> allocatedSeats = new ArrayList<>();

        for (int i = 0; i < seats.size() && i < selectedFans.size(); i++) {
            Fan fan = selectedFans.get(i);
            fans.remove(fan);

            Reward reward = new Reward();
            reward.setSeatId(seats.get(i));
            reward.setFanId(fan.getId());

            rewardRepository.save(reward);
            allocatedSeats.add(seats.get(i)); // Mark this seat as allocated
        }

        // Remove allocated seats from the original list
        seats.removeAll(allocatedSeats);
    }
}