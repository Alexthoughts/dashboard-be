package dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CronController {
    
    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("App is running!");
    }

    @GetMapping("/cron-ping")
    public ResponseEntity<String> cronPing() {
        return ResponseEntity.ok("OK");
    }
}