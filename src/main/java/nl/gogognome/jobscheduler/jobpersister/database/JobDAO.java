package nl.gogognome.jobscheduler.jobpersister.database;

import nl.gogognome.dataaccess.dao.AbstractDomainClassDAO;
import nl.gogognome.dataaccess.dao.NameValuePairs;
import nl.gogognome.dataaccess.dao.ResultSetWrapper;
import nl.gogognome.jobscheduler.scheduler.Job;
import nl.gogognome.jobscheduler.scheduler.JobState;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
class JobDAO extends AbstractDomainClassDAO<Job>{

    private final DatabaseJobPersisterProperties properties;

    public JobDAO(DatabaseJobPersisterProperties properties) {
        super(properties.getTableName(), null, properties.getConnectionName());
        this.properties = properties;
    }

    @Override
    protected Job getObjectFromResultSet(ResultSetWrapper result) throws SQLException {
        Job job = new Job(result.getString(properties.getIdColumn()));
        job.setCreationInstant(result.getInstant(properties.getCreationInstantColumn()));
        job.setSchedueledAtInstant(result.getInstant(properties.getScheduledAtInstantColumn()));
        job.setType(result.getString(properties.getTypeColumn()));
        job.setData(result.getString(properties.getDataColumn()));
        job.setState(result.getEnum(JobState.class, properties.getJobStateColumn()));
        job.setRequesterId(result.getString(properties.getRequesterIdColumn()));

        return job;
    }

    @Override
    protected NameValuePairs getNameValuePairs(Job job) throws SQLException {
        return new NameValuePairs()
                .add(properties.getIdColumn(), job.getId())
                .add(properties.getCreationInstantColumn(), job.getCreationInstant())
                .add(properties.getScheduledAtInstantColumn(), job.getSchedueledAtInstant())
                .add(properties.getTypeColumn(), job.getType())
                .add(properties.getDataColumn(), job.getData())
                .add(properties.getJobStateColumn(), job.getState())
                .add(properties.getRequesterIdColumn(), job.getRequesterId());
    }
}
