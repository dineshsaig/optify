package com.example.jobfinder.scheduler;

import com.example.jobfinder.service.JobRefreshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Triggers job refresh at 8:00 AM, 12:00 PM, 4:00 PM, and 8:00 PM
 * in the JVM's local time zone.
 *
 * To change the time zone, set:
 *   spring.task.scheduling.pool.size=1
 *   spring.jackson.time-zone=America/New_York   (or your preferred zone)
 * in application.properties, and update the zone attribute below.
 */
@Component
public class RefreshScheduler {

    private static final Logger log = LoggerFactory.getLogger(RefreshScheduler.class);

    private final JobRefreshService refreshService;

    public RefreshScheduler(JobRefreshService refreshService) {
        this.refreshService = refreshService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void refreshAt8AM() {
        log.info("Scheduled trigger: 8:00 AM");
        refreshService.refresh();
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void refreshAt12PM() {
        log.info("Scheduled trigger: 12:00 PM");
        refreshService.refresh();
    }

    @Scheduled(cron = "0 0 16 * * *")
    public void refreshAt4PM() {
        log.info("Scheduled trigger: 4:00 PM");
        refreshService.refresh();
    }

    @Scheduled(cron = "0 0 20 * * *")
    public void refreshAt8PM() {
        log.info("Scheduled trigger: 8:00 PM");
        refreshService.refresh();
    }
}
