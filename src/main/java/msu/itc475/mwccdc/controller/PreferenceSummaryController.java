/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import msu.itc475.mwccdc.types.SeatSummary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
public class PreferenceSummaryController {

    @GetMapping("/")
    public String index(Model model) {

        List<SeatSummary> seatSummaries = new ArrayList<>();
        double totalEarnings = 0.0;
        int totalAvailableSeats = 0;
        int totalPreferredSeats = 0;
        int totalUnclaimedSeats = 0;
        int totalRemainingSeats = 0;

        try {
            // Load and parse JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/data/prefData.json");

            if (inputStream == null) {
                throw new IOException("JSON file not found");
            }

            JsonNode rootNode = objectMapper.readTree(inputStream);

            // Iterate over nodes and calculate estimated total earnings
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String name = field.getKey();
                JsonNode details = field.getValue();

                List<String> availableSeats = new ArrayList<>();
                JsonNode availableNode = details.path("available ");
                if (availableNode.isArray()) {
                    availableNode.forEach(seatNode -> availableSeats.add(seatNode.asText()));
                }

                JsonNode preferredNode = details.path("preferred ");
                JsonNode priceNode = details.path("price ");

                int preferred = preferredNode.isMissingNode() ? 0 : preferredNode.asInt(0);
                double price = priceNode.isMissingNode() ? 0.0 : priceNode.asDouble(0.0);

                double estimatedTotalEarnings = (preferred > availableSeats.size())
                        ? price * availableSeats.size()
                        : preferred * price;

                // Add to total
                totalEarnings += estimatedTotalEarnings;
                totalAvailableSeats += availableSeats.size();
                totalPreferredSeats += preferred;

                if(availableSeats.size() > preferred){
                    totalRemainingSeats += (availableSeats.size() - preferred);
                }

                seatSummaries.add(new SeatSummary(name, availableSeats, preferred, price, estimatedTotalEarnings));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("seatSummaries", seatSummaries);
        model.addAttribute("totalEarnings", totalEarnings);
        model.addAttribute("totalAvailableSeats", totalAvailableSeats);
        model.addAttribute("totalPreferredSeats", totalPreferredSeats);
        model.addAttribute("totalRemainingSeats", totalRemainingSeats);
        return "preferences";
    }


}
