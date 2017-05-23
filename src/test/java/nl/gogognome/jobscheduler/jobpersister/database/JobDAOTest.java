package nl.gogognome.jobscheduler.jobpersister.database;

import nl.gogognome.dataaccess.migrations.DatabaseMigratorDAO;
import nl.gogognome.dataaccess.transaction.CompositeDatasourceTransaction;
import nl.gogognome.dataaccess.transaction.NewTransaction;
import nl.gogognome.jobscheduler.scheduler.Job;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class JobDAOTest {

    private DatabaseJobPersisterProperties properties = new DatabaseJobPersisterProperties();
    private Connection connectionToKeepInMemoryDatabaseAlive;
    private JobDAO jobDAO;

    @Before
    public void setupInMemoryDatabase() throws SQLException {
        String jdbcUrl = "jdbc:h2:mem:" + UUID.randomUUID();
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(jdbcUrl);
        connectionToKeepInMemoryDatabaseAlive = dataSource.getConnection();
        CompositeDatasourceTransaction.registerDataSource(properties.getConnectionName(), dataSource);

        NewTransaction.runs(() -> new DatabaseMigratorDAO(properties.getConnectionName()).applyMigrationsFromResource("/database/_migrations.txt"));

        jobDAO = new JobDAO(properties);
    }

    @After
    public void removeInMemoryDatabase() {
        connectionToKeepInMemoryDatabaseAlive = null;
    }

    @Test
    public void createJobAndReadItBack_shouldGetSameJobAgain() throws SQLException {
        Job job = JobBuilder.buildJob("1");

        NewTransaction.runs(() -> {
            jobDAO.create(job);

            Job readJob = jobDAO.get(job.getId());
            assertNotNull(readJob);
            assertEquals(job.getCreationInstant(), readJob.getCreationInstant());
            assertEquals(job.getSchedueledAtInstant(), readJob.getSchedueledAtInstant());
            assertEquals(job.getData(), readJob.getData());
            assertEquals(job.getId(), readJob.getId());
            assertEquals(job.getRequesterId(), readJob.getRequesterId());
            assertEquals(job.getState(), readJob.getState());
            assertEquals(job.getType(), readJob.getType());
        });
    }

    @Test
    public void findAll_noJobsCreated_returnsEmptyList() {
        List<Job> jobs = NewTransaction.returns(() -> jobDAO.findAll());

        assertTrue(jobs.isEmpty());
    }

    @Test
    public void findAll_oneJobCreated_returnsJob() {
        Job job = NewTransaction.returns(() -> jobDAO.create(JobBuilder.buildJob("1")));

        List<Job> jobs = NewTransaction.returns(() -> jobDAO.findAll());

        assertEquals(asList(job), jobs);
    }

    @Test
    public void findAll_multipleJobsCreated_returnsJobs() {
        Job job1 = NewTransaction.returns(() -> jobDAO.create(JobBuilder.buildJob("1")));
        Job job2 = NewTransaction.returns(() -> jobDAO.create(JobBuilder.buildJob("2")));
        Job job3 = NewTransaction.returns(() -> jobDAO.create(JobBuilder.buildJob("3")));

        List<Job> jobs = NewTransaction.returns(() -> jobDAO.findAll());

        assertEquals(asList(job1, job2, job3), jobs);
    }
}
