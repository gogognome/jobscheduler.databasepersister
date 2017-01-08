package nl.gogognome.jobscheduler.jobpersister.database;

import nl.gogognome.jobscheduler.scheduler.Job;
import nl.gogognome.jobscheduler.scheduler.JobState;

import java.time.Instant;

public class JobBuilder {

    public static Job buildJob(String id) {
        Job job = new Job(id);
        job.setCreationTimestamp(Instant.now());
        job.setType("Test");
        job.setData("Data");
        job.setState(JobState.IDLE);
        return job;
    }

}
