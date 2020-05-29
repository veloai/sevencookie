package swing.demon.cmrctl;

import swing.demon.cmrctl.exception.DataValidationException;
import swing.demon.cmrctl.vo.CtrlInfoVO;
import swing.demon.cmrctl.vo.CtrlInputVO;
import swing.demon.cmrctl.vo.DvcOp;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.LogShow;
import swing.demon.util.props.Props;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControlCmr {
    final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmss");
    static String[] dvcIps = null;		//제어기 아이피
    static String[] dvcIds = null;		//제어기 아이디
    static boolean isLogShow = false;
    public static void processCtrl(String fileName, Props props, boolean isShow){

        dvcIps = props.getString("dvc.ip").trim().split(",");
        dvcIds = props.getString("dvc.id").trim().split(",");
        isLogShow = isShow;
        StringBuffer sf = new StringBuffer();

        //파일 정보, 데이터 set
        List<CtrlInfoVO> listVO = parseFile(fileName);

        String sido = props.getString("sido.cd");
        String sigungu = props.getString("sigungu.cd");
        //String resultFileName = props.getString("result.dir") + File.separator + sido+sigungu + "_" + dFrmt.format(new Date()) + ".txt";
        int port = props.getInt("dvc.port");
        boolean isDvcIdExist;
        for (CtrlInfoVO vo : listVO) {
            vo.setPort(port);
            isDvcIdExist = false;
            int size = dvcIps.length;
            for (int i = 0; i < size; i++) {
                if(vo.getDvcId().equals(dvcIds[i])) {
                    isDvcIdExist = true;
                    vo.setIpAddress(dvcIps[i]);
                }
            }
            if(!isDvcIdExist) {
                LogShow.logMessage(isLogShow, "------ "+ vo.getDvcId() +"설정했던 제어기 ID가 아닙니다. 다시 확인해주세요.------ ");
                continue;
            }
            //- TODO 디폴트 소프트웨어리셋
//            vo.setCmd(props.getString("default.reset") == null? "S" : props.getString("default.reset"));
            vo.setSido(sido);
            vo.setSigungu(sigungu);

            String resultStatus = sendOp(vo, props);
            vo.setSuccessYn(resultStatus.equalsIgnoreCase("S")? "Y" : "N");
            vo.setStatus(resultStatus.equalsIgnoreCase("S")? "N" : "F");
            String sndDate = dFrmt.format(new Date());
            vo.setApplyDt(sndDate);
            vo.setRegDt(sndDate);

            String thisResult = vo.getFormatting();
            sf.append(thisResult).append("\n");
            //System.out.println(thisResult);

            if (resultStatus.equalsIgnoreCase("S")) {
                LogShow.logMessage(isLogShow, "------ "+ vo.result() + " controlled successfully.");
            } else {
                LogShow.logMessage(isLogShow, "------ "+ vo.result() + " controlled failed.");
            }
        }

    }

    private static List<CtrlInfoVO> parseFile(String file){
        //parsing file
        List listCtrlInfoVO = new ArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            //csv file
            for (int i = 1; (line = br.readLine()) != null; i++) {
                try {
                    listCtrlInfoVO.add(parse(line));
                } catch (DataValidationException dve) {
                    LogShow.logMessage(isLogShow, "------ "+ ExceptionConvert.getMessage(dve));
                }

            }
        } catch (FileNotFoundException e) {
            LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
        } catch (IOException e) {
            LogShow.logMessage(isLogShow, ExceptionConvert.getMessage(e));
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

    public static String sendOp(CtrlInfoVO vo, Props props) {
        String	ret		= "S";
        int		timeout	= 3000;

        try(Socket client	= new Socket()) {
            InetSocketAddress dvc = new InetSocketAddress(vo.getIpAddress(), props.getInt("dvc.port"));

            client.connect(dvc, timeout);

            try(OutputStream sender = client.getOutputStream(); InputStream receiver = client.getInputStream();) {
                byte[] sData 	= new byte[9];
                sData[0] = 'S';
                sData[1] = 'O';
                sData[2] = 'D';
                sData[3] = 0x20;
                sData[4] = 0x01;
                sData[5] = vo.getCmd().equalsIgnoreCase("S") ? DvcOp.SOFT_RESET : DvcOp.HARD_RESET;
                sData[6] = 'E';
                sData[7] = 'O';
                sData[8] = 'D';

                sender.write(sData, 0, sData.length);

                byte[] rData = new byte[11];
                receiver.read(rData,0,11);
            }
        } catch (IllegalArgumentException e) {
            ret = "E";// 호스트 입력 잘못
            LogShow.logMessage(isLogShow, vo.getCmrId() + " : "+ ExceptionConvert.getMessage(e));
        } catch (SocketTimeoutException e) {
            ret = "T"; // Time Out
            LogShow.logMessage(isLogShow, vo.getCmrId() + " : "+ ExceptionConvert.getMessage(e));
        } catch (IOException e){
            ret = "E";
            LogShow.logMessage(isLogShow, vo.getCmrId() + " : "+ ExceptionConvert.getMessage(e));
        } catch (Exception e) {
            ret = "E";
            LogShow.logMessage(isLogShow, vo.getCmrId() + " : "+ ExceptionConvert.getMessage(e));
        }

        return ret;
    }

}
