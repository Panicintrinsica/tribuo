/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc.controller;

import msu.itc475.mwccdc.types.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

import static msu.itc475.mwccdc.CSVLoader.readPeopleFromCSV;

@Controller
public class PersonController {

    @GetMapping("/people")
    public String people(Model model) throws IOException {
        List<Person> people = readPeopleFromCSV("people_b.csv");
        model.addAttribute("people", people);

        return "person-list";
    }
}
