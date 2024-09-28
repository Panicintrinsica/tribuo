package msu.itc475.mwccdc;

import msu.itc475.mwccdc.types.Person;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {
    public static void main(String[] args) throws IOException {

    }

    // Solution to the deprecated formatting
    private static final CSVFormat CSV_FORMAT  = CSVFormat.Builder.create(CSVFormat.DEFAULT)
            .setIgnoreSurroundingSpaces(true)
            .build();


    public static List<Person> readPeopleFromCSV(String fileName) throws IOException {
        List<Person> people = new ArrayList<>();

        String root = "D:\\local";
        //String fileName = "people_b.csv";
        String filePath = root + "\\" + fileName;

        FileReader reader = new FileReader(filePath);

        CSVParser parser = CSVParser.parse(reader, CSV_FORMAT);

        for (CSVRecord record: parser) {
            Person person = new Person(record.get(0), record.get(1), record.get(2), record.get(3));
            people.add(person);
        }

        return people;
    }
}
