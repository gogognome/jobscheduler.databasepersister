CREATE TABLE NlGogognomeJobs (
  id VARCHAR(1000),
  creationInstant TIMESTAMP NOT NULL,
  scheduledAtInstant TIMESTAMP NULL,
  type VARCHAR(1000) NOT NULL,
  data VARCHAR(100000) NULL,
  state VARCHAR(100) NOT NULL,
  requesterId VARCHAR(1000) NULL,
  PRIMARY KEY (id)
);
