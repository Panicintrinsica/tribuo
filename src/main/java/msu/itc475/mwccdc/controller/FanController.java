/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import msu.itc475.mwccdc.services.FanService;
import msu.itc475.mwccdc.types.Fan;
import msu.itc475.mwccdc.dto.ImportResponse;
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
    public ResponseEntity<ImportResponse> submitPreferences(
            @RequestBody Object fanData,
            @RequestHeader(value = "Import-Type", required = false) String importType) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String rawRequestBody = objectMapper.writeValueAsString(fanData);

        try {
            if (importType != null) {
                // Handle import (replace or append) - expecting a list of fans
                List<Fan> fans = objectMapper.readValue(rawRequestBody, new TypeReference<List<Fan>>() {});
                fanService.importFans(fans, importType);
            } else {
                // Attempt to directly deserialize into a Fan object
                Fan fan = objectMapper.readValue(rawRequestBody, Fan.class);
                fanService.addFan(fan);
            }

            return ResponseEntity.ok().header("Content-Type", "application/json")
                    .body(new ImportResponse(true, "Preferences updated successfully."));
        } catch (JsonProcessingException e) {
            // Log the exception for debugging
            System.err.println("Error deserializing Fan: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ImportResponse(false, "Invalid fan data."));
        }
    }
}