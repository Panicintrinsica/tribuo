package msu.itc475.mwccdc.controller;

import msu.itc475.mwccdc.types.Person;
import msu.itc475.mwccdc.types.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static msu.itc475.mwccdc.CSVLoader.readPeopleFromCSV;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        User activeUser = new User("Emrys");

        model.addAttribute("message", "Hello");
        model.addAttribute("user", activeUser);
        return "index-form";
    }

    @GetMapping("/advanced")
    public String advanced(Model model) {
        List<User> users1 = new ArrayList<User>();

        users1.add(new User("Emrys Corbin"));
        users1.add(new User("Anna Svensson"));
        users1.add(new User("Lauren Taylor"));

        model.addAttribute("users1", users1);

        return "advanced-form";
    }

    @GetMapping("/people")
    public String people(Model model) throws IOException {
        List<Person> people = readPeopleFromCSV("people_b.csv");
        model.addAttribute("people", people);

        return "person-list";
    }

}
