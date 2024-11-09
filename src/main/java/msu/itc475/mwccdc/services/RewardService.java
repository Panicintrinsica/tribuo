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
        logger.log(Level.INFO, "Starting reward processing...");

        // Clear all existing rewards
        rewardRepository.deleteAll();
        logger.log(Level.INFO, "Cleared all existing rewards.");

        List<Fan> tempFanList = new ArrayList<>(fanRepository.findAll());
        logger.log(Level.INFO, "Fetched all fans: " + tempFanList);

        // Use the path from src/main/resources
        Map<String, List<String>> seatData = ConfigReader.getSeatData("static/data/prefData.json");
        logger.log(Level.INFO, "Seat data loaded from config: " + seatData);

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

        // Handle overflow by assigning remaining seats
        for (Map.Entry<String, List<String>> entry : remainingSeats.entrySet()) {
            String stand = entry.getKey();
            List<String> seats = entry.getValue();

            if (!seats.isEmpty() && !tempFanList.isEmpty()) {
                allocateSeats(tempFanList, seats, stand);
            }
        }

        logger.log(Level.INFO, "Reward processing completed.");
    }

    private void allocateSeats(List<Fan> fans, List<String> seats, String stand) {
        logger.log(Level.INFO, "Allocating seats for stand: " + stand + " with fans: " + fans);

        List<Fan> selectedFans;
        switch (stand) {
            case "NORTH":
                selectedFans = AllocationUtil.militaryPrioritySelection(fans, seats.size());
                break;
            case "SOUTH":
                selectedFans = AllocationUtil.randomSelection(fans, seats.size());
                break;
            case "EAST":
            case "WEST":
                selectedFans = AllocationUtil.firstComeFirstServed(fans, seats.size());
                break;
            default:
                throw new IllegalArgumentException("Unknown stand: " + stand);
        }

        logger.log(Level.INFO, "Selected fans for " + stand + ": " + selectedFans);

        List<String> allocatedSeats = new ArrayList<>();
        for (int i = 0; i < seats.size() && i < selectedFans.size(); i++) {
            Fan fan = selectedFans.get(i);
            fans.remove(fan);

            Reward reward = new Reward();
            reward.setSeatId(seats.get(i)); // Use the full seat identifier including the letter
            reward.setFanId(fan.getId());

            rewardRepository.save(reward);
            allocatedSeats.add(seats.get(i)); // Mark this seat as allocated
            logger.log(Level.INFO, "Saved reward for fan: " + fan.getId() + " with seat: " + seats.get(i));
        }

        // Remove allocated seats from the original list
        seats.removeAll(allocatedSeats);
    }
}