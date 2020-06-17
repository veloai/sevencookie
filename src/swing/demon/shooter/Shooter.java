package swing.demon.shooter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.props.Props;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class Shooter {

    private static JSONParser parser = new JSONParser();
    final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    static boolean isLogShow = false;
    private static Logger logger = LoggerFactory.getLogger(Shooter.class);

    public static void processShooter(String fileName, Props props, Boolean isShow) {

        String				serverUrl		= (String) props.get("serverUrl");
        String				prgmUrl			= (String) props.get("prgmUrl");
        isLogShow = isShow;
        readFile(fileName, serverUrl, prgmUrl);

        boolean isRemove = (props.getString("watching.dir.file.remove") != null) && props.getString("watching.dir.file.remove").equalsIgnoreCase("y");
        if (isRemove) {
            try {
                new File(fileName).delete();
            } catch (SecurityException e) {
                logger.info(ExceptionConvert.TraceAllError(e));
            }
        }
    }

    public static JSONObject readFile(String json, String serverUrl, String prgmUrl) {
        BufferedReader br = null;
        JSONObject jsonObject = null;
        try {
            br = new BufferedReader(new FileReader(json));  // 파일에서 문자 읽기

            String thisLine  = null;
            while ((thisLine = br.readLine()) != null) {           // 줄단위로 읽기
                //System.out.println(thisLine);

                jsonObject = (JSONObject)parser.parse(thisLine);
                doKeepAlive(serverUrl, prgmUrl, jsonObject.toJSONString());
            }

        } catch (IOException io) {
            logger.info(ExceptionConvert.TraceAllError(io));
        } catch (Exception e) {
            logger.info(ExceptionConvert.TraceAllError(e));
        } finally {
            if (br != null) try{br.close();}catch (IOException io){logger.info(ExceptionConvert.TraceAllError(io));}
        }

        return jsonObject;
    }

    public static void doKeepAlive(String serverUrl, String prgmUrl, String dvcJson) throws IOException {
        logger.info("###### Send KeepAlive Start ######");
        logger.info(dvcJson);

        URL url 			= new URL(serverUrl + prgmUrl);
        HttpURLConnection conn 			= (HttpURLConnection) url.openConnection();

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
            logger.info(line);
        }

        osw.close();
        br.close();
        logger.info("###### Send KeepAlive End ######");
    }

}
