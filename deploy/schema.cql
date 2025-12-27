-- Create keyspace first
CREATE KEYSPACE IF NOT EXISTS userKeySpace
WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'datacenter1': 3
}
AND durable_writes = true;

-- Switch to keyspace and create table
USE userKeySpace;

CREATE TABLE IF NOT EXISTS user (
  username text PRIMARY KEY,
  uuid bigint,
  email text,
  created_at date
);
