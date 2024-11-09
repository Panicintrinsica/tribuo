/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc.services;

import msu.itc475.mwccdc.repos.FanRepository;
import msu.itc475.mwccdc.types.Fan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FanService {

    private final FanRepository fanRepository;

    @Autowired
    public FanService(FanRepository fanRepository) {
        this.fanRepository = fanRepository;
    }

    public List<Fan> getAllFans() {
        return fanRepository.findAll();
    }

    public void importFans(List<Fan> fans, String importType) {
        if ("replace".equals(importType)) {
            fanRepository.deleteAll();
            fanRepository.saveAll(fans);
        } else if ("append".equals(importType)) {
            List<Fan> existingFans = fanRepository.findAll();
            for (Fan newFan : fans) {
                if (!fanExists(existingFans, newFan)) {
                    fanRepository.save(newFan);
                }
            }
        }
    }

    private boolean fanExists(List<Fan> existingFans, Fan newFan) {
        for (Fan existingFan : existingFans) {
            if (existingFan.getFirstName().equals(newFan.getFirstName()) &&
                    existingFan.getLastName().equals(newFan.getLastName()) &&
                    existingFan.getEmail().equals(newFan.getEmail())) {
                return true;
            }
        }
        return false;
    }

    public void addFan(Fan fan) {
        fanRepository.save(fan);
    }
}