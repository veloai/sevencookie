package swing.demon.kpAlv;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.FileLog;
import swing.demon.util.LogShow;
import swing.demon.util.props.Props;
import swing.demon.util.props.PropsException;


@SuppressWarnings("unchecked")
public class kpAlvMain {
	static Props props;
	final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	static boolean isLogShow = false;
	static Timer timer;
	static void initPorpos(String path) {
		//final String cname = args.length <= 0 || args[0] == null || args[0].isEmpty() ? null : args[0];
		
		kpAlvMain.props = new Props(path);
	}
	
	static JSONArray parseDvcList() {
		JSONParser 	parser 	= new JSONParser();
		JSONArray	ret 	= null;
		
		try {
			//- TODO 배포전 경로 확인
			JSONObject obj = (JSONObject) parser.parse(new FileReader(props.getString("dvcfile.dir")));
			ret = (JSONArray) obj.get("dvcList");
		} catch (ParseException | IOException e) {
			LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
			return null;
		}
		return ret;
	}

	static void chkSts(JSONArray dvcList) {
		String sndDate = kpAlvMain.dFrmt.format(new Date());
		
		for (int i =0; i < dvcList.size(); i++) {
			JSONObject 	dvc 	= (JSONObject) dvcList.get(i);
			JSONObject  dvcItem	= (JSONObject) dvc.get("dvc");
			JSONArray	cmrList = (JSONArray) dvc.get("cmrList");
		
			dvc.put("sndDate", sndDate);
			dvcItem.put("sts", 1);
			
			kpAlvMain.chkCmrSts(cmrList);
		}
	}
	
	static void chkCmrSts(JSONArray cmrList) {
		for (int i =0; i < cmrList.size(); i++) {
			JSONObject	cmr	= 	(JSONObject) cmrList.get(i);
			String		ip	=	(String) cmr.get("ip");
			
			cmr.put("sts", kpAlvMain.doPing(ip) ? 1 : 2);
			cmr.remove("ip");
		}
	}
	
	static boolean doPing (String ip) {
		boolean ret;
		
		try {
			InetAddress ping = InetAddress.getByName(ip);
			ret = ping.isReachable(1000);
		} catch (UnknownHostException e) {
			LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
			return false;
		} catch (IOException e) {
			LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
			return false;
		}

		return ret;
	}

	public void kpStart(String filePath) {
		if(timer == null) {

			kpAlvMain.initPorpos(filePath);

			isLogShow = props.getBoolean("is.log.show");
			if(isLogShow) {
				String logPath = props.getString("log.file.path");
				FileLog fileLog = new FileLog();
				fileLog.setFileLog(logPath, "kpAlive");
			}

			timer = new Timer();
			TimerTask 	timerTask 	= new TimerTask() {
				@Override
				public void run() {
					BufferedWriter bw = null;
					try {

						JSONArray dvcList = kpAlvMain.parseDvcList();
						kpAlvMain.chkSts(dvcList);
						String sido = props.getString("sido.cd");
						String sigungu = props.getString("sigungu.cd");
						String fileName = sido + sigungu + "_" + kpAlvMain.dFrmt.format(new Date());
						bw = new BufferedWriter(new FileWriter(props.getString("trns.json") + File.separator + fileName + ".json", true));

						//String sndDate = null;
						for (int i =0; i < dvcList.size(); i++) {
							JSONObject dvc = (JSONObject) dvcList.get(i);
							bw.write(dvc.toJSONString());
							bw.newLine();
							LogShow.logMessage(isLogShow, dvc.toJSONString());
						}

					} catch (Exception e) {
						//LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
						e.printStackTrace();
						timer.cancel();
					} finally {
						if (bw != null) try {bw.close();}catch (Exception e){}
					}
				}
			};

			timer.schedule(timerTask, 0, props.getInt("interval"));
		} else {
			LogShow.logMessage(isLogShow, "이미 실행중 입니다.");
		}
	}

	public void kpStop() {
		if(timer != null) {
			timer.cancel();
			timer = null;
			LogShow.logMessage(isLogShow, "정상적으로 KeepAlive 종료");
		} else {
			LogShow.logMessage(isLogShow, "실행중인 keepAlive thread 없습니다.");
		}
	}
}
