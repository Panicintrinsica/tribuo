/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc;

import msu.itc475.mwccdc.types.Person;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {
    public static void main(String[] args) throws IOException {}

    // Solution to the deprecated formatting
    private static final CSVFormat CSV_FORMAT  = CSVFormat.Builder.create(CSVFormat.DEFAULT)
            .setIgnoreSurroundingSpaces(true)
            .build();

    public static List<Person> readPeopleFromCSV(String filePath) throws IOException {
        // Create a resource object to read the file from classpath
        ClassPathResource resource = new ClassPathResource(filePath);
        List<Person> people = new ArrayList<>();

        // Try with resources to ensure that the reader is closed
        try (Reader reader = new InputStreamReader(resource.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSV_FORMAT)) {

            for (CSVRecord record: csvParser) {
                Person person = new Person(record.get(0), record.get(1), record.get(2), record.get(3));
                people.add(person);
            }
        }
        return people;
    }
}
