CREATE TABLE SAIX_AUTH.PROCESSSTATUS 
(
	STATUS VARCHAR2(64), 
	DESCRIPTION VARCHAR2(256), 
	PRIMARY KEY (STATUS)
);

COMMENT ON COLUMN SAIX_AUTH.PROCESSSTATUS.STATUS IS 'Unique status name supported';
COMMENT ON COLUMN SAIX_AUTH.PROCESSSTATUS.DESCRIPTION IS 'description for this status';
COMMENT ON TABLE SAIX_AUTH.PROCESSSTATUS  IS 'Table to list possible process status.';


INSERT INTO SAIX_AUTH.PROCESSSTATUS (STATUS, DESCRIPTION) 
VALUES('WAITING', 'Process is started and waiting to acquire lock to start processing.');

INSERT INTO SAIX_AUTH.PROCESSSTATUS (STATUS, DESCRIPTION) 
VALUES('PROCESSING', 'Process is active and processing.');

INSERT INTO SAIX_AUTH.PROCESSSTATUS (STATUS, DESCRIPTION) 
VALUES('EXIT_NORMAL', 'Process exited normally and is not running.');

INSERT INTO SAIX_AUTH.PROCESSSTATUS (STATUS, DESCRIPTION) 
VALUES('KILLED', 'This status indicate that process being forcefully killed.');
-- Ideally process is killed if process is in killed status.
-- Process manager update the status to killed in one of the following condition:
-- 1. If process is in waiting/processing status for long without heartbeat 
-- 2. Try to start the new process with same name on same host. In this case old process with waiting/processing status will be updated to killed.


INSERT INTO SAIX_AUTH.PROCESSSTATUS (STATUS, DESCRIPTION) 
VALUES('ERROR', 'Error exited with some error.');
