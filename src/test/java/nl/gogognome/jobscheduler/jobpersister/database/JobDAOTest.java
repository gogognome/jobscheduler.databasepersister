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
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
            assertEquals(job.getCreationTimestamp(), readJob.getCreationTimestamp());
            assertEquals(job.getStartTimestamp(), readJob.getStartTimestamp());
            assertEquals(job.getData(), readJob.getData());
            assertEquals(job.getId(), readJob.getId());
            assertEquals(job.getRequesterId(), readJob.getRequesterId());
            assertEquals(job.getState(), readJob.getState());
            assertEquals(job.getType(), readJob.getType());
        });
    }

}
