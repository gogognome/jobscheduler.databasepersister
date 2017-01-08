package nl.gogognome.jobscheduler.jobpersister.database;

import nl.gogognome.dataaccess.migrations.DatabaseMigratorDAO;
import nl.gogognome.dataaccess.transaction.NewTransaction;
import nl.gogognome.dataaccess.transaction.RequireTransaction;
import nl.gogognome.jobscheduler.scheduler.Job;
import nl.gogognome.jobscheduler.scheduler.JobPersister;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DatabaseJobPersister implements JobPersister {

    private final DatabaseJobPersisterProperties properties;
    private final JobDAO jobDAO;

    public DatabaseJobPersister(DatabaseJobPersisterProperties properties, JobDAO jobDAO) {
        this.properties = properties;
        this.jobDAO = jobDAO;
    }

    @Override
    public void create(Job job) {
        RequireTransaction.runs(() -> jobDAO.create(job));
    }

    @Override
    public void remove(String jobId) {
        RequireTransaction.runs(() -> jobDAO.delete(jobId));
    }

    @Override
    public void update(Job job) {
        RequireTransaction.runs(() -> jobDAO.update(job));
    }
}
