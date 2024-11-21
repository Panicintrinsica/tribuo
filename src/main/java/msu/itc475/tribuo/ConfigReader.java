/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.tribuo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigReader {

    public static Map<String, List<String>> getSeatData(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource resource = new ClassPathResource(filePath);

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode rootNode = mapper.readTree(inputStream);
            Map<String, List<String>> seatData = new HashMap<>();
            rootNode.fields().forEachRemaining(entry -> {
                String stand = entry.getKey();
                JsonNode available = entry.getValue().get("available");
                List<String> seats = mapper.convertValue(available, List.class);
                seatData.put(stand, seats);
            });

            return seatData;
        }
    }
}