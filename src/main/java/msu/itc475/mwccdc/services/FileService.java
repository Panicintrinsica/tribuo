/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private ResourceLoader resourceLoader;

    public List<String> loadFileLines(String filename) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filename);
        return Files.readAllLines(Paths.get(resource.getURI()));
    }
}