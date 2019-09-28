package com.neasaa.processmgr.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.security.CodeSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neasaa.processmgr.ProcessManager;

public class SystemUtils {
	private static final Logger LOG = LoggerFactory.getLogger(SystemUtils.class);

	/**
	 * Try to get the system process id (PID) for current JVM. If not able to find
	 * the actual pid, returns -1.
	 * 
	 * @return
	 */
	public static int getCurrentJvmProcessId() {

		int pid = -1;
		final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		final int index = jvmName.indexOf('@');

		if (index < 1) {
			try {
				pid = Integer.parseInt(jvmName);
			} catch (NumberFormatException nfe) {
				LOG.info("Fail to parse the pid " + jvmName + " from jvmName " + jvmName + ", setting pid as -1");

			}
		} else {
			String strPid = jvmName.substring(0, index);
			try {
				pid = Integer.parseInt(strPid);
			} catch (NumberFormatException nfe) {
				LOG.info("Fail to parse the pid " + strPid + " from jvmName " + jvmName + ", setting pid as -1");
			}
		}
		return pid;
	}

	/**
	 * Get jar path or class path folder from where specified class is loaded
	 */
	public static String getSourcePathForClass(Class<?> aClazz) {

		if (aClazz == null) {
			return null;
		}

		try {
			CodeSource codeSource = aClazz.getProtectionDomain().getCodeSource();
			if (codeSource != null) {
				URL location = codeSource.getLocation();
				return location.getPath();
			}
		} catch (Throwable e) {
			LOG.info("Error determining path for class " + aClazz.getName(), e);
		}
		return null;
	}

	/**
	 * Get jar version from jar path
	 */
	public static String getJarversionFromJarPath(String aJarFilePath) {
		String jarFileWithVersion = "unknown";
		if (aJarFilePath == null || aJarFilePath.isEmpty()) {
			return jarFileWithVersion;
		}

		try {
			String fileName = new File(aJarFilePath).getName();
			if (fileName.endsWith(".jar")) {
				jarFileWithVersion = fileName.substring(0, fileName.length() - 4);
			}
			else {
				LOG.info("Jarfile path '" + aJarFilePath
						+ "' does not ends with .jar, assumimg jar is loaded from classpath folder");
				jarFileWithVersion = aJarFilePath;
			}
		}
		catch (Throwable e) {
			LOG.info("Error extracting jar version", e);
			jarFileWithVersion = aJarFilePath;
		}
		return jarFileWithVersion;
	}
	
	public static final String getProcessMgrVersion () {
		String jarFilePath = getSourcePathForClass(ProcessManager.class);
		return getJarversionFromJarPath(jarFilePath);
	}

}
