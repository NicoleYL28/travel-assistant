package oocl.travelassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TravelAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelAssistantApplication.class, args);
    }

    @GetMapping("/health")
    public String healthy() {
        return "I'm healthy";
    }

}
