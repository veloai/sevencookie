//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package demon.cmrctl.src.main.java.com.credif.util.props;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Props {
    Map<String, Object> props = new HashMap();

    public Props() throws PropsException, IOException {
        File propsFile = this.openProps();
        this.readProps(propsFile);
    }

    public Props(String path) throws PropsException, IOException {
        File propsFile = this.openProps(path);
        this.readProps(propsFile);
    }

    File openProps() throws PropsException {
        return this.openProps("./config.properties");
    }

    File openProps(String path) throws PropsException {
        File ret = new File(path);
        if (!ret.exists()) {
            throw new PropsException("설정 파일이 없습니다.");
        } else if (!ret.canRead()) {
            throw new PropsException("설정 파일을 읽을 수 없습니다.");
        } else {
            return ret;
        }
    }

    void readProps(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = null;

        while((line = br.readLine()) != null) {
            if (!line.isEmpty()) {
                String[] temp = line.split("=");
                this.props.put(temp[0].trim(), temp[1].trim());
            }
        }

    }

    public Object get(String key) {
        return this.props.get(key);
    }

    public int getInt(String key) {
        return Integer.parseInt((String)this.props.get(key));
    }

    public String getString(String key) {
        return (String)this.props.get(key);
    }
}
