package swing.demon.cmrctl.ctrl;

import swing.demon.cmrctl.exception.DataValidationException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import swing.demon.cmrctl.vo.CtrlInfoVO;
import swing.demon.cmrctl.vo.CtrlInputVO;
import swing.demon.util.props.Props;


public class ControlOprt {

//    final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmss");
//
//
//    public static void processCtrl(String fileName, Props props) {
//        //DvcListMngr dvcListMngr = DvcListMngr.getInstance(props);
//        StringBuilder sb = new StringBuilder();
//
//        //- 파일 정보 읽고, 아이피, [soft|hard]reset
//
//        List<CtrlInfoVO> listVO = parseFile(fileName);
//
//        String sido = props.getString("sido.cd");
//        String sigungu = props.getString("sigungu.cd");
//        String resultFileName = props.getString("result.dir") + File.separator + sido+sigungu + "_" + dFrmt.format(new Date()) + ".txt";
//
//        boolean isDvcIdExist = false;
//        for (CtrlInfoVO vo : listVO) {
//            vo.setPort(props.getInt("dvc.port"));
//
//            isDvcIdExist = false;
//            int size = FileChecker.dvcIds.length;
//            for (int i = 0; i < size; i++) {
//            	if(vo.getDvcId().equals(FileChecker.dvcIds[i])) {
//            		isDvcIdExist = true;
//            		vo.setIpAddress(FileChecker.dvcIps[i]);
//            	}
//			}
//            if(!isDvcIdExist) {
//            	System.out.println("------ "+ vo.getDvcId() +"설정했던 제어기 ID가 아닙니다. 다시 확인해주세요.------ ");
//            	continue;
//            }
//            //- TODO 디폴트 소프트웨어리셋
////            vo.setCmd(props.getString("default.reset") == null? "S" : props.getString("default.reset"));
//            vo.setSido(sido);
//            vo.setSigungu(sigungu);
//
//            String resultStatus = DvcController.sendOp(vo);
//            vo.setSuccessYn(resultStatus.equalsIgnoreCase("S")? "Y" : "N");
//            vo.setStatus(resultStatus.equalsIgnoreCase("S")? "N" : "F");
//            String sndDate = dFrmt.format(new Date());
//            vo.setApplyDt(sndDate);
//            vo.setRegDt(sndDate);
//
//            String thisResult = vo.getFormatting();
//            sb.append(thisResult).append("\n");
//            //System.out.println(thisResult);
//
//            if (resultStatus.equalsIgnoreCase("S")) {
//                System.out.println(vo.result() + " controlled successfully.");
//            } else {
//                System.out.println(vo.result() + " controlled failed.");
//            }
//        }
//        BufferedWriter fw = null;
//        try {
//    		  fw = new BufferedWriter(new FileWriter(resultFileName)); 
//    		  fw.write(sb.toString());
//    		  fw.flush();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        } finally {
//            if (fw != null) try{fw.close();}catch (Exception e) {}
//        }
//    }

//    private static List<CtrlInfoVO> parseFile(String file) {
//        List listCtrlInfoVO = new ArrayList();
//        BufferedReader br = null;
//        try { //예외처리 필수! 또는 throwsIOException해주기
//            //콘솔에서 입력 받을 경우
//            br = new BufferedReader(new FileReader(file));
//
//            //파일의 한 줄 한 줄 읽어서 출력한다.
//            String line = "";
//            for (int i = 1; (line = br.readLine()) != null; i++) {
//                try {
//                    listCtrlInfoVO.add(parse(line));
//                } catch (DataValidationException dve) {
//                    System.out.println(dve.getMessage());
//                }
//            }
//        }catch (FileNotFoundException fnfe){ //
//            fnfe.printStackTrace();
//        }catch (IOException e){ //
//            e.printStackTrace();
//            System.out.println(e.getMessage());
//        } finally {
//            if (br != null) try{br.close();}catch (Exception e) {}
//        }
//
//        return listCtrlInfoVO;
//    }
//
//    /**
//     * 123.11.22.33:20340,H
//     *
//     * @param line
//     * @return
//     */
//    private static CtrlInfoVO parse(String line) throws DataValidationException {
//        CtrlInfoVO vo = new CtrlInfoVO();
//        CtrlInputVO inVO = new CtrlInputVO(line);
//
//        vo.setRegDt(inVO.getRegDt());
//        vo.setApplyDt(inVO.getApplyDt());
//        //- Status 값 [H:hardware reset|S: software reset]
//        vo.setCmd(inVO.getStatus());
//        vo.setReqSeq(inVO.getReqSeq());
//        vo.setDvcId(inVO.getDvcId());
//        //vo.setIpAddress(DvcListMngr.searchIPbyDeviceId(inVO.getDvcId()));
//
////            if (cmd.equalsIgnoreCase("S") || cmd.equalsIgnoreCase("H")) {
////                vo.setCmd(cmd);
////            } else {
////
////                System.out.println("[Validation Error] must be S or H but " + cmd + ".");
////                throw new DataValidationException("[Validation Error] must be S or H but " + cmd + ".");
////            }
//
//        return vo;
//    }
}
