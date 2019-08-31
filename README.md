# neasaa-process-mgr
This project is to manage java process/service at one centralize storage.
With this utility you can do the following:
1. Monitor where certain process is running on required server
	Each process send heart beat to centralize storage to make sure application is up
2. Make sure only one process active at any point of time and other processes are waiting for active process to release a lock
	Only one process gets the lock and process the job, while other process waits for lock
3. You can configure to have one active process in each environment or one active process per host
4. This utility can also be used to get statics around the processes like
	Start time, time active, processing time etc.