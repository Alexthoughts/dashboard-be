package dashboard.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CronController {

    private volatile long lastExecution = 0;
    private static final long MIN_INTERVAL_MS = 5 * 60 * 1000; // 5 minutes

    @GetMapping("/cron-ping")
    public ResponseEntity<String> cronPing() {
        long now = System.currentTimeMillis();
        synchronized (this) {
            if (now - lastExecution < MIN_INTERVAL_MS) {
                log.warn("Duplicate cron request ignored");
                return ResponseEntity.ok("Duplicate ignored");
            }
            lastExecution = now;
        }
        try {
            log.info("Cron job ping");
            return ResponseEntity.ok("OK");
        } catch (Exception ex) {
            log.error("Cron job failed: {}", ex.getMessage(), ex);
            return ResponseEntity.status(503).body("cron-ping failed: " + ex.getMessage());
        }
    }
}