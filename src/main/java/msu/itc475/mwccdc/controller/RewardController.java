/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc.controller;

import msu.itc475.mwccdc.services.RewardService;
import msu.itc475.mwccdc.types.Reward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
@RequestMapping("/rewards")
public class RewardController {

    private static final Logger logger = Logger.getLogger(RewardController.class.getName());

    private final RewardService rewardService;

    @Autowired
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/")
    public String index() {
        return "rewards";
    }

//    @GetMapping
//    public String getAllRewards() {
//        return rewardService.getAllRewards().toString();
//    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerReward() {
        logger.log(Level.INFO, "Trigger reward endpoint called.");

        try {
            rewardService.processRewards();
            return ResponseEntity.ok("Reward processed successfully!");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to process rewards", e);
            return ResponseEntity.status(500).body("Failed to process rewards.");
        }
    }
}