package swing.demon.cmrctl;

import swing.demon.cmrctl.ctrl.CtrlInfoVO;
import swing.demon.cmrctl.ctrl.CtrlInputVO;
import swing.demon.cmrctl.ctrl.DvcController;
import swing.demon.cmrctl.ctrl.FileChecker;
import swing.demon.cmrctl.exception.DataValidationException;
import swing.demon.util.props.Props;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControlCmr {
    final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmss");
    static String[] dvcIps = null;		//제어기 아이피
    static String[] dvcIds = null;		//제어기 아이디

    public static void processCtrl(String fileName, Props props){

        dvcIps = props.getString("dvc.ip").trim().split(",");
        dvcIds = props.getString("dvc.id").trim().split(",");


        StringBuffer sf = new StringBuffer();

        //파일 정보, 데이터 set
        List<CtrlInfoVO> listVO = parseFile(fileName);

        String sido = props.getString("sido.cd");
        String sigungu = props.getString("sigungu.cd");
        //String resultFileName = props.getString("result.dir") + File.separator + sido+sigungu + "_" + dFrmt.format(new Date()) + ".txt";

        boolean isDvcIdExist = false;
        for (CtrlInfoVO vo : listVO) {
            vo.setPort(props.getInt("dvc.port"));
            isDvcIdExist = false;
            int size = dvcIps.length;
            for (int i = 0; i < size; i++) {
                if(vo.getDvcId().equals(dvcIds[i])) {
                    isDvcIdExist = true;
                    vo.setIpAddress(dvcIps[i]);
                }
            }
            if(!isDvcIdExist) {
                System.out.println("------ "+ vo.getDvcId() +"설정했던 제어기 ID가 아닙니다. 다시 확인해주세요.------ ");
                continue;
            }
            //- TODO 디폴트 소프트웨어리셋
//            vo.setCmd(props.getString("default.reset") == null? "S" : props.getString("default.reset"));
            vo.setSido(sido);
            vo.setSigungu(sigungu);

            String resultStatus = DvcController.sendOp(vo);
            vo.setSuccessYn(resultStatus.equalsIgnoreCase("S")? "Y" : "N");
            vo.setStatus(resultStatus.equalsIgnoreCase("S")? "N" : "F");
            String sndDate = dFrmt.format(new Date());
            vo.setApplyDt(sndDate);
            vo.setRegDt(sndDate);

            String thisResult = vo.getFormatting();
            sf.append(thisResult).append("\n");
            //System.out.println(thisResult);

            if (resultStatus.equalsIgnoreCase("S")) {
                System.out.println(vo.result() + " controlled successfully.");
            } else {
                System.out.println(vo.result() + " controlled failed.");
            }
        }

    }

    private static List<CtrlInfoVO> parseFile(String file){
        //parsing file
        List listCtrlInfoVO = new ArrayList();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            //csv file
            for (int i = 1; (line = br.readLine()) != null; i++){
                try {
                    listCtrlInfoVO.add(parse(line));
                } catch (DataValidationException dve) {
                    System.out.println(dve.getMessage());
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }finally {
            if (br !=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return listCtrlInfoVO;
    }

    private static CtrlInfoVO parse(String line) throws DataValidationException {
        CtrlInfoVO vo = new CtrlInfoVO();
        CtrlInputVO inVO = new CtrlInputVO(line);
        vo.setRegDt(inVO.getRegDt());
        vo.setApplyDt(inVO.getApplyDt());

        //- Status 값 [H:hardware reset|S: software reset]
        vo.setCmd(inVO.getStatus());
        vo.setReqSeq(inVO.getReqSeq());
        vo.setDvcId(inVO.getDvcId());
        return vo;
    }

}
