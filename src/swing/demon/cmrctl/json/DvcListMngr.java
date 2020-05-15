package swing.demon.cmrctl.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import swing.demon.util.props.Props;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class DvcListMngr {

    private static DvcListMngr singleton = new DvcListMngr();

    private static HashMap<String, String> dvcMap = new HashMap<String, String>();

    private DvcListMngr() {
        System.out.println("Singleton Instance Created..");
    }

    public static DvcListMngr getInstance(Props props) {
        if (dvcMap.size() == 0) {
            parseDvcList(props.getString("dvcfile.dir"));
        }

        return singleton;
    }

    private static void parseDvcList(String dvcFile) {
        JSONParser parser 	= new JSONParser();
        try {
            //- TODO 배포전 경로 확인
            JSONObject obj = (JSONObject) parser.parse(new FileReader(dvcFile));
            JSONArray dvcList = (JSONArray) obj.get("dvcList");
            for (int i =0; i < dvcList.size(); i++) {
                JSONObject dvc = (JSONObject) dvcList.get(i);
                JSONArray cmrList = (JSONArray)dvc.get("cmrList");
                for (int ii=0; ii < cmrList.size(); ii++) {
                    JSONObject	cmr	= 	(JSONObject) cmrList.get(ii);
                    dvcMap.put((String)cmr.get("crmId"), (String)cmr.get("ip"));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String searchIPbyDeviceId(String cmrId) {
        return dvcMap.get(cmrId);
    }
}
