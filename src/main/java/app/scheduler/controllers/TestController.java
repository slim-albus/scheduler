package app.scheduler.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String testController(){
        System.out.println("Testing!");
        return "Success: Spring Boot server running!";
    }
}
