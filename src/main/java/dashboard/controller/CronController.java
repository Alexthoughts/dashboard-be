package dashboard.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CronController {

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("App is running!");
    }

    @GetMapping("/cron-ping")
    public ResponseEntity<String> cronPing() {
        try {
            log.debug("Cron ping received");
            return ResponseEntity.ok("OK");
        } catch (Exception ex) {
            log.debug("Cron ping failed: {}", ex.getMessage(), ex);
            return ResponseEntity.status(503).body("cron-ping failed: " + ex.getMessage());
        }
    }
}