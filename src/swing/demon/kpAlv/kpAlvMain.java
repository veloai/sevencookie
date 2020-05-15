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
import swing.demon.util.props.Props;
import swing.demon.util.props.PropsException;


@SuppressWarnings("unchecked")
public class kpAlvMain {
	static Props props;
	final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	static void initPorpos(String[] args) {
		final String cname = args.length <= 0 || args[0] == null || args[0].isEmpty() ? null : args[0];
		
		try {
			kpAlvMain.props = new Props(cname == null ? "./kpAlvSim.properties" : cname);
		} catch (PropsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		kpAlvMain.initPorpos(args);
		
		Timer 		timer 		= new Timer();
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

					String sndDate = null;
					for (int i =0; i < dvcList.size(); i++) {
						JSONObject dvc = (JSONObject) dvcList.get(i);
//						JSONArray cmrList = (JSONArray)dvc.get("cmrList");
//						for (int ii=0; ii < cmrList.size(); ii++) {
//							JSONObject	cmr	= 	(JSONObject) cmrList.get(ii);
//							sndDate = ((String)dvc.get("sndDate")).substring(0,14);
//
//							String thisResult = String.format("%2s^%3s^%9s^%14s^%1s^%14s", sido, sigungu, cmr.get("crmId"), sndDate, cmr.get("sts").equals("1")? "N" : "F", sndDate);
//							bw.write(thisResult);
//							bw.newLine();
//						}
//						kpAlvSimMain.doKeepAlive(dvc.toJSONString());
						bw.write(dvc.toJSONString());
						bw.newLine();
						System.out.println(dvc.toJSONString());
					}
//					new File(props.getString("trns.json") + File.separator  + "dummy.eof").createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bw != null) try {bw.close();}catch (Exception e){}
				}
			}
		};
		
		timer.schedule(timerTask, 0, props.getInt("interval"));
	}

	public static int randomRange(int n1, int n2) {
		return (int) (Math.random() * (n2 - n1 + 1)) + n1;
	}

	
	static JSONArray parseDvcList() {
		JSONParser 	parser 	= new JSONParser();
		JSONArray	ret 	= null;
		
		try {
			//- TODO 배포전 경로 확인
			JSONObject obj = (JSONObject) parser.parse(new FileReader(props.getString("dvcfile.dir")));
			ret = (JSONArray) obj.get("dvcList");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
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
		boolean ret = false;
		
		try {
			InetAddress ping = InetAddress.getByName(ip);
			ret = ping.isReachable(1000);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return ret;
	}
	
	public static void doKeepAlive(String dvcJson) throws IOException {
		System.out.println("###### Send KeepAlive Start ######");
		System.out.println("- " + kpAlvMain.dFrmt.format(new Date()));
		System.out.println(dvcJson);
		
		String				serverUrl		= (String) props.get("serverUrl");
		String				prgmUrl			= (String) props.get("prgmUrl");
		URL 				url 			= new URL(serverUrl + prgmUrl);
		HttpURLConnection 	conn 			= (HttpURLConnection) url.openConnection();
		
		conn.setDoOutput(true);
		conn.setRequestMethod("POST"); 
		conn.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3");
		conn.connect();
		
		OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

		osw.write(dvcJson);
		osw.flush();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

		String line = null;

		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		
		osw.close();
		br.close();
		
		System.out.println("###### Send KeepAlive End ######");
	}
}
