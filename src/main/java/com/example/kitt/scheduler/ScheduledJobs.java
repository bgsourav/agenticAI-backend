package com.example.kitt.scheduler;

import com.example.kitt.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ScheduledJobs {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledJobs.class);

    @Autowired
    private DataService dataService;

    @PostConstruct
    public void init() {
        logger.info("Initializing scheduled jobs");
        dataService.initializeData();
    }

    // /**
    //  * Job 1 - Runs every 2 minutes
    //  * Cron expression: "0 */2 * * * *" means every 2 minutes
    //  **/
    @Scheduled(cron = "${cron.job1.schedule:0 */2 * * * *}")
    public void executeJob1() {
        logger.info("Starting Job 1 execution");
        try {
            dataService.processJob1Data();
            logger.info("Job 1 completed successfully");
        } catch (Exception e) {
            logger.error("Job 1 failed with error: {}", e.getMessage(), e);
        }
    }

    // /**
    //  * Job 2 - Runs every 2 minutes
    //  * Cron expression: "0 */2 * * * *" means every 2 minutes
    //  */
    @Scheduled(cron = "${cron.job2.schedule:0 */2 * * * *}")
    public void executeJob2() {
        logger.info("Starting Job 2 execution");
        try {
            dataService.processJob2Data();
            logger.info("Job 2 completed successfully");
        } catch (Exception e) {
            logger.error("Job 2 failed with error: {}", e.getMessage(), e);
        }
    }

    // /**
    //  * Job 3 - Runs every 2 minutes
    //  * Cron expression: "0 */2 * * * *" means every 2 minutes
    //  */
    @Scheduled(cron = "${cron.job3.schedule:0 */2 * * * *}")
    public void executeJob3() {
        logger.info("Starting Job 3 execution");
        try {
            dataService.processJob3Data();
            logger.info("Job 3 completed successfully");
        } catch (Exception e) {
            logger.error("Job 3 failed with error: {}", e.getMessage(), e);
        }
    }

    /**
     * Health check job - Runs every 5 minutes
     * This job can be used to monitor the health of external APIs
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void healthCheck() {
        logger.debug("Performing health check");
        // Add health check logic here if needed
    }
}