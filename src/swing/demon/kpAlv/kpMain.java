package swing.demon.kpAlv;

import swing.demon.util.props.Props;
import swing.demon.util.props.PropsException;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class kpMain {
    static Props props;
    final static SimpleDateFormat dFrmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public void run(String pth) {
        //System.out.println("## Start kpMain ##");

        //System.out.println("## get properties file ##");

        //time check
        //long startlogT = System.currentTimeMillis();
        try {
            props = new Props(pth);
        } catch (PropsException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

}
