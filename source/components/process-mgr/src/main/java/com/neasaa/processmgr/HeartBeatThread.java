package com.neasaa.processmgr;

import com.neasaa.processmgr.entity.ProcessEntity;

public class HeartBeatThread extends Thread {
	
	private ProcessEntity process;
	
	public HeartBeatThread (ProcessEntity aProcess) {
		this.process = aProcess;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}
}
