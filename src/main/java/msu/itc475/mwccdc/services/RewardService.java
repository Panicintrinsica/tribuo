/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc.services;

import msu.itc475.mwccdc.AllocationUtil;
import msu.itc475.mwccdc.ConfigReader;
import msu.itc475.mwccdc.dto.RewardResponse;
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

    public List<RewardResponse> getAllRewardsWithFanDetails() {
        return rewardRepository.findRewardsWithFanDetails();
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

        List<Fan> secondPassFans = new ArrayList<>(tempFanList);
        for (Map.Entry<String, List<String>> entry : remainingSeats.entrySet()) {
            String stand = entry.getKey();
            List<String> seats = entry.getValue();

            if (!seats.isEmpty() && !secondPassFans.isEmpty()) { // Use the local copy
                allocateSeats(secondPassFans, seats, stand);
            }
        }

    }

    private void allocateSeats(List<Fan> fans, List<String> seats, String stand) {
        List<Fan> selectedFans = switch (stand) {
            case "NORTH" -> new ArrayList<>(AllocationUtil.militaryPrioritySelection(fans, seats.size()));
            case "SOUTH" -> new ArrayList<>(AllocationUtil.randomSelection(fans, seats.size()));
            case "EAST", "WEST" -> new ArrayList<>(AllocationUtil.firstComeFirstServed(fans, seats.size()));
            default -> throw new IllegalArgumentException("Unknown stand: " + stand);
        };

        List<String> allocatedSeats = new ArrayList<>();

        for (int i = 0; i < seats.size() && i < selectedFans.size(); i++) {
            Fan fan = selectedFans.get(i);

            // Check if the fan already has a reward
            if (hasReward(fan)) {
                continue; // Skip this fan if they already have a reward
            }
            fans.remove(fan); // Remove from the input list to prevent re-selection


            Reward reward = new Reward();
            reward.setSeatId(seats.get(i));
            reward.setFan(fan);
            fan.setReward(reward);
            rewardRepository.save(reward);
            allocatedSeats.add(seats.get(i));
        }

        seats.removeAll(allocatedSeats);
    }

    private boolean hasReward(Fan fan) {
        return rewardRepository.existsRewardForFan(fan);
    }
}