/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.tribuo.controller;

import msu.itc475.tribuo.dto.RewardResponse;
import msu.itc475.tribuo.services.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/rewards")
public class RewardController {
    private final RewardService rewardService;

    @Autowired
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("")
    public String index(Model model) {
        List<RewardResponse> rewards = rewardService.getAllRewardsWithFanDetails();
        model.addAttribute("persons", rewards);
        return "rewards"; // Ensure this returns the template name
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerReward() {
        try {
            rewardService.processRewards();
            return ResponseEntity.ok("Reward processed successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to process rewards.");
        }
    }

    @GetMapping("/all")
    public List<RewardResponse> getAllRewards() {
        return rewardService.getAllRewardsWithFanDetails();
    }
}