package swing.demon.cmrctl.ctrl;

import swing.demon.cmrctl.vo.CtrlInfoVO;
import swing.demon.cmrctl.vo.DvcOp;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class DvcController {

    /**
     * 제어기 Control
     * @param vo
     * @return
     */
    public static String sendOp(CtrlInfoVO vo) {
        String	ret		= "S";
        int		timeout	= 3000;
        Socket	client	= new Socket();

        try{
            InetSocketAddress dvc = new InetSocketAddress(vo.getIpAddress(), FileChecker.props.getInt("dvc.port"));

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
        }catch(SocketTimeoutException e) {
            ret = "T"; // Time Out
//            e.printStackTrace();

            System.out.println(vo.getCmrId() + " : " + e.getMessage());
        }catch(Throwable e){
            ret = "E";
            System.out.println(vo.getCmrId() + ":" + e.getMessage());
//            e.printStackTrace();
        }finally{
            try {
                client.close();
            }catch(IOException e) {
                e.printStackTrace();
                System.out.println(vo.getCmrId() + " timeout error." + e.getMessage());
            }
        }

        return ret;
    }
}
