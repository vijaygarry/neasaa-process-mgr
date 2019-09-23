package com.neasaa.processmgr.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralUtilities {
	
	private static final Logger logger = LoggerFactory.getLogger(GeneralUtilities.class);
	
	/**
	 * Get Local Host name. If fail to get the local host name, returns "localhost"
	 * 
	 * @return
	 */

	public static String getLocalHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error("Failed to get local host name", e);
			return "localhost";
		}
	}
}
