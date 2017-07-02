package nl.gogognome.jobscheduler.jobpersister.database;

import nl.gogognome.jobscheduler.scheduler.Job;

import java.time.Instant;

public class JobBuilder {

    public static Job build(String id) {
        Job job = new Job(id);
        job.setCreationInstant(Instant.now());
        job.setType("Test");
        return job;
    }

}
