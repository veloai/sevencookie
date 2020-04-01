package com.util;

import java.io.File;

public class Utility {
	
	/**
	 * path의 경로가 존재하는지 체크하여 없으면 생성
	 * @param path
	 */
	public static void checkAndMakePath(String path) {
		File dir = new File(path);
        
		if (!dir.exists()) {
            dir.mkdirs();
        }
	}
	
	/**
	 * 쉘 커맨드 실행시 작업
	 * @param 쉘 명령문
	 */
	public static String cmdExecString(String command) {
		StringBuffer sb = new StringBuffer();
		String os = System.getProperty("os.name").toLowerCase(); 
		if (os.contains("win")) {
			sb.append("cmd.exe ");
		} else {
			sb.append("/bin/sh ");
		}
		sb.append("/c ");
		sb.append(command);
		
		return sb.toString();
		
	}
	
	/**
	 * path의 경로에 서비스 pid가 있는지 확인
	 * @param path
	 */
	public static boolean checkPid(String path) {
		File f = new File("/tmp/"+path);
        if(f.exists()) {
        	return true;
        }
		
		return false;
	}
	

}
