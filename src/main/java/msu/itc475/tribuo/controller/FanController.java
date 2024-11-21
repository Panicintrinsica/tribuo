/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.tribuo.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import msu.itc475.tribuo.services.FanService;
import msu.itc475.tribuo.types.Fan;
import msu.itc475.tribuo.dto.ImportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FanController {

    @Autowired
    private FanService fanService; // Inject FanService

    @GetMapping("/api/fans")
    public List<Fan> getData() {
        return fanService.getAllFans();
    }

    @PostMapping("/api/fans")
    public ResponseEntity<ImportResponse> addFan(@RequestBody Fan fan) {
        try {
            fanService.addFan(fan);
            return ResponseEntity.ok().header("Content-Type", "application/json")
                    .body(new ImportResponse(true, "Fan added successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ImportResponse(false, "Failed to add fan."));
        }
    }

    @PutMapping("/api/fans")
    public ResponseEntity<ImportResponse> submitPreferences(
            @RequestBody Object fanData,
            @RequestHeader(value = "Import-Type") String importType) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String rawRequestBody = objectMapper.writeValueAsString(fanData);

        try {
            List<Fan> fans = objectMapper.readValue(rawRequestBody, new TypeReference<List<Fan>>() {});
            fanService.importFans(fans, importType);

            return ResponseEntity.ok().header("Content-Type", "application/json")
                    .body(new ImportResponse(true, "Preferences updated successfully."));
        } catch (JsonProcessingException e) {
            System.err.println("Error deserializing Fan: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ImportResponse(false, "Invalid fan data."));
        }
    }
}